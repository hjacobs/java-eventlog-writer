package de.zalando.zomcat.cxf;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.zomcat.cxf.metrics.MetricsListener;

/**
 * An outbound interceptor used in collectiong metrics from web service requests.
 *
 * <p>When a response is sent back to a client, this interceptor invokes the appropriate <code>MetricsListener</code> to
 * handle the outgoing message.
 *
 * @author  rreis
 * @see     MetricsCollectorInInterceptor
 */
public class MetricsCollectorOutInterceptor extends AbstractPhaseInterceptor<Message> {

    /**
     * The logging object for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger(MetricsCollectorOutInterceptor.class);

    /**
     * The listener for message and fault handling events from this object.
     */
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
     * Handles the specified message.
     *
     * <p>When invoked, the listener given at construction time is notified through its <code>onResponse()</code>
     * implementation.
     *
     * @param   message  the outgoing message from the service's outbound chain.
     *
     * @throws  Fault  if some fault occurs during the invocation processing.
     */
    @Override
    public void handleMessage(final Message message) throws Fault {
        try {
            listener.onResponse(message);
        } catch (Exception e) {
            LOG.error("Exception in metrics interceptor while handling message", e);
        }
    }

    /**
     * Handles a fault for the specified message.
     *
     * <p>When invoked, the listener given at construction time is notified through its <code>onFault()</code>
     * implementation.
     *
     * @param  message  the message from the service's inbound chain.
     */
    @Override
    public void handleFault(final Message message) {
        try {
            listener.onFault(message);
        } catch (Exception e) {
            LOG.error("Exception in metrics interceptor while handling fault", e);
        }
    }
}
