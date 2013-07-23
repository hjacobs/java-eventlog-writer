package de.zalando.data.jpa.domain.support;

/**
 * Interface an BusinessKeyGenerator has to implement.
 *
 * @author  jbellmann
 */
public interface BusinessKeyGenerator {

    String getBusinessKeyForSelector(String businessKeySelector);

}
