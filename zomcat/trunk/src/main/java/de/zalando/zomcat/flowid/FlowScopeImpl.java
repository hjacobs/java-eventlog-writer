package de.zalando.zomcat.flowid;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.MDC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.ObjectFactory;

import org.springframework.core.NamedThreadLocal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

class FlowScopeImpl implements FlowScope {
    private static final Logger LOG = LoggerFactory.getLogger(FlowScopeImpl.class);

    private static final String FLOW_ID_KEY = "FlowScopeImpl.flowId";
    private static final String DESTRUCTION_CALLBACKS_KEY = "FlowScopeImpl.destructionCallbacks";
    private static final String LOGGING_KEYS_KEY = "FlowScopeImpl.loggingKeys";

    private static final ThreadLocal<Map<String, Object>> FLOW_CONTEXT = new NamedThreadLocal<Map<String, Object>>(
            "FlowScope");

    @Override
    public boolean isActive() {
        return FLOW_CONTEXT.get() != null;
    }

    @Override
    public void enter() {
        enter(FlowId.peekFlowId());
    }

    @Override
    public void enter(final String flowId) {
        Preconditions.checkState(!isActive(), "Flow scope already initialized with flow ID {}!", flowId);

        FlowId.pushFlowId(flowId);

        FLOW_CONTEXT.set(Maps.<String, Object>newHashMap());

        getContext().put(FLOW_ID_KEY, flowId);
        getContext().put(DESTRUCTION_CALLBACKS_KEY, Maps.<String, Runnable>newHashMap());
        getContext().put(LOGGING_KEYS_KEY, Sets.<String>newHashSet());

        LOG.trace("Flow scope {} initialized.", flowId);
    }

    @Override
    public void exit() {
        Preconditions.checkState(isActive(), "No flow scope active!");

        final String flowId = getConversationId();

        for (final String destructionCallbackName : getDestructionCallbacks().keySet()) {
            callDestructionCallback(destructionCallbackName);
        }

        getDestructionCallbacks().clear();

        for (final String loggingKey : getLoggingKeys()) {
            MDC.remove(loggingKey);
        }

        getLoggingKeys().clear();

        getContext().clear();
        FLOW_CONTEXT.remove();
        LOG.trace("Flow scope {} destroyed.", flowId);
    }

    private Map<String, Object> getContext() {
        return Preconditions.checkNotNull(FLOW_CONTEXT.get(), "No flow scope active!");
    }

    private Map<String, Runnable> getDestructionCallbacks() {
        return (Map<String, Runnable>) getContext().get(DESTRUCTION_CALLBACKS_KEY);
    }

    @Override
    public Object get(final String name, final ObjectFactory objectFactory) {
        Map<String, Object> scope = getContext();

        Object object = scope.get(name);
        if (object == null) {
            object = objectFactory.getObject();
            scope.put(name, object);
        }

        return object;
    }

    @Override
    public Object remove(final String name) {
        callDestructionCallback(name);
        getDestructionCallbacks().remove(name);
        return getContext().remove(name);
    }

    @Override
    public void registerDestructionCallback(final String name, final Runnable callback) {
        getDestructionCallbacks().put(name, callback);
    }

    private void callDestructionCallback(final String name) {
        final Runnable runnable = getDestructionCallbacks().get(name);
        if (runnable != null) {
            try {
                runnable.run();
            } catch (final Exception e) {
                LOG.error(String.format("Failed to destruct '%s'!", name), e);
            }
        }
    }

    @Override
    public Object resolveContextualObject(final String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return (String) getContext().get(FLOW_ID_KEY);
    }

    private Set<String> getLoggingKeys() {
        return (Set<String>) getContext().get(LOGGING_KEYS_KEY);
    }

    @Override
    public void putLogInfo(final String key, final Object value) {
        final Set<String> loggingKeys = getLoggingKeys();

        MDC.put(key, value);
        loggingKeys.add(key);

        LOG.trace("Logging information set up: {} => {}", key, value);
    }

    @Override
    public void removeLogInfo(final String key) {
        final Set<String> loggingKeys = getLoggingKeys();

        LOG.trace("Logging information {} removed.", key);

        MDC.remove(key);
        loggingKeys.remove(key);
    }
}
