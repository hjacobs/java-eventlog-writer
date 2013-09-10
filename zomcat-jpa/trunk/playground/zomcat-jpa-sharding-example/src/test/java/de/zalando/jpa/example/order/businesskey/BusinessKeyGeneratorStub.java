package de.zalando.jpa.example.order.businesskey;

import java.util.UUID;

import de.zalando.data.jpa.domain.support.BusinessKeyGenerator;

/**
 * @author  jbellmann
 */
public class BusinessKeyGeneratorStub implements BusinessKeyGenerator {

    @Override
    public String getBusinessKeyForSelector(final String businessKeySelector) {

        // we can ignore the selector in this example
        return UUID.randomUUID().toString().replace("-", "");
    }

}
