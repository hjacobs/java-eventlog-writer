package de.zalando.data.jpa.repository.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * NamespaceHandler for zalando-spring-data-jpa-extensions.
 *
 * @author  jbellmann
 */
@Deprecated
public class BusinessKeyNameSpaceHandler extends NamespaceHandlerSupport {

    public void init() {
// registerBeanDefinitionParser("businesskey", new BusinessKeyBeanDefinitionParser());
    }

}
