package de.zalando.data.jpa.domain.sample;

import javax.persistence.Entity;

import org.springframework.data.jpa.domain.AbstractPersistable;

import de.zalando.data.jpa.domain.support.BusinessKeyAware;

@Entity
public class BusinessKeyAwareProduct extends AbstractPersistable<Integer> implements BusinessKeyAware {

    private static final long serialVersionUID = 1L;

    private static final String KEY_SELECTOR = "BusinessKeyAwareProduct";

    private String businessKey;

    public String getBusinessKeySelector() {
        return KEY_SELECTOR;
    }

    public void setBusinessKey(final String businessKey) {
        if (this.businessKey == null) {
            this.businessKey = businessKey;
        }
    }

    public String getBusinessKey() {
        return this.businessKey;
    }

}
