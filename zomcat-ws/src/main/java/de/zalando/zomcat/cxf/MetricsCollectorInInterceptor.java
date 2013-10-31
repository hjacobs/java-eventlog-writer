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

        // Gets the HTTP request. It's an error if it's not present in the message.
        final HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        if (request == null) {
            LOG.error("No HTTP Request found.");
            return;
        }

        // Collect metrics from Message
        String flowId = HttpHeaders.FLOW_ID.get(request);
        String clientIp = request.getRemoteAddr();
        int requestSize = request.getContentLength();
        String serviceIp = request.getLocalAddr();
        String host = HttpHeaders.HOST.get(request);
        String instance = HttpHeaders.INSTANCE.get(request);
        String serviceName = ((QName) message.get(Message.WSDL_SERVICE)).getLocalPart();
        String operation = ((QName) message.get(Message.WSDL_OPERATION)).getLocalPart();
        long serviceRequestTime = System.nanoTime();

        // Instantiate metrics and add to Exchange
        WebServiceMetrics metrics = new WebServiceMetrics.Builder().field(MetricsFields.FLOW_ID, flowId)
                                                                   .field(MetricsFields.CLIENT_IP, clientIp)
                                                                   .field(MetricsFields.REQUEST_SIZE, requestSize)
                                                                   .field(MetricsFields.SERVICE_IP, serviceIp)
                                                                   .field(MetricsFields.SERVICE_HOST, host)
                                                                   .field(MetricsFields.SERVICE_INSTANCE, instance)
                                                                   .field(MetricsFields.SERVICE_NAME, serviceName)
                                                                   .field(MetricsFields.SERVICE_OPERATION, operation)
                                                                   .field(MetricsFields.SERVICE_NAME, serviceName)
                                                                   .field(MetricsFields.SERVICE_REQUEST_TIME,
                serviceRequestTime).build();
        message.getExchange().put(WebServiceMetrics.class, metrics);

        // Log the metrics
        LOG.info("{} {} {} {}:{} {} {} {} null",
            new Object[] {flowId, clientIp, serviceIp, host, instance, serviceName, operation, requestSize});
    }
}
