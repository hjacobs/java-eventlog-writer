package de.zalando.data.jpa.auditing.cxf;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import de.zalando.data.jpa.auditing.AuditorContextHolder;

/**
 * An InboundInterceptor that tries to set the username to a Context.
 *
 * @author  jbellmann
 */
public class AuditorAwareInboundInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Logger LOG = LoggerFactory.getLogger(AuditorAwareInboundInterceptor.class);

    public AuditorAwareInboundInterceptor() {
        super(Phase.PRE_INVOKE);
    }

    /**
     * Invoked on Exception in Service-Invocation.
     */
    @Override
    public void handleFault(final Message message) {
        LOG.info("Seems an error occurred. Clear context.");
        AuditorContextHolder.clearContext();
        LOG.info("AuditorContext cleared.");
    }

    @Override
    public void handleMessage(final Message message) throws Fault {
        LOG.info("Set Auditor to Context.");
        AuditorContextHolder.getContext().setAuditor(getUsernameFromMessage(message));
        LOG.info("Auditor was set to Context.");
    }

    protected String getUsernameFromMessage(final Message message) {
        final AuthorizationPolicy policy = message.get(AuthorizationPolicy.class);
        String result = "UNDEFINED";
        if (policy != null) {

            // get user name from authorization HTTP header
            String username = policy.getUserName();
            if (!Strings.isNullOrEmpty(username)) {
                result = username;
            }
        }

        LOG.debug("Auditor is " + result);
        return result;
    }
}
