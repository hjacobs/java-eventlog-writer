package de.zalando.data.jpa.domain.support;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;

import org.springframework.core.io.ClassPathResource;

/**
 * @author  jbellmann
 */
public class BusinessKeyBeanFactoryPostProcessorUnitTests {

    DefaultListableBeanFactory beanFactory;
    BusinessKeyBeanFactoryPostProcessor processor;

    @Before
    public void setUp() {

        beanFactory = new DefaultListableBeanFactory();

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(new ClassPathResource("businesskey/" + getConfigFile()));

        processor = new BusinessKeyBeanFactoryPostProcessor();
    }

    protected String getConfigFile() {

        return "businesskey-bfpp-context.xml";
    }

    @Test
    public void testname() throws Exception {

        processor.postProcessBeanFactory(beanFactory);

        BeanDefinition definition = beanFactory.getBeanDefinition("entityManagerFactory");

        assertTrue(Arrays.asList(definition.getDependsOn()).contains(
                BusinessKeyBeanFactoryPostProcessor.BEAN_CONFIGURER_ASPECT_BEAN_NAME));
    }
}
