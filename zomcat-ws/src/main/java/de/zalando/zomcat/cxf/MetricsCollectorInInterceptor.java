package de.zalando.zomcat.cxf;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageUtils;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.zomcat.cxf.metrics.MetricsListener;

/**
 * An inbound interceptor used in collectiong metrics from web service requests.
 *
 * <p>When a request arrives to the server, this interceptor invokes the appropriate <code>MetricsListener</code> to
 * handle the incoming message.
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
     * The listener for message and fault handling events from this object.
     */
    private final MetricsListener listener;

    /**
     * Constructs a new instance of this inbound interceptor.
     *
     * <p>The interceptor is placed in the <code>USER_LOGICAL</code> phase of the service's inbound interceptor chain.
     *
     * @param  listener  the object listening for events from this interceptor.
     */
    public MetricsCollectorInInterceptor(final MetricsListener listener) {
        super(Phase.USER_LOGICAL);
        this.listener = listener;
    }

    /**
     * Handles the specified message.
     *
     * <p>When invoked, the listener given at construction time is notified through its <code>onRequest()</code>
     * implementation.
     *
     * @param   message  the message from the service's inbound chain.
     *
     * @throws  Fault  if some fault occurs during the invocation processing.
     */
    @Override
    public void handleMessage(final Message message) throws Fault {
        if (!MessageUtils.isRequestor(message)) {
            try {
                listener.onRequest(message);
            } catch (Exception e) {
                LOG.error("Exception in metrics interceptor while handling message", e);
            }
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
        if (!MessageUtils.isRequestor(message)) {
            try {
                listener.onFault(message);
            } catch (Exception e) {
                LOG.error("Exception in metrics interceptor while handling fault", e);
            }
        }
    }
}
