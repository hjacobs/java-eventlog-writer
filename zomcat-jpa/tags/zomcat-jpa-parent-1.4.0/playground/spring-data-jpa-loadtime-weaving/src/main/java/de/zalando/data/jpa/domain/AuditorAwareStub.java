package de.zalando.data.jpa.domain;

import org.springframework.data.domain.AuditorAware;

/**
 * The Auditor who changes the domain-objects.
 *
 * @author  jbellmann
 */
public class AuditorAwareStub implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        return "klaus.tester@zalando.de";
    }

}
