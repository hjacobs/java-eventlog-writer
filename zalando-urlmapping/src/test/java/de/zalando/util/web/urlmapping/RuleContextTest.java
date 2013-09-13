package de.zalando.util.web.urlmapping;

import static org.hamcrest.core.IsNot.not;

import static org.junit.Assert.assertThat;

import static de.zalando.util.web.urlmapping.domain.MappingConstants.OPTIONAL_WILDCARD;
import static de.zalando.util.web.urlmapping.domain.MappingConstants.WILDCARD;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import org.junit.Assert;
import org.junit.Test;

import org.junit.internal.matchers.TypeSafeMatcher;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;

import de.zalando.util.web.urlmapping.rule.NoOpMappingRule;
import de.zalando.util.web.urlmapping.rule.RuleActivationPredicate;
import de.zalando.util.web.urlmapping.util.Delimiter;

public class RuleContextTest {
    private static final Joiner SLASH_JOINER = Delimiter.SLASH.joiner();

    @Test
    public void testRuleContext() throws Exception {
        final RuleContext.Builder builder = RuleContext.builder();
        final String matchParam = "foo";
        builder.addRule("", paramMatchingRule(matchParam));
        builder.addRule("baz/phleem", paramMatchingRule(matchParam));
        builder.addRule("bar/baz", alwaysMatchRule());
        builder.addRule(SLASH_JOINER.join("foo", "bar", WILDCARD), alwaysMatchRule());
        builder.addRule(SLASH_JOINER.join("bar", WILDCARD, OPTIONAL_WILDCARD), alwaysMatchRule());

        final RuleContext ruleContext = builder.build();
        assertThat(ruleContext, not(hasAMappingFor(request(""))));
        assertThat(ruleContext, hasAMappingFor(request("", "foo")));
        assertThat(ruleContext, not(hasAMappingFor(request("baz/phleem"))));
        assertThat(ruleContext, not(hasAMappingFor(request("baz/phleem", "bar"))));
        assertThat(ruleContext, hasAMappingFor(request("baz/phleem", "foo")));
    }

    private Matcher<RuleContext> hasAMappingFor(final HttpServletRequest request) {
        return new TypeSafeMatcher<RuleContext>() {

            @Override
            public void describeTo(final Description description) {
                description.appendText("RuleContext with a matching rule for ").appendValue(request);
            }

            @Override
            public boolean matchesSafely(final RuleContext ruleContext) {
                try {
                    return ruleContext.mapRequest(request, new MockHttpServletResponse(),
                            RuleActivationPredicate.ALL_ACTIVE);
                } catch (final UrlMappingException e) {
                    Assert.fail(Throwables.getStackTraceAsString(e));
                    return false;                                                       // unreachable code
                }
            }
        };
    }

    private HttpServletRequest request(final String path, final String... parameters) {
        final MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath(path);
        for (final String param : parameters) {
            request.addParameter(param, "");
        }

        return request;
    }

    private NoOpMappingRule alwaysMatchRule() {
        return new NoOpMappingRule() {
            private static final long serialVersionUID = 6145886904362515778L;

            @Override
            public String getId() {

                return "brrr";
            }

            @Override
            public Integer getPriority() {
                return null;
            }
        };
    }

    private NoOpMappingRule paramMatchingRule(final String matchParam) {
        return new NoOpMappingRule() {
            @Override
            public String getId() {

                return "flapp";
            }

            @Override
            public Integer getPriority() {
                return null;
            }

            private static final long serialVersionUID = -3356043622642602886L;

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean appliesTo(final MappingContext mappingContext) {
                @SuppressWarnings("unchecked") // this is documented
                final Map<String, String[]> parameterMap = mappingContext.getRequest().getParameterMap();
                return parameterMap.containsKey(matchParam);
            }
        };
    }

}
