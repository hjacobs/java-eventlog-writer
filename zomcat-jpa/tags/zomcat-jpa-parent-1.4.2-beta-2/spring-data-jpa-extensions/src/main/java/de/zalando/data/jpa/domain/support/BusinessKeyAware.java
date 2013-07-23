package de.zalando.data.jpa.domain.support;

/**
 * Marks an Entity as BusinessKeyAware to get an BusinessKey injected.
 *
 * @author  jbellmann
 */
public interface BusinessKeyAware {

    String getBusinessKeySelector();

    void setBusinessKey(String businessKey);

    String getBusinessKey();

}
