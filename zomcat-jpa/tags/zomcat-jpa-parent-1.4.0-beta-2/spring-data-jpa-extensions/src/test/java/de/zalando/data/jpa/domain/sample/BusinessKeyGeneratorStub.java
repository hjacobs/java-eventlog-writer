package de.zalando.data.jpa.domain.sample;

import de.zalando.data.jpa.domain.support.BusinessKeyGenerator;

/**
 * @author  jbellmann
 */
public class BusinessKeyGeneratorStub implements BusinessKeyGenerator {

    public String getBusinessKeyForSelector(final String selector) {
        return "KP000000001";
    }

}
