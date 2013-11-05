package de.zalando.zomcat.cxf.metrics;

import org.apache.cxf.message.Message;

/**
 * Objects implementing this interface may register themselves as listeners in a <code>WebServiceMetricsInInterceptor.
 * </code> or <code>WebServiceMetricsOutInterceptor</code>, thus being able to process CXF messages.
 *
 * @author  rreis
 */
public interface MetricsListener {

    /**
     * Called when a request arrives to the Web Service provider.
     *
     * @param  m  the message from the request.
     */
    void onRequest(Message m);

    /**
     * Called when a response is sent back to the Web Service requestor.
     *
     * @param  m  the message included in the response.
     */
    void onResponse(Message m);

    /**
     * Called when a fault occurs while sending the response.
     *
     * @param  m  the message included in the fault.
     */
    void onFault(Message m);
}
