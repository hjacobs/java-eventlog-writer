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
public class BusinessKeyHandler<T> implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BusinessKeyHandler.class);

    private final BusinessKeyBeanWrapperFactory factory = new BusinessKeyBeanWrapperFactory();
    private BusinessKeyGenerator keyGenerator;

    public void setKeyGenerator(final BusinessKeyGenerator keygenerator) {
        Assert.notNull(keygenerator);
        this.keyGenerator = keygenerator;
    }

    public void afterPropertiesSet() throws Exception {
        if (keyGenerator == null) {
            LOGGER.debug("No KeyGenerator set! Keying will not be applied!");
        }
    }

    public void markCreated(final Object target) {
        touchCreated(target);
    }

    private void touchCreated(final Object target) {

        BusinessKeyBeanWrapper wrapper = factory.getBeanWrapperFor(target);
        if (wrapper != null) {
            String key = keyGenerator.getBusinessKeyForSelector(wrapper.getBusinessKeySelector());
            wrapper.setBusinessKey(key);
        }
    }
}
