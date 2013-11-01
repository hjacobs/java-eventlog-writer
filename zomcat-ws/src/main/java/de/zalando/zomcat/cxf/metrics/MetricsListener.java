package de.zalando.zomcat.cxf.metrics;

import org.apache.cxf.message.Message;

public interface MetricsListener {
    void onRequest(Message m);

    void onResponse(Message m);
}
