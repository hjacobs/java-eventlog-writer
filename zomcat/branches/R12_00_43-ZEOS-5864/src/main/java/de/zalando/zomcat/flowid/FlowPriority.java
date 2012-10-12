package de.zalando.zomcat.flowid;

import de.zalando.zomcat.ExecutionContext;

public class FlowPriority {

    private static final String FLOW_PRIORITY_KEY = "FLOW_PRIORITY";

    public enum Priority {
        DEFAULT,
        HIGH
    }

    /**
     * access the priority of the current flow.
     *
     * @return  the Priority of the current flow
     */
    public static Priority flowPriority() {
        final String priority = ExecutionContext.getValue(FLOW_PRIORITY_KEY);
        if (priority == null) {
            return Priority.DEFAULT;
        }

        return Priority.valueOf(priority);
    }

    /**
     * Set the priority of this flow. Currently the priority can only be changed to the upside.
     */
    public static void setFlowPriority(final Priority priority) {
        final Priority flowPriority = flowPriority();

        // the priority can only be changed to the upside - but not down.
        if (flowPriority == Priority.DEFAULT && flowPriority != priority) {
            ExecutionContext.add(FLOW_PRIORITY_KEY, priority.name());
        }
    }

    /**
     * remove the current priority from this flow.
     */
    public static void clearFlowPriority() {
        ExecutionContext.remove(FLOW_PRIORITY_KEY);
    }
}
