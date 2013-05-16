package de.zalando.zomcat.wsclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.zalando.appconfig.Configuration;

import de.zalando.zomcat.appconfig.BaseApplicationConfig;

/**
 * Abstract base class for web service factory beans. See https://devwiki.zalando.de/Web_Service_Clients
 */
public abstract class WebServiceClientFactoryBean<WS> extends ConfigurableWebServiceClientFactoryBean<WS> {

    @Autowired
    @Qualifier(BaseApplicationConfig.BEAN_NAME)
    private BaseApplicationConfig applicationConfig;

    @Override
    protected Configuration getConfiguration() {
        return applicationConfig.getConfig();
    }
}
