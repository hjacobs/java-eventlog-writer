package de.zalando.data.jpa.auditing.cxf;

import java.util.List;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import de.zalando.data.jpa.auditing.AuditorContextHolder;

public class AuditorAwareInterceptorsTest {

    private static final String ILLEGAL = "ILLEGAL";
    private static final String USER_NAME = "TESTER";

    @Test
    public void testInterceptors() {
        Message message = Mockito.mock(Message.class);
        AuthorizationPolicy authorizationPolicy = Mockito.mock(AuthorizationPolicy.class);
        Mockito.when(message.get(AuthorizationPolicy.class)).thenReturn(authorizationPolicy);
        Mockito.when(authorizationPolicy.getUserName()).thenReturn(USER_NAME);

        InterceptorBetween spy = new InterceptorBetween();

        List<? extends AbstractPhaseInterceptor> interceptors = Lists.newArrayList(new AuditorAwareInboundInterceptor(),
                spy, new AuditorAwareOutboundInterceptor());

        for (AbstractPhaseInterceptor<Message> interceptor : interceptors) {
            interceptor.handleMessage(message);
        }

        Assert.assertNotNull(spy.getAuditor());
        Assert.assertEquals(USER_NAME, spy.getAuditor());

    }

    private static class InterceptorBetween extends AbstractPhaseInterceptor<Message> {

        private static final Logger LOG = LoggerFactory.getLogger(InterceptorBetween.class);

        private String auditor = ILLEGAL;

        public InterceptorBetween() {
            super(Phase.INVOKE);
        }

        @Override
        public void handleMessage(final Message message) throws Fault {

            // we do nothing with message
            this.auditor = AuditorContextHolder.getContext().getAuditor();
            LOG.info("Auditor is " + this.auditor);
        }

        public String getAuditor() {
            return this.auditor;
        }

    }

}
