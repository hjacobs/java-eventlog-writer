package de.zalando.data.jpa.domain.support;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import org.springframework.util.StringUtils;

/**
 * @author  jbellmann
 */
public class BusinessKeyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    static final String BEAN_CONFIGURER_ASPECT_BEAN_NAME =
        "org.springframework.context.config.internalBeanConfigurerAspect";

    private static final String JPA_PACKAGE = "org.springframework.orm.jpa.";
    private static final List<String> CLASSES_TO_DEPEND = Arrays.asList(JPA_PACKAGE
                + "LocalContainerEntityManagerFactoryBean", JPA_PACKAGE + "LocalEntityManagerFactoryBean");

    public void postProcessBeanFactory(final ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (!isSpringConfigured(beanFactory)) {
            return;
        }

        for (String beanName : beanFactory.getBeanDefinitionNames()) {

            BeanDefinition definition = beanFactory.getBeanDefinition(beanName);

            if (CLASSES_TO_DEPEND.contains(definition.getBeanClassName())) {
                definition.setDependsOn(StringUtils.addStringToArray(definition.getDependsOn(),
                        BEAN_CONFIGURER_ASPECT_BEAN_NAME));
            }
        }

        for (String beanName
                : BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory, BusinessKeyGenerator.class, true,
                    false)) {
            BeanDefinition definition = beanFactory.getBeanDefinition(beanName);
            definition.setLazyInit(true);
        }
    }

    /**
     * Returns whether we have a bean factory for which {@code &lt;context:spring-configured&gt;} was activated.
     *
     * @param   factory
     *
     * @return
     */
    private boolean isSpringConfigured(final BeanFactory factory) {
        try {
            factory.getBean(BEAN_CONFIGURER_ASPECT_BEAN_NAME);
            return true;
        } catch (NoSuchBeanDefinitionException e) {
            return false;
        }
    }

}
