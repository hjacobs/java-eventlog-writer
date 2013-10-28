package de.zalando.util.web.urlmapping.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.io.InputStream;

import java.util.List;
import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import org.junit.Test;

import org.junit.internal.matchers.TypeSafeMatcher;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.base.Splitter;

import de.zalando.util.web.urlmapping.RuleContext;
import de.zalando.util.web.urlmapping.UrlMappingException;

/**
 * Created with IntelliJ IDEA. User: abaresel Date: 10/17/13 Time: 10:25 AM To change this template use File | Settings
 * | File Templates.
 */
public class ForwardMappingRuleTest {

    private static final Splitter.MapSplitter MAP_SPLITTER = Splitter.on('&').withKeyValueSeparator(Splitter.on('='));

    @Test
    public void testMapping() throws Exception {
        final InputStream stream = RuleSetDescriptionTest.class.getResourceAsStream("/urlmappings3.txt");
        final List<RuleSetDescription> rules = RuleSetDescription.deserialize(stream);
        final RuleContext.Builder builder = RuleContext.builder();
        for (final RuleSetDescription rule : rules) {
            rule.register(builder);
        }

        final RuleContext ruleContext = builder.build();

        assertThat(ruleContext,
            maps("benutzerkonto/home-pup-auswahl/", "/customer/OnlineRetoure.action?showPickupChooseStep="));

        assertThat(ruleContext, maps("benutzerkonto/home-pup-auswahl/1/", "/customer/OnlineRetoure.action?page=1"));

    }

    private Matcher<RuleContext> maps(final String incoming, final String to) {
        return maps(incoming, to, RuleTargetSwitchDelegator.DEFAULT, "");
    }

    private Matcher<RuleContext> maps(final String incoming, final String to,
            final RuleTargetSwitchDelegator delegator) {
        return maps(incoming, to, delegator, "");
    }

    private Matcher<RuleContext> maps(final String incoming, final String to, final RuleTargetSwitchDelegator delegator,
            final String requestMethod) {
        return new TypeSafeMatcher<RuleContext>() {

            @Override
            public void describeTo(final Description description) {

                description.appendText("Expect a mapping from ").appendValue(incoming).appendText(" to ").appendValue(
                    to);
            }

            @Override
            public boolean matchesSafely(final RuleContext context) {
                final MockHttpServletResponse response = new MockHttpServletResponse();
                try {
                    if (context.mapRequest(request(incoming, requestMethod), response, delegator)) {
                        assertEquals(to, response.getForwardedUrl());
                        return true;
                    }
                } catch (final UrlMappingException e) {
                    e.printStackTrace();
                    fail();
                }

                return false;
            }
        };
    }

    private MockHttpServletRequest request(final String url) {
        return request(url, "");
    }

    private MockHttpServletRequest request(final String url, final String method) {

        final MockHttpServletRequest request = new MockHttpServletRequest(method, "");
        request.setServerName("www.zalando.de");

        final int queryOffset = url.indexOf('?');
        if (queryOffset < 0) {
            request.setRequestURI(url);
        } else {
            request.setRequestURI(url.substring(0, queryOffset));

            final String queryString = url.substring(queryOffset + 1);
            request.setQueryString(queryString);

            final Map<String, String> parameters = MAP_SPLITTER.split(queryString);
            for (final Map.Entry<String, String> paramEntry : parameters.entrySet()) {
                request.addParameter(paramEntry.getKey(), paramEntry.getValue());
            }

        }

        return request;
    }
}
