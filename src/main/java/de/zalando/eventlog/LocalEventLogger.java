package de.zalando.eventlog;

import com.google.common.collect.Maps;
import org.apache.log4j.*;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by jmussler on 8/11/15.
 */
public class LocalEventLogger extends EventLogger {

    private static final Logger EVENT_LOG = Logger.getLogger("eventlog");

    private static final Logger EVENT_LOG_LAYOUT = Logger.getLogger("eventlog-layout");
    private static final Logger LOG = Logger.getLogger(LocalEventLogger.class);

    // enforce naming convention: for example do not allow underscores in field names!
    private static final Pattern VALID_FIELD_NAME_PATTERN = Pattern.compile("^[a-z][a-zA-Z0-9]*$");

    private final Map<Integer, Boolean> eventTypes = Maps.newConcurrentMap();

    public LocalEventLogger(final Class clazz) {
        EVENT_LOG.setLevel(Level.INFO);
        EVENT_LOG.setAdditivity(false);

        Layout layout = new PatternLayout("%d %x %m%n");
        String filename = System.getProperty("eventlog.filename");
        if (filename == null) {
            filename = "eventlog.log";

            String jvmProcessName = System.getProperty("jvm.process.name");
            if (jvmProcessName != null) {
                filename = "/data/zalando/logs/" + jvmProcessName + "/" + filename;
            }
        }

        try {
            Appender appender = new DailyRollingFileAppender(layout, filename, "'.'yyyy-MM-dd");
            EVENT_LOG.addAppender(appender);
        } catch (IOException ioe) {
            LOG.error("Could not initialize eventlog appender for class " + clazz.getName(), ioe);
        }

        EVENT_LOG_LAYOUT.setLevel(Level.INFO);
        EVENT_LOG_LAYOUT.setAdditivity(false);
        layout = new PatternLayout("%d %m%n");
        try {
            Appender appender = new DailyRollingFileAppender(layout, filename.replace(".log", ".layout"),
                    "'.'yyyy-MM-dd");
            EVENT_LOG_LAYOUT.addAppender(appender);
        } catch (IOException ioe) {
            LOG.error("Could not initialize eventlog layout appender for class " + clazz.getName(), ioe);
        }
    }

    private void logLayout(final EventType type) {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toHexString(type.getId()));
        sb.append('\t');
        sb.append(getValue(type.getName()));
        for (String name : type.getFieldNames()) {

            // enforce naming conventions!
            checkArgument(VALID_FIELD_NAME_PATTERN.matcher(name).matches(), "invalid event field name: '%s'", name);
            sb.append('\t');
            sb.append(getValue(name));
        }

        EVENT_LOG_LAYOUT.info(sb.toString());
    }

    public void log(final EventType type, final Object... values) {

        // we can log less values than fields, but not more
        checkArgument(values.length <= type.getFieldNames().size(),
                "too many event values for %s (number of values: %s, number of fields: %s)", type.getName(), values.length,
                type.getFieldNames().size());

        int id = type.getId();
        if (!eventTypes.containsKey(id)) {
            logLayout(type);
            eventTypes.put(id, true);
        }

        StringBuilder sb = new StringBuilder();

        sb.append(Integer.toHexString(id));

        int i = 0;
        for (Object o : values) {
            sb.append('\t');
            sb.append(getValue(o));
            i++;
        }

        EVENT_LOG.info(sb.toString());
    }
}
