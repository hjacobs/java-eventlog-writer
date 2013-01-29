package de.zalando.zomcat.flowid;

import de.zalando.zomcat.ExecutionContext;

public class FlowUserContext {

    private static final String FLOW_USER_CONTEXT_KEY = "FLOW_USER_CTX";

    /**
     * access the user context of the current flow.
     *
     * @return  the user context of the current flow - or an empty string if not available.
     */
    public static String getUserContext() {
        final String userContext = ExecutionContext.getValue(FLOW_USER_CONTEXT_KEY);
        return userContext == null ? "" : userContext;
    }

    /**
     * Set the user context of this flow.
     */
    public static void setUserContext(final String userContext) {
        ExecutionContext.add(FLOW_USER_CONTEXT_KEY, userContext);
    }

    /**
     * remove the current user context from this flow.
     *
     * @return  the removed user context of the current flow - or an empty string if not available.
     */
    public static String clear() {
        final String removedUserContext = ExecutionContext.remove(FLOW_USER_CONTEXT_KEY);
        return removedUserContext == null ? "" : removedUserContext;
    }
}
