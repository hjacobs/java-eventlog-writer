package de.zalando.zomcat.cxf;

import javax.servlet.http.HttpServletRequest;

import javax.xml.namespace.QName;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An inbound interceptor used to log web service requests.
 *
 * <p>When a request arrives to the server, this interceptor collects the following data:
 *
 * <ul>
 *   <li><b>Flow ID</b> - as defined in the request HTTP Header;</li>
 *   <li><b>Remote Address</b> - the IP address of the requestor;</li>
 *   <li><b>Local Address</b> - the IP address of the service being invoked;</li>
 *   <li><b>Host and Instance</b> - the Host and Instance of the service being invoked, in the format <code>
 *     host:instance</code> (as defined in the request HTTP Header);</li>
 *   <li><b>Service</b> - the service being accessed;</li>
 *   <li><b>Operation</b> - the operation being invoked;</li>
 *   <li><b>Request Size</b> - the size of the request, in bytes;</li>
 *   <li><b>Execution Time</b> - the total execution time for this operation. In this case it's <code>null</code>,
 *     because the service operation wasn't invoked yet.</li>
 * </ul>
 *
 * <p>When inserted in a service's inbound chain, this interceptor outputs the aforementioned metrics separated by
 * single spaces through the current logging system, in the following format:
 *
 * <p><code>&lt;flow id&gt; &lt;remote IP&gt; &lt;local IP&gt; &lt;host:instance&gt; &lt;service&gt; &lt;operation&gt;
 * &lt;request size&gt; &lt;exec time&gt;
 *
 * <p>Used in conjunction with a <code>MetricsCollectorOutInterceptor</code>, it is possible to determine the execution
 * time of the related web service operation.
 *
 * @author  rreis
 * @see     MetricsCollectorOutInterceptor
 */
public class MetricsCollectorInInterceptor extends AbstractPhaseInterceptor<Message> {

    /**
     * The logging object for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MetricsCollectorInInterceptor.class);

    /**
     * Constructs a new instance of this inbound interceptor.
     *
     * <p>The interceptor is placed in the <code>USER_LOGICAL</code> phase of the service's inbound interceptor chain.
     */
    public MetricsCollectorInInterceptor() {
        super(Phase.USER_LOGICAL);
    }

    /**
     * Retrieves the relevant metrics from the specified message and outputs through the current logging system.
     *
     * @param   message  the message from the service's inbound chain.
     *
     * @throws  Fault  if some fault occurs during the invocation processing.
     */
    @Override
    public void handleMessage(final Message message) throws Fault {
        StringBuffer logMessage = new StringBuffer();

        // Get's the HTTP request. It's an error if it's not present in the message.
        final HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        if (request == null) {
            LOG.error("No HTTP Request found.");
            return;
        }

        // Retrieves the metrics from the message and construct the log message
        logMessage.append(getFlowId(request));
        logMessage.append(" ");
        logMessage.append(request.getRemoteAddr());
        logMessage.append(" ");
        logMessage.append(request.getLocalAddr());
        logMessage.append(" ");
        logMessage.append(request.getHeader(HttpHeaders.HOST_INSTANCE));
        logMessage.append(" ");
        logMessage.append(((QName) message.get(Message.WSDL_SERVICE)).getLocalPart());
        logMessage.append(" ");
        logMessage.append(((QName) message.get(Message.WSDL_OPERATION)).getLocalPart());

        // Log the metrics
        LOG.info("{} {} null", logMessage, request.getContentLength());

        // Store the metrics and request time in milliseconds to be preocessed by the OutInterceptor
        message.getExchange().put("de.zalando.wsmetrics", logMessage.toString());
        message.getExchange().put("de.zalando.requestmillis", System.currentTimeMillis());
    }

    /**
     * Gets the Flow ID of the specified request.
     *
     * @param   request  the HTTP request.
     *
     * @return  the Flow ID, or <code>null</code> if not found.
     */
    protected String getFlowId(final HttpServletRequest request) {

        // Tries lower- and upper case versions of the Flow ID HTTP Header (as implemented in FlowIdInboundInterceptor)
        String flowId = request.getHeader(HttpHeaders.FLOW_ID);
        return flowId != null ? flowId : request.getHeader(HttpHeaders.FLOW_ID.toUpperCase());
    }
}
