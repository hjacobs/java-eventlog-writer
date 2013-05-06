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
import de.zalando.util.web.urlmapping.RuleContext.Builder;
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
        final Builder builder = RuleContext.builder();
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
        final Builder builder = RuleContext.builder();
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
        assertThat(ruleContext, maps("wham/bam", "/foo/bar/bam/phleem"));

    }

    private Matcher<RuleContext> maps(final String incoming, final String to) {
        return new TypeSafeMatcher<RuleContext>() {

            @Override
            public void describeTo(final Description description) {

                description.appendText("a mapping from ").appendValue(incoming).appendText(" to ").appendValue(to);
            }

            @Override
            public boolean matchesSafely(final RuleContext context) {
                final MockHttpServletResponse response = new MockHttpServletResponse();
                try {
                    if (context.mapRequest(request(incoming), response)) {
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

        final MockHttpServletRequest request = new MockHttpServletRequest();
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
        updater.setTargetUrl("/Updater.action");
        updater.addPath("updater");
        updater.addPathKey("", true);
        return updater;
    }

    private RuleSetDescription recoImage() {
        final RuleSetDescription recoImage = new RuleSetDescription("link.dynareco.image.sku");
        recoImage.setTargetUrl("/reco/DynaReco.action?image=");
        recoImage.addPath("katalog/recoimage.jpg");
        recoImage.addRequestParameter("sku", "sku").addRequestParameter("pos", "position");
        return recoImage;
    }

    private RuleSetDescription adminLogin() {
        final RuleSetDescription adminLogin = new RuleSetDescription("link.admin.login.link");
        adminLogin.setTargetUrl("/customer/AdminLogin.action");
        adminLogin.addPath("admin/login");
        adminLogin.addAggregationParameter("emailCombination", ':', Arrays.asList("admin", "customer"));
        return adminLogin;
    }

    private RuleSetDescription orderCancelled() {
        final RuleSetDescription cancelled = new RuleSetDescription("link.checkout.canceled");
        cancelled.setTargetUrl("/checkout/Final.action?cancel=");
        cancelled.addPath("kasse/abgebrochen");
        cancelled.addPath("paiement/annule");
        cancelled.addOptionalPathParameter("id");
        return cancelled;
    }

    private RuleSetDescription someStupidPath() {
        final RuleSetDescription cancelled = new RuleSetDescription("link.some.stupid.path");
        cancelled.setTargetUrl("/foo/bar/{baz}/phleem");
        cancelled.addPath("flapp/flupp");
        cancelled.addPath("wapp/wupp");
        cancelled.addPathSegmentParameter("baz", 2);
        return cancelled;
    }

    private RuleSetDescription voucherSuccess() {
        final RuleSetDescription voucherSuccess = new RuleSetDescription("link.checkout.vouchers.success");
        voucherSuccess.setTargetUrl("/checkout/VoucherFinal.action?success=");
        voucherSuccess.addPath("kasse/geschenkgutscheine/bestellt");
        voucherSuccess.addPath("checkout/giftvouchers/ordered");
        voucherSuccess.addPath("kasa/hediye-cekleri/bestellt");
        voucherSuccess.addOptionalPathParameter("id");
        return voucherSuccess;
    }

}
