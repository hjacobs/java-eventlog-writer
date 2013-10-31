package de.zalando.zomcat.cxf;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.zomcat.ExecutionContext;

/**
 * This interceptor managed the flow id from/to the http-header at RECEIVE time.
 *
 * @author  carsten.wolters
 */
public class ExecutionContextInboundInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final Logger LOG = LoggerFactory.getLogger(ExecutionContextInboundInterceptor.class);

    public ExecutionContextInboundInterceptor() {
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
            // execution context. don't do anything
        } else {

            // we receive a message from outside. Clean all execution contexts from
            // this thread (maybe from a previous aborted worker thread)
            ExecutionContext.clear();

            // Try to get the flow id from the header or generate a new one:
            final HttpServletRequest httpServletRequest = (HttpServletRequest) message.get(
                    AbstractHTTPDestination.HTTP_REQUEST);
            if (httpServletRequest == null) {
                LOG.error("httpServletRequest must not be null.");
            } else {
                final String serializedExecutionContexts = HttpHeaders.EXECUTION_CONTEXT.get(httpServletRequest);
                if (serializedExecutionContexts != null) {
                    ExecutionContext.addSerialized(serializedExecutionContexts);
                }
            }
        }
    }
}
