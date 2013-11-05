package de.zalando.zomcat.cxf;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.zomcat.cxf.metrics.MetricsListener;

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
public class MetricsCollectorOutInterceptor extends AbstractPhaseInterceptor<Message> {

    /**
     * The logging object for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MetricsCollectorOutInterceptor.class);

    private final MetricsListener listener;

    /**
     * Constructs a new instance of this outbound interceptor.
     *
     * <p>The interceptor is placed in the <code>PRE_STREAM</code> phase of the service's outbound interceptor chain.
     */
    public MetricsCollectorOutInterceptor(final MetricsListener listener) {
        super(Phase.PRE_STREAM);
        this.listener = listener;
    }

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
        try {
            listener.onResponse(message);
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
