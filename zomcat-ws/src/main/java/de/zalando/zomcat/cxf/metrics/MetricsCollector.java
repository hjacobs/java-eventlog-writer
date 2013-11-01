package de.zalando.zomcat.cxf.metrics;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.namespace.QName;

import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;

import de.zalando.zomcat.cxf.HttpHeaders;
import de.zalando.zomcat.io.StatsCollectorOutputStream;
import de.zalando.zomcat.io.StatsCollectorOutputStreamCallback;

public class MetricsCollector implements MetricsListener {

    /**
     * The logging object for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MetricsCollector.class);

    private final MetricRegistry registry;

    public MetricsCollector(final MetricRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void onRequest(final Message message) {

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
                                                                   .field(MetricsFields.SERVICE_REQUEST_TIME,
                serviceRequestTime).build();
        message.getExchange().put(WebServiceMetrics.class, metrics);

        // Log the metrics
        LOG.info("{} {} {} {}:{} {} {} {} null",
            new Object[] {flowId, clientIp, serviceIp, host, instance, serviceName, operation, requestSize});
    }

    @Override
    public void onResponse(final Message cxfMessage) {

        // Get's the HTTP response. It's an error if it's not present in the message.
        final HttpServletResponse response = (HttpServletResponse) cxfMessage.get(
                AbstractHTTPDestination.HTTP_RESPONSE);
        if (response == null) {
            LOG.error("No HTTP Response found.");
            return;
        }

        // Get the response time
        long responseTime = System.nanoTime();

        /*
         * Wrap the message's output stream inside a StatsCollector, with the ability to count the response size in
         * bytes.
         */
        StatsCollectorOutputStream statsOs = new StatsCollectorOutputStream(cxfMessage.getContent(OutputStream.class));

        // Register our callback, which will effectively log the metrics when the response size is available.
        statsOs.registerCallback(new MetricsCollectorCallback(cxfMessage, responseTime));
        cxfMessage.setContent(OutputStream.class, statsOs);

    }

    protected class MetricsCollectorCallback implements StatsCollectorOutputStreamCallback {

        private final Message cxfMessage;

        private long responseTime;

        public MetricsCollectorCallback(final Message cxfMessage, final long responseTime) {
            this.cxfMessage = cxfMessage;
            this.responseTime = responseTime;
        }

        @Override
        public void onClose(final StatsCollectorOutputStream os) {
            long responseSize = os.getBytesWritten();

            WebServiceMetrics metrics = cxfMessage.getExchange().get(WebServiceMetrics.class);

            // Update metrics in Exchange
            cxfMessage.getExchange().put(WebServiceMetrics.class, metrics);

            // Calculate execution time
            long executionTime = responseTime - (Long) metrics.get(MetricsFields.SERVICE_REQUEST_TIME);

            // Output log
            LOG.info("{} {} {} {}:{} {} {} {} {}",
                new Object[] {
                    metrics.get(MetricsFields.FLOW_ID), metrics.get(MetricsFields.CLIENT_IP),
                    metrics.get(MetricsFields.SERVICE_IP), metrics.get(MetricsFields.SERVICE_HOST),
                    metrics.get(MetricsFields.SERVICE_INSTANCE), metrics.get(MetricsFields.SERVICE_NAME),
                    metrics.get(MetricsFields.SERVICE_OPERATION), responseSize, executionTime
                });
        }
    }
}
