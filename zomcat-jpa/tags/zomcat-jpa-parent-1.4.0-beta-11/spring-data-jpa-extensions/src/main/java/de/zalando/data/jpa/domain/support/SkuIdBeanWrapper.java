package de.zalando.data.jpa.domain.support;

/**
 * An wrapper-interface for entities.
 *
 * @author  jbellmann
 */
public interface SkuIdBeanWrapper {

    String getSequenceName();

    boolean negateSku();

    void setSkuId(Number key);

}
