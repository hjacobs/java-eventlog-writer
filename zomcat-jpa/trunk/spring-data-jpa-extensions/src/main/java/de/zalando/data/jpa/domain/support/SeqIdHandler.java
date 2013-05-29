package de.zalando.data.jpa.domain.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.InitializingBean;

import org.springframework.util.Assert;

/**
 * @param   <T>
 *
 * @author  Joerg Bellmann
 */
public class SeqIdHandler<T> implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeqIdHandler.class);

    private final SeqIdBeanWrapperFactory factory = new SeqIdBeanWrapperFactory();
    private SeqIdGenerator seqIdGenerator;

    public void setKeyGenerator(final SeqIdGenerator skuIdGenerator) {
        Assert.notNull(skuIdGenerator);
        this.seqIdGenerator = skuIdGenerator;
    }

    public void afterPropertiesSet() throws Exception {
        if (seqIdGenerator == null) {
            LOGGER.debug("No SkuIdGenerator set! Id will not be applied!");
        }
    }

    public void markCreated(final Object target) {
        touchCreated(target);
    }

    private void touchCreated(final Object target) {

        SeqIdBeanWrapper wrapper = factory.getBeanWrapperFor(target);
        if (wrapper != null) {
            Number key = seqIdGenerator.getSeqId(wrapper.getSequenceName(), wrapper.negateSku());
            wrapper.setSkuId(key);
        }
    }
}
