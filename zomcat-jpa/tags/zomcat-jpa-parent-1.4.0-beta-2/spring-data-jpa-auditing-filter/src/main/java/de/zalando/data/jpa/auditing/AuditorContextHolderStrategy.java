package de.zalando.data.jpa.auditing;

/**
 * An AuditorContextHolder very much inspired by Spring-Security.<br/>
 * But should work in our environment too.
 *
 * @author  jbellmann
 */
interface AuditorContextHolderStrategy {

    void setContext(AuditorContext context);

    AuditorContext getContext();

    void clearContext();

    AuditorContext createEmptyContext();

}
