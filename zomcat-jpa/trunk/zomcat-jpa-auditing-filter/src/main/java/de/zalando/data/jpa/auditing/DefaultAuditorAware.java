package de.zalando.data.jpa.auditing;

import org.springframework.data.domain.AuditorAware;

/**
 * Default provider for the current auditor.
 *
 * @author  jbellmann
 */
public class DefaultAuditorAware implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        return AuditorContextHolder.getContext().getAuditor();
    }

}
