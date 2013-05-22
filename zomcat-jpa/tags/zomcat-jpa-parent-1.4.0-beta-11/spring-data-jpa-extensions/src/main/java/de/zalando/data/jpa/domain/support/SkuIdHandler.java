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
public class SkuIdHandler<T> implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SkuIdHandler.class);

    private final SkuIdBeanWrapperFactory factory = new SkuIdBeanWrapperFactory();
    private SkuIdGenerator skuIdGenerator;

    public void setKeyGenerator(final SkuIdGenerator skuIdGenerator) {
        Assert.notNull(skuIdGenerator);
        this.skuIdGenerator = skuIdGenerator;
    }

    public void afterPropertiesSet() throws Exception {
        if (skuIdGenerator == null) {
            LOGGER.debug("No SkuIdGenerator set! Id will not be applied!");
        }
    }

    public void markCreated(final Object target) {
        touchCreated(target);
    }

    private void touchCreated(final Object target) {

        SkuIdBeanWrapper wrapper = factory.getBeanWrapperFor(target);
        if (wrapper != null) {
            Number key = skuIdGenerator.getSkuId(wrapper.getSequenceName(), wrapper.negateSku());
            wrapper.setSkuId(key);
        }
    }
}
