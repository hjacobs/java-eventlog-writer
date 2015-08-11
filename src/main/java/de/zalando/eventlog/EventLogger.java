package de.zalando.eventlog;

import org.apache.log4j.Logger;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class EventLogger {

    private static final Logger LOG = Logger.getLogger(EventLogger.class);

    private static EventLogger instance;

    private static Integer LOCK_OBJ = new Integer(1);

    private final static ScheduledThreadPoolExecutor WRITER_POOL = new ScheduledThreadPoolExecutor(1);

    public static EventLogger getLogger(final Class clazz) {
        if(instance!=null) return instance;

        String remoteUrl = System.getenv("EVENTLOG_REMOTE_URL");
        String applicationId = System.getenv("APPLICATION_ID");
        String applicationVersion = System.getenv("APPLICATION_VERSION");
        String tokenServiceUrl = System.getenv("OAUTH_ACCESS_TOKEN_URL");

        synchronized(LOCK_OBJ) {

            if (null == remoteUrl || "".equals(remoteUrl)) {
                if (null == instance) {
                    instance = new LocalEventLogger(clazz);
                }
            }
            else {
                if(null == instance) {
                    if (null == applicationId) {
                        LOG.error("APPLICATION_ID not set in environment vars");
                    }
                    if (null == applicationVersion) {
                        LOG.error("APPLICATION_VERSION not set in environment vars");
                    }

                    LinkedTransferQueue<EventTask> queue = new LinkedTransferQueue<>();

                    EventLogRemoteWriter writer = new EventLogRemoteWriter(remoteUrl, applicationId, applicationVersion, tokenServiceUrl, queue);

                    WRITER_POOL.scheduleWithFixedDelay(writer, 10, 1, TimeUnit.SECONDS);

                    instance = new RemoteEventLogger(queue);
                }
            }
        }

        return instance;
    }

    abstract public void log(final EventType type, final Object... values);

    protected String getValue(final Object o) {
        if (o == null) {
            return "null";
        }

        return o.toString().replace("\t", "\\t").replace("\n", "\\n");
    }
}
