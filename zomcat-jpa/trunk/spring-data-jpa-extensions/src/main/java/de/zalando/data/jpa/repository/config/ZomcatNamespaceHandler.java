package de.zalando.data.jpa.repository.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Namespacehandler for custom-namespaces of zomcat-jpa.
 *
 * @author  jbellmann
 */
public class ZomcatNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {

        registerBeanDefinitionParser("businesskey", new BusinessKeyBeanDefinitionParser());
        registerBeanDefinitionParser("seqid", new SeqIdBeanDefinitionParser());
    }

}
