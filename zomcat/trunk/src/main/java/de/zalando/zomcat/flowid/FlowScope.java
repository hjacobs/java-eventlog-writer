package de.zalando.zomcat.flowid;

import org.springframework.beans.factory.config.Scope;

/**
 * The flow scope allows beans to hold state during a flow (in the local component).
 *
 * @link  http://static.springsource.org/spring/docs/3.0.4.RELEASE/reference/htmlsingle/#beans-factory-scopes-custom
 */
public interface FlowScope extends Scope {

    /**
     * Only one flow scope can be active at a time in one thread.
     *
     * @return  if a flow scope is active on the current thread.
     */
    boolean isActive();

    /**
     * Start a new flow scope on the current thread.
     */
    void enter();

    /**
     * Start a new flow scope with the given flowId on the current thread.
     */
    void enter(String flowId);

    /**
     * End the flow scope of the current thread.
     */
    void exit();

    /**
     * End the flow scope of the current thread.
     */
    void exit(String flowId);

    /**
     * Store information important for logging in the scope. Exiting the scope will clean up the information
     * automatically.
     */
    void putLogInfo(String key, Object value);

    /**
     * Remove information from the logging before flow ends.
     */
    void removeLogInfo(String key);

}
