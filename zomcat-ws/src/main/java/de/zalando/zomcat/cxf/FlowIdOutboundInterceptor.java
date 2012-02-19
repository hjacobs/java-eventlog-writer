package de.zalando.zomcat.cxf;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.zomcat.flowid.FlowId;

/**
 * This interceptor managed the flow id from/to the http-header at PRE_STREAM time.
 *
 * @author  carsten.wolters
 */
public class FlowIdOutboundInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final Logger LOG = LoggerFactory.getLogger(FlowIdOutboundInterceptor.class);

    public FlowIdOutboundInterceptor() {
        super(Phase.PRE_STREAM);
    }

    /**
     * Retrieves the session id from the servlet request of the webservice call. Store the id as flow object in thread
     * local to allow access to the flow object through the whole processing tree.
     */
    @Override
    public void handleMessage(final Message message) {

        // add the flow id to the internal message structure, too:
        @SuppressWarnings("unchecked")
        Map<String, List<String>> map = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);

        if (map == null) {
            map = new TreeMap<String, List<String>>();
            message.put(Message.PROTOCOL_HEADERS, map);
        }

        map.put(FlowIdInboundInterceptor.X_FLOW_ID, Arrays.asList(FlowId.peekFlowId()));

        if (isRequestor(message)) {

            // no response header available. do nothing, do not remove the
            // context
            // if (LOG.isDebugEnabled()) {
            // LOG.debug("client handleMessage: owning flowId: " + FlowId.peekFlowId());
            // }
        } else {

            // this is the response and the end of the call.
            // add the flow id to the response and remove it from our context.
            final HttpServletResponse httpServletResponse = (HttpServletResponse) message.get(
                    AbstractHTTPDestination.HTTP_RESPONSE);
            // if (LOG.isDebugEnabled()) {
            // LOG.debug("server handleMessage: removing flowId: " + FlowId.peekFlowId());
            // }

            httpServletResponse.setHeader(FlowIdInboundInterceptor.X_FLOW_ID, FlowId.popFlowId());
        }
    }
}
