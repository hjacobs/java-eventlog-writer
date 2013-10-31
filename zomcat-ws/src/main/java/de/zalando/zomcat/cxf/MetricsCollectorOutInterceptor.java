package de.zalando.zomcat.cxf;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.zomcat.io.StatsCollectorOutputStream;
import de.zalando.zomcat.io.StatsCollectorOutputStreamCallback;

/**
 * An outbound interceptor used to log web service responses.
 *
 * <p>Before a response is sent to the requestor, this interceptor collects the following data:
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
 *   <li><b>Execution Time</b> - the total execution time for this operation.</li>
 * </ul>
 *
 * <p>When inserted in a service's outbound chain, this interceptor outputs the aforementioned metrics separated by
 * single spaces through the current logging system, in the following format:
 *
 * <p><code>&lt;flow id&gt; &lt;remote IP&gt; &lt;local IP&gt; &lt;host:instance&gt; &lt;service&gt; &lt;operation&gt;
 * &lt;request size&gt; &lt;exec time&gt;
 *
 * <p>This interceptor only works in conjunction with a <code>MetricsCollectorInInterceptor</code>.
 *
 * @author  rreis
 * @see     MetricsCollectorInInterceptor
 */
public class MetricsCollectorOutInterceptor extends AbstractPhaseInterceptor<Message>
    implements StatsCollectorOutputStreamCallback {

    /**
     * The logging object for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MetricsCollectorOutInterceptor.class);

    /**
     * Constructs a new instance of this outbound interceptor.
     *
     * <p>The interceptor is placed in the <code>PRE_STREAM</code> phase of the service's outbound interceptor chain.
     */
    public MetricsCollectorOutInterceptor() {
        super(Phase.PRE_STREAM);
    }

    /**
     * The log message where the response time will be inserted.
     */
    private WebServiceMetrics metrics;

    /**
     * The message contained in the web service.
     */
    private Message serviceMessage;

    /**
     * Retrieves the relevant metrics from the specified message and outputs through the current logging system.
     *
     * <p>All metrics depend on the inclusion of a <code>MetricsCollectorInInterceptor</code> in the inbound chain.
     *
     * @param   message  the message from the service's outbound chain.
     *
     * @throws  Fault  if some fault occurs during the invocation processing.
     *
     * @see     MetricsCollectorInInterceptor
     */
    @Override
    public void handleMessage(final Message message) throws Fault {

        // Get's the HTTP response. It's an error if it's not present in the message.
        final HttpServletResponse response = (HttpServletResponse) message.get(AbstractHTTPDestination.HTTP_RESPONSE);
        if (response == null) {
            LOG.error("No HTTP Response found.");
            return;
        }

        serviceMessage = message;

        // Get the response time
        long responseTime = System.nanoTime();

        // Get collected metrics from Inbound Interceptor
        metrics = serviceMessage.getExchange().get(WebServiceMetrics.class);
        if (metrics != null) {
            metrics = new WebServiceMetrics.Builder().fromInstance(metrics)
                                                     .field(MetricsFields.SERVICE_RESPONSE_TIME, responseTime).build();
        } else {
            metrics = new WebServiceMetrics.Builder().field(MetricsFields.SERVICE_RESPONSE_TIME, responseTime).build();
        }

        /*
         * Wrap the message's output stream inside a StatsCollector, with the ability to count the response size in
         * bytes.
         */
        StatsCollectorOutputStream statsOs = new StatsCollectorOutputStream(message.getContent(OutputStream.class));

        // Register our callback, which will effectively log the metrics when the response size is available.
        statsOs.registerCallback(this);
        serviceMessage.setContent(OutputStream.class, statsOs);

    }

    /**
     * Logs to the current logging system, after the specified stream is closed, including the response size.
     *
     * @param  os  the output stream where this callback is registered.
     */
    @Override
    public void onClose(final StatsCollectorOutputStream os) {
        long responseSize = os.getBytesWritten();

        // Insert response size in metrics
        metrics = new WebServiceMetrics.Builder().fromInstance(metrics).field(MetricsFields.RESPONSE_SIZE, responseSize)
                                                 .build();

        // Update metrics in Exchange
        serviceMessage.getExchange().put(WebServiceMetrics.class, metrics);

        // Calculate execution time
        long executionTime = (Long) metrics.get(MetricsFields.SERVICE_RESPONSE_TIME)
                - (Long) metrics.get(MetricsFields.SERVICE_REQUEST_TIME);

        // Output log
        LOG.info("{} {} {} {}:{} {} {} {} {}",
            new Object[] {
                metrics.get(MetricsFields.FLOW_ID), metrics.get(MetricsFields.CLIENT_IP),
                metrics.get(MetricsFields.SERVICE_IP), metrics.get(MetricsFields.SERVICE_HOST),
                metrics.get(MetricsFields.SERVICE_INSTANCE), metrics.get(MetricsFields.SERVICE_NAME),
                metrics.get(MetricsFields.SERVICE_OPERATION), metrics.get(MetricsFields.RESPONSE_SIZE), executionTime
            });
    }
}
