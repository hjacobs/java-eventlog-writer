package de.zalando.data.jpa.domain.sample;

import org.springframework.data.domain.AuditorAware;

public class AuditorAwareStub implements AuditorAware<String> {

    @Override
    public String getCurrentAuditor() {
        return "klaus.meier@test.de";
    }
}
