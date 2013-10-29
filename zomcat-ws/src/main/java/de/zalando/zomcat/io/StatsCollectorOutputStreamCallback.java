package de.zalando.zomcat.io;

public interface StatsCollectorOutputStreamCallback {
    void onClose(StatsCollectorOutputStream os);
}
