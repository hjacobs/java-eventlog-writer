package de.zalando.zomcat.cxf;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.zomcat.cxf.metrics.MetricsListener;

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

    private final MetricsListener listener;

    /**
     * Constructs a new instance of this inbound interceptor.
     *
     * <p>The interceptor is placed in the <code>USER_LOGICAL</code> phase of the service's inbound interceptor chain.
     */
    public MetricsCollectorInInterceptor(final MetricsListener listener) {
        super(Phase.USER_LOGICAL);
        this.listener = listener;
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
        try {
            listener.onRequest(message);
        } catch (Exception e) {
            LOG.error("Exception in metrics interceptor while handling message", e);
        }
    }

    @Override
    public void handleFault(final Message message) {
        try {
            listener.handleFault(message);
        } catch (Exception e) {
            LOG.error("Exception in metrics interceptor while handling fault", e);
        }
    }
}
