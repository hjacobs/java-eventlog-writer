package de.zalando.eventlog;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedTransferQueue;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by jmussler on 8/11/15.
 */
public class RemoteEventLogger extends EventLogger {

    private static final Logger LOG = Logger.getLogger(RemoteEventLogger.class);

    private final LinkedTransferQueue<EventTask> queue;

    public RemoteEventLogger(LinkedTransferQueue<EventTask> queue) {
        this.queue = queue;
    }

    @Override
    public void log(final EventType type, final Object... values) {
        // we can log less values than fields, but not more
        checkArgument(values.length <= type.getFieldNames().size(),
                "too many event values for %s (number of values: %s, number of fields: %s)", type.getName(), values.length,
                type.getFieldNames().size());

        List<String> stringValues = new ArrayList<>(20);
        for(Object o : values) {
            String v = getValue(o);
            stringValues.add(v);
        }

        queue.add(new EventTask(type, "-flow-id-", stringValues));
    }

}
