package de.zalando.data.jpa.domain.support;

/**
 * Interface an SkuIdGenerator has to implement.
 *
 * @author  jbellmann
 */
public interface SeqIdGenerator {

    Number getSeqId(String sequenceName, boolean negate);

}
