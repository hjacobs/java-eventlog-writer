package de.zalando.zomcat;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;

import de.zalando.zomcat.jobs.listener.JobFlowIdListener;

/**
 * Execution context management. Add key, value pairs to the execution context. It will be spreaded into client services
 * as done with flow id.
 *
 * @author  carsten.wolters
 */
public final class ExecutionContext {
    private static final Logger LOG = LoggerFactory.getLogger(JobFlowIdListener.class);

    // the thread local containing our map:
    private final ThreadLocal<Map<String, String>> executionContextMap = new ThreadLocal<Map<String, String>>() {
        @Override
        protected Map<String, String> initialValue() {
            return Maps.newHashMap();
        }
    };

    // the static instance
    private static ExecutionContext instance = new ExecutionContext();

    // Don't let outsiders create new factories directly
    private ExecutionContext() { }

    // get the context
    private static ExecutionContext getExecutionContext() {
        return instance;
    }

    /**
     * Add a key, value pair to the execution context.
     *
     * @param  key    the key to add
     * @param  value  the value to add
     */
    public static void add(final String key, final String value) {
        getExecutionContext().executionContextMap.get().put(key, value);
        LOG.trace("added key, value pair to execution context: {}={}", key, value);
    }

    /**
     * Get the matching value is exists.
     *
     * @param   key  the key to get the value for
     *
     * @return  the found value or null
     */
    public static String getValue(final String key) {
        return getExecutionContext().executionContextMap.get().get(key);
    }

    /**
     * Remove a key/value pair from the map.
     *
     * @param   key  the key for the value to remove.
     *
     * @return  the removed value.
     */
    public static String remove(final String key) {
        LOG.trace("removed key/value pair from the map for key [{}]", key);
        return getExecutionContext().executionContextMap.get().remove(key);
    }

    /**
     * clear the whole thread local map. we do not remove them from this thread
     */
    public static void clear() {
        getExecutionContext().executionContextMap.get().clear();
        LOG.trace("cleared execution context.");
    }

    /**
     * Check if the execution context is empty.
     *
     * @return  the map status.
     */
    public static boolean isEmpty() {
        return getExecutionContext().executionContextMap.get().isEmpty();
    }

    /**
     * add a serialized execution context to the current context.
     *
     * @param  serializedExecutionContexts
     */
    public static void addSerialized(final String serializedExecutionContexts) {
        if (serializedExecutionContexts != null) {
            final Iterable<String> splitted = Splitter.on("&").trimResults().omitEmptyStrings().split(
                    serializedExecutionContexts);
            for (final String split : splitted) {
                final String[] keyValues = split.split("=");
                add(keyValues[0], keyValues[1]);
            }
        }
    }

    /**
     * serialize the current execution context.
     *
     * @return  the serialized context or null if the map is empty.
     */
    public static String serialize() {
        final String serialized = Joiner.on("&").useForNull("").withKeyValueSeparator("=").join(
                getExecutionContext().executionContextMap.get());
        return serialized;
    }
}
