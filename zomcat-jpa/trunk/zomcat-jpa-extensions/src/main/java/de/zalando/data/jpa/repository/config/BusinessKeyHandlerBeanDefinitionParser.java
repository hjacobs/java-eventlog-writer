package de.zalando.data.jpa.repository.config;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.LazyInitTargetSource;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.BeanDefinitionParser;

import org.springframework.util.StringUtils;

import org.w3c.dom.Element;

import de.zalando.data.jpa.domain.support.BusinessKeyHandler;

/**
 * {@link BeanDefinitionParser} that parses and {@link BusinessKeyHandler} {@link BeanDefinition}.
 *
 * @author  Joerg Bellmann
 */
public class BusinessKeyHandlerBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    private static final String BUSINESSKEY_GENERATOR_REF = "businesskey-generator-ref";

    @Override
    protected Class<?> getBeanClass(final Element element) {
        return BusinessKeyHandler.class;
    }

    @Override
    protected boolean shouldGenerateId() {
        return true;
    }

    @Override
    protected void doParse(final Element element, final BeanDefinitionBuilder builder) {

        String keyGeneratorRef = element.getAttribute(BUSINESSKEY_GENERATOR_REF);
        if (StringUtils.hasText(keyGeneratorRef)) {
            builder.addPropertyValue("keyGenerator", createLazyInitTargetSourceBeanDefinition(keyGeneratorRef));
        }
    }

    private BeanDefinition createLazyInitTargetSourceBeanDefinition(final String keyGeneratorRef) {

        BeanDefinitionBuilder targetSourceBuilder = BeanDefinitionBuilder.rootBeanDefinition(
                LazyInitTargetSource.class);
        targetSourceBuilder.addPropertyValue("targetBeanName", keyGeneratorRef);

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ProxyFactoryBean.class);
        builder.addPropertyValue("targetSource", targetSourceBuilder.getBeanDefinition());

        return builder.getBeanDefinition();
    }
}
