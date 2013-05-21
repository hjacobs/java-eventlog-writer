package de.zalando.data.jpa.domain.support;

/**
 * Interface an SkuIdGenerator has to implement.
 *
 * @author  jbellmann
 */
public interface SkuIdGenerator {

    Number getSkuId(String sequenceName, boolean negate);

}
