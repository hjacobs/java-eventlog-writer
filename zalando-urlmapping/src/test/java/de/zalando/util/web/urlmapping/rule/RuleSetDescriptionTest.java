/**
 *
 */
package de.zalando.util.web.urlmapping.rule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import static com.google.common.collect.Lists.newArrayList;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import org.junit.Test;

import org.junit.internal.matchers.TypeSafeMatcher;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.base.Splitter;
import com.google.common.base.Splitter.MapSplitter;
import com.google.common.io.OutputSupplier;

import de.zalando.util.web.urlmapping.RuleContext;
import de.zalando.util.web.urlmapping.UrlMappingException;

public class RuleSetDescriptionTest {

    private static final MapSplitter MAP_SPLITTER = Splitter.on('&').withKeyValueSeparator(Splitter.on('='));

    @Test
    public void testSerialization() {
        final List<RuleSetDescription> original = createRules();

        final StringWriter stringWriter = new StringWriter();
        RuleSetDescription.serialize(original, new OutputSupplier<Writer>() {

                @Override
                public Writer getOutput() throws IOException {
                    return stringWriter;
                }
            });

        final String serialized = stringWriter.toString();
        final List<RuleSetDescription> deserialized = RuleSetDescription.deserialize(new ByteArrayInputStream(
                    serialized.getBytes()));
        assertEquals(original, deserialized);
    }

    private List<RuleSetDescription> createRules() {
        final List<RuleSetDescription> original = newArrayList();

        original.add(voucherSuccess());
        original.add(orderCancelled());
        original.add(adminLogin());
        original.add(someStupidPath());
        original.add(recoImage());
        original.add(updater());
        return original;
    }

    @Test
    public void testRuleContext() throws Exception {
        final List<RuleSetDescription> rules = createRules();
        final RuleContext.Builder builder = RuleContext.builder();
        for (final RuleSetDescription rule : rules) {
            rule.register(builder);
        }

        final RuleContext ruleContext = builder.build();
        assertThat(ruleContext,
            maps("kasse/geschenkgutscheine/bestellt/123", "/checkout/VoucherFinal.action?success=&id=123"));
        assertThat(ruleContext, maps("kasse/geschenkgutscheine/bestellt", "/checkout/VoucherFinal.action?success="));
        assertThat(ruleContext, maps("kasse/geschenkgutscheine/bestellt/", "/checkout/VoucherFinal.action?success="));
        assertThat(ruleContext,
            maps("admin/login?customer=foo@foo.foo&admin=bar@bar.bar",
                "/customer/AdminLogin.action?emailCombination=bar@bar.bar:foo@foo.foo"));
        assertThat(ruleContext,
            maps("katalog/recoimage.jpg?sku=abcd-1234&pos=TOP",
                "/reco/DynaReco.action?image=&sku=abcd-1234&position=TOP"));
        assertThat(ruleContext, maps("updater", "/Updater.action"));
        assertThat(ruleContext, maps("updater/foo", "/Updater.action?foo="));

    }

    @Test
    public void testRuleContextFromFile() throws Exception {
        final InputStream stream = RuleSetDescriptionTest.class.getResourceAsStream("/urlmappings.txt");
        final List<RuleSetDescription> rules = RuleSetDescription.deserialize(stream);
        final RuleContext.Builder builder = RuleContext.builder();
        for (final RuleSetDescription rule : rules) {
            rule.register(builder);
        }

        final RuleContext ruleContext = builder.build();
        assertThat(ruleContext,
            maps("kasse/geschenkgutscheine/bestellt/123", "/checkout/VoucherFinal.action?success=&id=123"));
        assertThat(ruleContext, maps("kasse/geschenkgutscheine/bestellt", "/checkout/VoucherFinal.action?success="));
        assertThat(ruleContext, maps("kasse/geschenkgutscheine/bestellt/", "/checkout/VoucherFinal.action?success="));
        assertThat(ruleContext,
            maps("admin/login?customer=foo@foo.foo&admin=bar@bar.bar",
                "/customer/AdminLogin.action?emailCombination=bar@bar.bar:foo@foo.foo"));

        // testing the rule 'RULE:link.springcontroller.test'
        assertThat(ruleContext, maps("wham/bam", "/foo/bar/bam/phleem"));

    }

    @Test
    public void testRuleContextFromFileWithSpring() throws Exception {
        final InputStream stream = RuleSetDescriptionTest.class.getResourceAsStream("/urlmappings2.txt");
        final List<RuleSetDescription> rules = RuleSetDescription.deserialize(stream);
        final RuleContext.Builder builder = RuleContext.builder();
        for (final RuleSetDescription rule : rules) {
            rule.register(builder);
        }

        final RuleContext ruleContext = builder.build();

        // testing the rule 'RULE:link.springcontroller.test'
        // assertThat(ruleContext, maps("wham/bam", "/foo/bar/bam/phleem"));
        assertThat(ruleContext, maps("wham/bam?id=10", "/foo/bar/bam/phleem?id=10"));

    }

    @Test
    public void testRuleContextWithRuleActivationPredicate() throws Exception {
        final InputStream stream = RuleSetDescriptionTest.class.getResourceAsStream("/urlmappings2.txt");
        final List<RuleSetDescription> rules = RuleSetDescription.deserialize(stream);
        final RuleContext.Builder builder = RuleContext.builder();
        for (final RuleSetDescription rule : rules) {
            rule.register(builder);
        }

        final RuleContext ruleContext = builder.build();

        // testing the rule 'RULE:link.springcontroller.test'
        // assertThat(ruleContext, maps("wham/bam", "/foo/bar/bam/phleem"));

        RuleTargetSwitchDelegator.Builder switchbuilder = new RuleTargetSwitchDelegator.Builder();

        switchbuilder.add("*", ForwardMappingRule.TargetType.STRIPES);
        switchbuilder.add("link.springcontroller.test", ForwardMappingRule.TargetType.SPRING);

        assertThat(ruleContext, maps("wham/bam?id=10", "/foo/bar/fallback?id=10", switchbuilder.build()));

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
            for (final Entry<String, String> paramEntry : parameters.entrySet()) {
                request.addParameter(paramEntry.getKey(), paramEntry.getValue());
            }

        }

        return request;
    }

    private RuleSetDescription updater() {
        final RuleSetDescription updater = new RuleSetDescription("link.test.updater");

        RuleMappingTarget target = new RuleMappingTarget();
        target.setTargetType(ForwardMappingRule.TargetType.STRIPES);
        target.setTargetUrl("/Updater.action");
        updater.addRuleMappingTarget(target);

        updater.addPathKey("", true);
        updater.addPath("updater");

        return updater;
    }

    private RuleSetDescription recoImage() {
        final RuleSetDescription recoImage = new RuleSetDescription("link.dynareco.image.sku");

        RuleMappingTarget target = new RuleMappingTarget();
        target.setTargetType(ForwardMappingRule.TargetType.STATIC);
        target.setTargetUrl("/reco/DynaReco.action?image=");
        recoImage.addRuleMappingTarget(target);

        recoImage.addRequestParameter("sku", "sku").addRequestParameter("pos", "position");
        recoImage.addPath("katalog/recoimage.jpg");
        return recoImage;
    }

    private RuleSetDescription adminLogin() {
        final RuleSetDescription adminLogin = new RuleSetDescription("link.admin.login.link");

        RuleMappingTarget target = new RuleMappingTarget();
        target.setTargetType(ForwardMappingRule.TargetType.STRIPES);
        target.setTargetUrl("/customer/AdminLogin.action");
        adminLogin.addRuleMappingTarget(target);

        adminLogin.addAggregationParameter("emailCombination", ':', Arrays.asList("admin", "customer"));
        adminLogin.addPath("admin/login");
        return adminLogin;
    }

    private RuleSetDescription orderCancelled() {
        final RuleSetDescription cancelled = new RuleSetDescription("link.checkout.canceled");

        RuleMappingTarget target = new RuleMappingTarget();
        target.setTargetType(ForwardMappingRule.TargetType.STRIPES);
        target.setTargetUrl("/checkout/Final.action?cancel=");
        cancelled.addRuleMappingTarget(target);

        cancelled.addOptionalPathParameter("id");
        cancelled.addPath("kasse/abgebrochen");
        cancelled.addPath("paiement/annule");
        return cancelled;
    }

    private RuleSetDescription someStupidPath() {
        final RuleSetDescription cancelled = new RuleSetDescription("link.some.stupid.path");

        RuleMappingTarget target = new RuleMappingTarget();
        target.setTargetType(ForwardMappingRule.TargetType.STRIPES);
        target.setTargetUrl("/foo/bar/{baz}/phleem");
        cancelled.addRuleMappingTarget(target);

        cancelled.addPathSegmentParameter("baz", 2);
        cancelled.addPath("flapp/flupp");
        cancelled.addPath("wapp/wupp");
        return cancelled;
    }

    private RuleSetDescription voucherSuccess() {
        final RuleSetDescription voucherSuccess = new RuleSetDescription("link.checkout.vouchers.success");

        RuleMappingTarget target = new RuleMappingTarget();
        target.setTargetType(ForwardMappingRule.TargetType.STRIPES);
        target.setTargetUrl("/checkout/VoucherFinal.action?success=");
        voucherSuccess.addRuleMappingTarget(target);

        voucherSuccess.addOptionalPathParameter("id");
        voucherSuccess.addPath("kasse/geschenkgutscheine/bestellt");
        voucherSuccess.addPath("checkout/giftvouchers/ordered");
        voucherSuccess.addPath("kasa/hediye-cekleri/bestellt");

        return voucherSuccess;
    }

}
