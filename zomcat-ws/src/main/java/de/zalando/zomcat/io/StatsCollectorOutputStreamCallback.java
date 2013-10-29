package de.zalando.zomcat.io;

/**
 * Interface to define an object interested in events from a <code>StatsCollectorOutputStream</code>.
 *
 * <p>An object implementing this interface may register as a callback in a <code>StatsCollectorOutputStream</code>
 * object, thus enabling notification of relevant events.
 *
 * @author  rreis
 */
public interface StatsCollectorOutputStreamCallback {

    /**
     * Called when the <code>StatsCollectorOutputStream</code> where this object is registered as a callback is closed.
     *
     * @param  os  - the stream which was closed.
     */
    void onClose(StatsCollectorOutputStream os);
}
