package de.zalando.util.web.urlmapping.rule;

import static java.util.Collections.emptyMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.collect.ImmutableMap;

import de.zalando.util.web.urlmapping.MappingContext;
import de.zalando.util.web.urlmapping.domain.MappingConstants;
import de.zalando.util.web.urlmapping.rule.RuleBucket.Builder;

public class RuleBucketTest {

    @Test
    public void testFindFlatRule() throws Exception {
        final Builder builder = RuleBucket.builder();
        builder.addRootRule(flatRule("foo"));
        builder.addRootRule(flatRule("bar"));

        final RuleBucket bucket = builder.build();
        assertNotNull(bucket.findRule(paramMapping("", "foo", "")));
        assertNotNull(bucket.findRule(paramMapping("", "bar", "")));
        assertNull(bucket.findRule(paramMapping("", "baz", "")));

    }

    private MappingContext paramMapping(final String path, final String... paramKeyValues) {
        checkArgument((paramKeyValues.length % 2) == 0, "Params must be supplied in key / value pairs!");

        Map<String, String> params;
        if (paramKeyValues.length == 0) {
            params = emptyMap();
        } else {
            final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
            for (int i = 0; i < paramKeyValues.length; i += 2) {
                builder.put(paramKeyValues[i], paramKeyValues[i + 1]);
            }

            params = builder.build();
        }

        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath(path);
        request.setParameters(params);
        return MappingContext.create(request, new MockHttpServletResponse());
    }

    private MappingRule flatRule(final String paramName) {
        return new NoOpMappingRule() {

            private static final long serialVersionUID = -7621106177772996186L;

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean appliesTo(final MappingContext mappingContext) {
                return mappingContext.getRequest().getParameterMap().containsKey(paramName);
            }

            @Override
            public String getId() {
                return paramName;
            }
        };
    }

    @Test
    public void testFindHierachicRule() throws Exception {
        final Builder builder = RuleBucket.builder();
        final MappingRule rule = new NoOpMappingRule() {

            private static final long serialVersionUID = 5354782919532682129L;

            @Override
            public String getId() {
                return "noop";
            }
        };

        final String wc = MappingConstants.WILDCARD;
        builder.addRule(rule, "foo", "bar", "phleem", "brr");
        builder.addRule(rule, "foo", "bar", "baz", wc);
        builder.addRule(rule, "foo", wc, wc, "bing");

        final RuleBucket ruleBucket = builder.build();
        assertNull(ruleBucket.findRule(context("")));
        assertNull(ruleBucket.findRule(context("foo")));
        assertNull(ruleBucket.findRule(context("foo/bar")));
        assertNull(ruleBucket.findRule(context("foo/bar/phleem")));
        assertNotNull(ruleBucket.findRule(context("foo/bar/phleem/brr")));
        assertNotNull(ruleBucket.findRule(context("/foo/bar/phleem/brr")));
        assertNotNull(ruleBucket.findRule(context("/foo/bar/phleem/brr/")));

        // trailing slashes are trimmed
        assertNotNull(ruleBucket.findRule(context("/foo/bar/phleem/brr/////")));

        // but double slashes inside are preserved
        assertNull(ruleBucket.findRule(context("/foo/bar/phleem//brr")));
        assertNull(ruleBucket.findRule(context("/foo/bar/phleem/brr/1")));

    }

    private MappingContext context(final String path) {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(path);
        return MappingContext.create(request, new MockHttpServletResponse());
    }

}
