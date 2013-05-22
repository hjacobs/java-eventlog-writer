package de.zalando.data.jpa.domain;

import java.util.UUID;

import de.zalando.data.jpa.domain.support.BusinessKeyGenerator;

/**
 * To generate BusinessKeys.
 *
 * @author  jbellmann
 */
public class BusinessKeyGeneratorStub implements BusinessKeyGenerator {

    @Override
    public String getBusinessKeyForSelector(final String businessKeySelector) {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
