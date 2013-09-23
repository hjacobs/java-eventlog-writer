package de.zalando.util.web.urlmapping;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ContiguousSet.create;
import static com.google.common.collect.DiscreteDomain.integers;
import static com.google.common.collect.Iterables.limit;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.newHashMap;

import static de.zalando.util.web.urlmapping.domain.MappingConstants.ALLOWED_CHARS;
import static de.zalando.util.web.urlmapping.domain.MappingConstants.ALL_WILDCARDS;
import static de.zalando.util.web.urlmapping.util.Delimiter.SLASH;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Range;

import de.zalando.util.web.urlmapping.domain.MappingConstants;
import de.zalando.util.web.urlmapping.rule.MappingRule;
import de.zalando.util.web.urlmapping.rule.RuleBucket;
import de.zalando.util.web.urlmapping.rule.RuleTargetSwitchDelegator;

public class RuleContext {

    public enum TokenProcessor implements Function<String, String> {
        INSTANCE {

            @Override
            public String apply(final String input) {
                if (ALL_WILDCARDS.contains(input)) {
                    return "*";
                }

                checkArgument(ALLOWED_CHARS.matchesAllOf(input),
                    "Rule token '%s' contains illegal characters, only a-z, A-Z, 0-9, ._- are allowed", input);
                return input;
            }
        }

    }

    private RuleContext(final Map<Integer, RuleBucket> buckets) {
        this.buckets = ImmutableMap.copyOf(buckets);
    }

    private final Map<Integer, RuleBucket> buckets;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private static final Set<String> EMPTY_PATHS = ImmutableSet.of("/", "");
        private static final Splitter SPLITTER = Splitter.on('/').trimResults();

        private Builder() { }

        private final Map<Integer, RuleBucket.Builder> builders = newHashMap();

        public Builder addRule(final String path, final MappingRule rule) {
            int min = 0;
            int max = 0;
            final String trimmedPath = SLASH.matcher().trimAndCollapseFrom(path, '/');
            final Iterable<String> pathItems;
            if (EMPTY_PATHS.contains(trimmedPath)) {
                pathItems = ImmutableList.of();
            } else {
                pathItems = SPLITTER.split(trimmedPath);
            }

            boolean hasOptional = false;
            for (final String pathItem : pathItems) {
                max++;
                if (MappingConstants.OPTIONAL_WILDCARD.equals(pathItem)) {
                    hasOptional = true;
                } else {
                    checkArgument(!hasOptional, "Rule path %s contains non-optional elements after optional ones",
                        path);
                    min++;
                }
            }

            final Iterable<String> transformedPath;
            if (Iterables.isEmpty(pathItems)) {
                transformedPath = ImmutableSet.of();
            } else {
                transformedPath = transform(pathItems, TokenProcessor.INSTANCE);
            }

            for (final Integer cardinality : create(Range.closed(min, max), integers())) {
                RuleBucket.Builder builder;
                if (builders.containsKey(cardinality)) {
                    builder = builders.get(cardinality);
                } else {
                    builder = RuleBucket.builder();
                    builders.put(cardinality, builder);
                }

                builder.addRule(rule, limit(transformedPath, cardinality));
            }

            return this;
        }

        public RuleContext build() {
            return new RuleContext(Maps.transformValues(builders, RuleBucket.BUILDER_FUNCTION));
        }
    }

    public boolean mapRequest(final HttpServletRequest request, final HttpServletResponse response,
            final RuleTargetSwitchDelegator ruleTargetSwitch) throws UrlMappingException {
        final MappingContext mappingContext = MappingContext.create(request, response, ruleTargetSwitch);
        final int segments = mappingContext.getNumberOfSegments();

        // we keep one bucket per segment length to make sure we have a constant lookup time
        final RuleBucket ruleBucket = buckets.get(segments);
        if (ruleBucket != null) {
            final MappingRule rule = ruleBucket.findRule(mappingContext);
            if (rule != null) {
                rule.apply(mappingContext);
                return true;
            }

        }

        return false;
    }

}
