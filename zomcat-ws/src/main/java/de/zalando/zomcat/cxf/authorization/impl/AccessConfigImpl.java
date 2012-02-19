package de.zalando.zomcat.cxf.authorization.impl;

import java.util.List;

import de.zalando.appconfig.Configuration;

import de.zalando.zomcat.cxf.authorization.AccessConfig;
import de.zalando.zomcat.cxf.authorization.WebServiceAuthorizationLevel;

public class AccessConfigImpl implements AccessConfig {

    private Configuration config;

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(final Configuration config) {
        this.config = config;
    }

    @Override
    public List<String> getAllowedRoles() {
        return config.getStringListConfig("wsauth.roles.allowed");
    }

    @Override
    public List<String> getDeniedRoles() {
        return config.getStringListConfig("wsauth.roles.denied");
    }

    @Override
    public WebServiceAuthorizationLevel getWebServiceAuthorizationLevel() {
        if (!config.getBooleanConfig("wsauth.enabled", null, false)) {
            return WebServiceAuthorizationLevel.DISABLED;
        }

        return WebServiceAuthorizationLevel.getEnum(config.getStringConfig("wsauth.level", null,
                    WebServiceAuthorizationLevel.ENABLED.name()));
    }

}
