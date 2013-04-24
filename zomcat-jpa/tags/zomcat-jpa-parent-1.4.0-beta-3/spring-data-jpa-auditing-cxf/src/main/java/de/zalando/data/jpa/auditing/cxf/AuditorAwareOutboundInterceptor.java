package de.zalando.data.jpa.auditing.cxf;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.data.jpa.auditing.AuditorContextHolder;

/**
 * An Outbound-Interceptor that clears the context.
 *
 * @author  jbellmann
 */
public class AuditorAwareOutboundInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Logger LOG = LoggerFactory.getLogger(AuditorAwareOutboundInterceptor.class);

    public AuditorAwareOutboundInterceptor() {
        super(Phase.POST_INVOKE);
    }

    @Override
    public void handleMessage(final Message message) throws Fault {
        AuditorContextHolder.clearContext();
        LOG.debug("AuditorContext cleared");
    }
}
