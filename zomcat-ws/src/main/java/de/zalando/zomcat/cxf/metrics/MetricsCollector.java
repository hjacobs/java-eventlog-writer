package de.zalando.zomcat.cxf.metrics;

import java.io.OutputStream;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.namespace.QName;

import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.FaultMode;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Clock;
import com.codahale.metrics.MetricRegistry;

import com.google.common.base.Preconditions;

import de.zalando.zomcat.configuration.AppInstanceContextProvider;
import de.zalando.zomcat.cxf.HttpHeaders;
import de.zalando.zomcat.cxf.MetricsCollectorOutInterceptor;
import de.zalando.zomcat.flowid.FlowId;
import de.zalando.zomcat.io.StatsCollectorOutputStream;
import de.zalando.zomcat.io.StatsCollectorOutputStreamCallback;

/**
 * A collector of metrics from web service operations.
 *
 * <p>When registered on the appropriate MetricsCollector interceptors, this object handles collection of web service
 * request data.
 *
 * @see  MetricsCollectorInInterceptor
 * @see  MetricsCollectorOutInterceptor
 */
public class MetricsCollector implements MetricsListener {

    /**
     * The logging object for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MetricsCollector.class);

    public static final long ONE_KB = 1024;

    /**
     * Provider of the current host and instance information.
     */
    private static final AppInstanceContextProvider provider = AppInstanceContextProvider.fromManifestOnFilesystem();

    /**
     */

    /**
     * The clock used to collect timed events, e.g. request/response instant.
     */
    private final Clock clock = Clock.defaultClock();

    /**
     * The registry used to collect metrics.
     */
    private final MetricRegistry registry;

    /**
     * Constructs a new instance, with the specified <code>MetricRegistry</code> registered for metrics collecting.
     *
     * @param  registry  the object used for metrics collecting.
     */
    public MetricsCollector(final MetricRegistry registry) {
        this.registry = registry;
    }

    /**
     * Returns the object used to collect metrics.
     *
     * @return  the object used to collect metrics.
     */
    public MetricRegistry getRegistry() {
        return registry;
    }

    /**
     * Collects metrics from the specified message.
     *
     * <p>When registered on a <code>MetricsCollectorInInterceptor</code>, this object collects the following data from
     * a Web Service request:
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
     * </ul>
     *
     * <p>Additionally, the request count and size are recorded through the registered <code>MetricRegistry</code> in
     * this object.
     *
     * <p>If the request is not one-way, this information can be retrieved through the Outbound interceptor chain.
     */
    @Override
    public void onRequest(final Message message) {
        Preconditions.checkNotNull(message, "message");

        // Gets the HTTP request. It's an error if it's not present in the message.
        final HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        if (request == null) {
            LOG.error("No HTTP Request found.");
            return;
        }

        // Collect metrics from Message
        final long serviceRequestTime = clock.getTick();
        final String clientIp = request.getRemoteAddr();
        final int requestSize = request.getContentLength();
        final String serviceIp = request.getLocalAddr();
        final String host = provider.getHost();
        final String instance = provider.getInstanceCode();
        final String serviceName = ((QName) message.get(Message.WSDL_SERVICE)).getLocalPart();
        final String operation = ((QName) message.get(Message.WSDL_OPERATION)).getLocalPart();

        String flowId = FlowId.peekFlowId();
        if (flowId == null || flowId.isEmpty()) {
            flowId = HttpHeaders.FLOW_ID.get(request);
        }

        // Metrics are registered with prefix servicename.operation
        String keyPrefix = MetricRegistry.name(serviceName, operation);
        registry.meter(MetricRegistry.name(keyPrefix, MetricsFields.REQUEST_COUNT.toString())).mark();

        /*
         * Happened while testing with Tomcat 6/7. Sometimes the content length returned by the request implementation
         * is unknown (-1). In this situation, nothing is recorded.
         */
        if (requestSize >= 0) {
            registry.histogram(MetricRegistry.name(keyPrefix, MetricsFields.REQUEST_SIZE.toString())).update(
                requestSize);
        }

        Exchange ex = message.getExchange();

        // Fields are only added if request is not one-way
        if (!ex.isOneWay()) {

            // Instantiate metrics and add to Exchange
            WebServiceMetrics metrics = new WebServiceMetrics.Builder().field(MetricsFields.FLOW_ID, flowId)
                                                                       .field(MetricsFields.CLIENT_IP, clientIp)
                                                                       .field(MetricsFields.REQUEST_SIZE, requestSize)
                                                                       .field(MetricsFields.SERVICE_IP, serviceIp)
                                                                       .field(MetricsFields.SERVICE_HOST, host)
                                                                       .field(MetricsFields.SERVICE_INSTANCE, instance)
                                                                       .field(MetricsFields.SERVICE_NAME, serviceName)
                                                                       .field(MetricsFields.SERVICE_OPERATION,
                    operation).field(MetricsFields.REQUEST_TIME, serviceRequestTime).build();
            ex.put(WebServiceMetrics.class, metrics);
        }

        // Log the metrics
        LOG.info("REQUEST [flow-id {}] [client ip {}] [service ip:{} host:{} instance:{} name:{} operation:{}] "
                + "[request-size {}]",
            new Object[] {flowId, clientIp, serviceIp, host, instance, serviceName, operation, requestSize});
    }

    /**
     * Collects metrics from the specified message.
     *
     * <p>When registered on a <code>MetricsCollectorOutInterceptor</code>, this object collects the following data from
     * a Web Service response:
     *
     * <ul>
     *   <li><b>Operation duration</b> - with the information provided by the corresponding inbound interceptor;</li>
     *   <li><b>Response size</b> - in bytes;</li>
     * </ul>
     *
     * <p>this information is also recorded through the registered <code>MetricRegistry</code> in this object.
     *
     * @see  MetricsCollectorInInterceptor
     * @see  MetricsCollectorOutInterceptor
     */
    @Override
    public void onResponse(final Message cxfMessage) {
        Preconditions.checkNotNull(cxfMessage, "cxfMessage");

        // Get's the HTTP response. It's an error if it's not present in the message.
        final HttpServletResponse response = (HttpServletResponse) cxfMessage.get(
                AbstractHTTPDestination.HTTP_RESPONSE);
        if (response == null) {
            LOG.error("No HTTP Response found.");
            return;
        }

        // Gets the response time
        long responseTime = clock.getTick();

        WebServiceMetrics metrics = cxfMessage.getExchange().get(WebServiceMetrics.class);

        if (metrics != null) {

            // Calculate execution time and record in metrics registry
            long executionDelta = responseTime - metrics.get(MetricsFields.REQUEST_TIME);

            String keyPrefix = MetricRegistry.name(metrics.get(MetricsFields.SERVICE_NAME),
                    metrics.get(MetricsFields.SERVICE_OPERATION));

            registry.timer(MetricRegistry.name(keyPrefix, MetricsFields.DURATION.toString())).update(executionDelta,
                TimeUnit.NANOSECONDS);

            // Output log
            LOG.info("RESPONSE [flow-id {}] [duration {} ms]", metrics.get(MetricsFields.FLOW_ID),
                TimeUnit.NANOSECONDS.toMillis(executionDelta));

        } else {
            LOG.error("No metrics found in CXF exchange. Cannot calculate execution duration");
        }

        // Wraps the existing output stream with one capable of recording response size.
        cxfMessage.setContent(OutputStream.class, buildOutputStream(responseTime, cxfMessage));
    }

    /**
     * Collects faults from the specified message.
     *
     * <p>When a <code>Fault</code> flows through a registered <code>MetricsCollectorOutInterceptor</code>, this object
     * collects the type of fault, and records it in the registered <code>MetricRegistry</code>.
     *
     * <p>The type of fault can be one of the following:
     *
     * <ul>
     *   <li>CHECKED_APPLICATION_FAULT</li>
     *   <li>UNCHECKED_APPLICATION_FAULT</li>
     *   <li>LOGICAL_RUNTIME_FAULT</li>
     *   <li>RUNTIME_FAULT</li>
     * </ul>
     *
     * @param  message  the CXF message with the fault information.
     *
     * @see    MetricsFields
     */
    @Override
    public void onFault(final Message message) {
        Preconditions.checkNotNull(message, "message");

        String serviceName = ((QName) message.get(Message.WSDL_SERVICE)).getLocalPart();
        String operation = ((QName) message.get(Message.WSDL_OPERATION)).getLocalPart();

        String keyPrefix = MetricRegistry.name(serviceName, operation);

        String faultClass = null;
        FaultMode mode = message.get(FaultMode.class);
        if (mode == null) {
            mode = FaultMode.RUNTIME_FAULT;
        }

        switch (mode) {

            case CHECKED_APPLICATION_FAULT :

                faultClass = MetricsFields.CHECKED_APPLICATION_FAULT.toString();
                registry.meter(MetricRegistry.name(keyPrefix, faultClass)).mark();
                break;

            case LOGICAL_RUNTIME_FAULT :
                faultClass = MetricsFields.LOGICAL_RUNTIME_FAULT.toString();
                registry.meter(MetricRegistry.name(keyPrefix, faultClass)).mark();
                break;

            case UNCHECKED_APPLICATION_FAULT :
                faultClass = MetricsFields.UNCHECKED_APPLICATION_FAULT.toString();
                registry.meter(MetricRegistry.name(keyPrefix, faultClass)).mark();
                break;

            case RUNTIME_FAULT :
            default :
                faultClass = MetricsFields.RUNTIME_FAULT.toString();
                registry.meter(MetricRegistry.name(keyPrefix, faultClass)).mark();
        }

        WebServiceMetrics metrics = message.getExchange().get(WebServiceMetrics.class);

        // Output log
        LOG.error("FAULT [flow-id {}] [fault-type {}]", metrics.get(MetricsFields.FLOW_ID), faultClass);
    }

    /**
     * Wraps the message's output stream in a StatsCollector, with the ability to count the response size in bytes.
     *
     * @param   cxfMessage  the message where the OutputStream to wrap is.
     *
     * @return  the resulting output stream.
     */
    protected StatsCollectorOutputStream buildOutputStream(final long responseBuiltTime, final Message cxfMessage) {
        StatsCollectorOutputStream statsOs = new StatsCollectorOutputStream(cxfMessage.getContent(OutputStream.class));

        // Register our callback, which will effectively log the metrics when the response size is available.
        statsOs.registerCallback(new MetricsCollectorCallback(responseBuiltTime, cxfMessage));

        return statsOs;
    }

    /**
     * A callback object used to record the total number of bytes written in an output stream. <b>When registered in a
     * <code>StatsCollectorOuputStream</code>, this callback records through the registered <code>MetricRegistry</code>
     * the total number of bytes written to the stream.
     *
     * @author  rreis
     * @see     buildOutputStream
     */
    protected class MetricsCollectorCallback implements StatsCollectorOutputStreamCallback {

        private final long responseBuiltTime;

        /**
         * The response message from the Web Service.
         */
        private final Message cxfMessage;

        /**
         * Constructs a new instance, with the provided response message.
         *
         * @param  cxfMessage  the CXF response message.
         */
        public MetricsCollectorCallback(final Long responseBuiltTime, final Message cxfMessage) {
            this.cxfMessage = cxfMessage;
            this.responseBuiltTime = responseBuiltTime;
        }

        /**
         * Records the total number of bytes written in the registered <code>MetricRegistry</code> from the outer class.
         *
         * @param  os  - the stream which was closed.
         *
         * @see    MetricsCollector
         */
        @Override
        public void onClose(final StatsCollectorOutputStream os) {
            long now = clock.getTick();
            long responseSize = os.getBytesWritten();

            WebServiceMetrics metrics = cxfMessage.getExchange().get(WebServiceMetrics.class);

            // Records response size in Metrics
            String keyPrefix = MetricRegistry.name(metrics.get(MetricsFields.SERVICE_NAME),
                    metrics.get(MetricsFields.SERVICE_OPERATION));

            registry.histogram(MetricRegistry.name(keyPrefix, MetricsFields.RESPONSE_SIZE.toString())).update(
                responseSize);

            // Output log
            LOG.info("WRITE [flow-id {}] [response-size {} KB] [duration {} ms]",
                new Object[] {
                    metrics.get(MetricsFields.FLOW_ID), responseSize / ONE_KB,
                    TimeUnit.NANOSECONDS.toMillis(now - responseBuiltTime)
                });
        }
    }
}
