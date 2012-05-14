package de.zalando.util.web.urlmapping.rule;

import static com.google.common.collect.Lists.asList;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

import de.zalando.util.web.urlmapping.MappingContext;
import de.zalando.util.web.urlmapping.domain.MappingConstants;
import de.zalando.util.web.urlmapping.util.Helper;

public class RuleBucket {

    public static final Function<Builder, RuleBucket> BUILDER_FUNCTION =
        new Function<RuleBucket.Builder, RuleBucket>() {

            @Override
            public RuleBucket apply(final Builder input) {
                return input.build();
            }
        };

    private final Node rootNode;

    private RuleBucket(final Node rootNode) {
        this.rootNode = rootNode;
    }

    private static class Node {

        private static final Comparator<MappingRule> COMPARATOR = new Ordering<MappingRule>() {

            @Override
            public int compare(final MappingRule left, final MappingRule right) {
                return ComparisonChain.start().compare(getRuleParams(left), getRuleParams(right))
                                      .compare(left.getId(), right.getId()).result();
            }

            private int getRuleParams(final MappingRule mappingRule) {
                if (mappingRule instanceof ForwardMappingRule) {
                    return -((ForwardMappingRule) mappingRule).countHandlers();
                }

                return 1;
            }
        };

        private Node(final Map<String, Node> children, final Collection<MappingRule> rules) {
            this.children = ImmutableSortedMap.copyOf(children);
            this.rules = ImmutableSortedSet.copyOf(COMPARATOR, rules);
        }

        private final Map<String, Node> children;
        private final Set<MappingRule> rules;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private static final Function<MutableNode, Node> TO_IMMUTABLE;

        static {
            TO_IMMUTABLE = new Function<MutableNode, Node>() {

                @Override
                public Node apply(final MutableNode mutableNode) {
                    return new Node(ImmutableMap.copyOf(Maps.transformValues(mutableNode.children, TO_IMMUTABLE)),
                            mutableNode.rules);
                }
            };
        }

        private static class MutableNode {
            private final Map<String, MutableNode> children = newHashMap();
            private final List<MappingRule> rules = newArrayList();

        }

        private final MutableNode rootNode = new MutableNode();

        private Builder() { }

        public RuleBucket build() {
            return new RuleBucket(TO_IMMUTABLE.apply(rootNode));
        }

        public Builder addRule(final MappingRule rule, final String firstPathItem, final String... morePathItems) {
            return addRule(rule, asList(firstPathItem, morePathItems));
        }

        public Builder addRule(final MappingRule rule, final Iterable<String> path) {
            MutableNode node = rootNode;
            for (final String pathItem : path) {
                if (node.children.containsKey(pathItem)) {
                    node = node.children.get(pathItem);
                } else {
                    final MutableNode tmpNode = new MutableNode();
                    node.children.put(pathItem, tmpNode);
                    node = tmpNode;
                }
            }

            node.rules.add(rule);

            return this;
        }

        public String wildCard() {
            return MappingConstants.WILDCARD;
        }

        public Builder addRootRule(final MappingRule rule) {
            return addRule(rule, Collections.<String>emptySet());
        }

    }

    public MappingRule findRule(final MappingContext mappingContext) {
        Node node = rootNode;
        while (mappingContext.hasMorePathSegments()) {
            if (node.children.isEmpty()) {

                // this should never happen because all rules in a bucket should have the same depth which is the same
                // as mappingContext.getNumberOfSegments()
                return null;
            }

            final String pathSegment = mappingContext.nextPathSegment();

            // check for named segment or for wildcard
            node = Helper.firstExistingValue(node.children, pathSegment, MappingConstants.WILDCARD);
            if (node == null) {

                // neither was found
                return null;
            } else {
                mappingContext.consumePathSegment();
            }
        }

        for (final MappingRule rule : node.rules) {
            if (rule.appliesTo(mappingContext)) {
                return rule;
            }
        }

        return null;
    }
}
