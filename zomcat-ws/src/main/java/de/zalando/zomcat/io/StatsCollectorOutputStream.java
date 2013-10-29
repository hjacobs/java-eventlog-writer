package de.zalando.zomcat.io;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class StatsCollectorOutputStream extends OutputStream {

    protected OutputStream originalStream;

    private List<StatsCollectorOutputStreamCallback> callbacks;

    private long bytesWritten = 0;

    public StatsCollectorOutputStream(final OutputStream originalStream) {
        super();
        if (originalStream == null) {
            throw new IllegalArgumentException("Stream may not be null");
        }

        this.originalStream = originalStream;
    }

    public void registerCallback(final StatsCollectorOutputStreamCallback callback) {
        if (null == callbacks) {
            callbacks = new LinkedList<>();
        }

        callbacks.add(callback);
    }

    public void unregisterCallback(final StatsCollectorOutputStreamCallback callback) {
        if (null != callbacks) {
            callbacks.remove(callback);
        }
    }

    public List<StatsCollectorOutputStreamCallback> getCallbacks() {
        return callbacks == null ? null : Collections.unmodifiableList(callbacks);
    }

    public long getBytesWritten() {
        return bytesWritten;
    }

    @Override
    public void write(final int b) throws IOException {
        originalStream.write(b);

        bytesWritten += 4;
    }

    @Override
    public void write(final byte[] b) throws IOException {
        originalStream.write(b);

        bytesWritten += b.length;
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        originalStream.write(b, off, len);

        bytesWritten += len;
    }

    @Override
    public void flush() throws IOException {
        originalStream.flush();
    }

    @Override
    public void close() throws IOException {
        originalStream.flush();
        originalStream.close();

        if (null != callbacks) {
            for (StatsCollectorOutputStreamCallback cb : callbacks) {
                cb.onClose(this);
            }
        }
    }
}
