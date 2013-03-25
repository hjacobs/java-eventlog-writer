package de.zalando.data.jpa.domain.support;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

import static org.junit.Assert.assertThat;

import org.junit.Test;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;

public class BusinessKeyNamespaceHandlerTest extends BusinessKeyBeanFactoryPostProcessorUnitTests {

    @Override
    protected String getConfigFile() {
        return "businesskey-namespace-context.xml";
    }

    @Test
    public void registersBeanDefinitions() {
        BeanDefinition definition = beanFactory.getBeanDefinition(BusinessKeyEntityListener.class.getName());
        PropertyValue propertyValue = definition.getPropertyValues().getPropertyValue("keyHandler");
        assertThat(propertyValue, is(notNullValue()));
    }

}
