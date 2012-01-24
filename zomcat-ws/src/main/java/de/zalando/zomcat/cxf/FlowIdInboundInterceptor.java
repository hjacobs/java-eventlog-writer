package de.zalando.zomcat.cxf;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.apache.log4j.Logger;

import de.zalando.zomcat.flowid.FlowId;

/**
 * This interceptor managed the flow id from/to the http-header at RECEIVE time.
 *
 * @author  carsten.wolters
 */
public class FlowIdInboundInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final Logger LOG = Logger.getLogger(FlowIdInboundInterceptor.class);

    public static final String X_FLOW_ID = "x-flow-id";

    public FlowIdInboundInterceptor() {
        super(Phase.RECEIVE);
    }

    /**
     * Retrieves the session id from the servlet request of the webservice call. Store the id as flow object in thread
     * local to allow access to the flow object through the whole processing tree.
     */
    @Override
    public void handleMessage(final Message message) {
        if (isRequestor(message)) {

            // this is the answer of a client call. we already have a valid
            // flow id. don't do anything here.
            // if (LOG.isDebugEnabled()) {
            // LOG.debug("client answer (ignore http-header): owning flowId: " + FlowId.peekFlowId());
            // }
        } else {

            // we receive a message from outside. Clean all flowId contexts from
            // this thread (maybe from a previous aborted worker thread)
            FlowId.clear();

            // Try to get the flow id from the header or generate a new one:
            final HttpServletRequest httpServletRequest = (HttpServletRequest) message.get(
                    AbstractHTTPDestination.HTTP_REQUEST);
            if (httpServletRequest == null) {
                LOG.error("httpServletRequest must not be null.");
            } else {
                String flowId = httpServletRequest.getHeader(X_FLOW_ID);
                String from = "(from http-header)";
                if (flowId == null) {
                    flowId = httpServletRequest.getHeader(X_FLOW_ID.toUpperCase());
                    if (flowId == null) {
                        flowId = FlowId.generateFlowId();
                        from = "(from UUID-generator)";
                    }
                }

                // add a diagnostic message to the context:
                FlowId.pushFlowId(flowId);
                // if (LOG.isDebugEnabled()) {
                // LOG.debug("server receiver adds flowId " + from + " to context: " + FlowId.peekFlowId());
                // }
            }
        }
    }
}
