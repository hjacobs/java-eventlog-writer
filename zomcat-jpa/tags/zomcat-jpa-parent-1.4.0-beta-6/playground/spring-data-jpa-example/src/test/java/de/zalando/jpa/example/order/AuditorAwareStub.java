package de.zalando.jpa.example.order;

import org.springframework.data.domain.AuditorAware;

/**
 * @author  jbellmann
 */
public class AuditorAwareStub implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        return "klaus.tester@zalando.de";
    }
}
