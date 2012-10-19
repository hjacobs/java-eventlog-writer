package de.zalando.zomcat.wsclient;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.concurrent.TimeUnit;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.zalando.zomcat.appconfig.BaseApplicationConfig;

/**
 * Abstract base class for web service factory beans. See https://devwiki.zalando.de/Web_Service_Clients
 */
public abstract class WebServiceClientFactoryBean<WS> implements FactoryBean<WS> {

    public static final String WEBSERVICE_URL_PROPERTY_NAME = "webService.%s.url";

    public static final String CONNECT_TIMEOUT_PROPERTY_NAME = "webService.%s.connectTimeout";

    public static final String RECEIVE_TIMEOUT_PROPERTY_NAME = "webService.%s.receiveTimeout";

    public static final long DEFAULT_CONNECT_TIMEOUT_TIME = 30;
    public static final TimeUnit DEFAULT_CONNECT_TIMEOUT_UNIT = TimeUnit.SECONDS;

    public static final long DEFAULT_RECEIVE_TIMEOUT_TIME = 30;
    public static final TimeUnit DEFAULT_RECEIVE_TIMEOUT_UNIT = TimeUnit.SECONDS;

    public static final String TIME_UNIT_PROPERTY_NAME_SUFFIX = "Unit";

    public abstract Class<WS> getWebServiceClass();

    @Autowired
    @Qualifier(BaseApplicationConfig.BEAN_NAME)
    private BaseApplicationConfig applicationConfig;

    @Override
    @SuppressWarnings("unchecked")
    public WS getObject() throws Exception {
        ClientProxyFactoryBean factory = new JaxWsProxyFactoryBean();

        factory.setServiceClass(getWebServiceClass());
        factory.setAddress(getWebServiceUrl().toExternalForm());

        return configureTimeouts((WS) factory.create());
    }

    @Override
    public Class<?> getObjectType() {
        return getWebServiceClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private WS configureTimeouts(final WS service) {
        final Client client = ClientProxy.getClient(service);
        final HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        final HTTPClientPolicy httpClientPolicy = httpConduit.getClient();

        httpClientPolicy.setConnectionTimeout(getConnectTimeoutMillis());
        httpClientPolicy.setReceiveTimeout(getReceiveTimeoutMillis());

        return service;
    }

    private URL getWebServiceUrl() {
        final String urlPropertyName = formatProperty(WEBSERVICE_URL_PROPERTY_NAME);
        final String url = applicationConfig.getConfig().getStringConfig(urlPropertyName);

        if (url == null) {
            throw new IllegalArgumentException("Please provide WS URL with appConfig property " + urlPropertyName);
        }

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private long getConnectTimeoutMillis() {
        return getMillis(CONNECT_TIMEOUT_PROPERTY_NAME, DEFAULT_CONNECT_TIMEOUT_TIME, DEFAULT_CONNECT_TIMEOUT_UNIT);
    }

    private long getReceiveTimeoutMillis() {
        return getMillis(RECEIVE_TIMEOUT_PROPERTY_NAME, DEFAULT_RECEIVE_TIMEOUT_TIME, DEFAULT_RECEIVE_TIMEOUT_UNIT);
    }

    private long getMillis(final String propertyNameFormat, final long defaultTime, final TimeUnit defaultTimeUnit) {
        final String timePropertyName = formatProperty(propertyNameFormat);
        final String timeUnitPropertyName = formatProperty(propertyNameFormat + TIME_UNIT_PROPERTY_NAME_SUFFIX);

        final String timeString = applicationConfig.getConfig().getStringConfig(timePropertyName, null,
                "" + defaultTime);
        final String timeUnitString = applicationConfig.getConfig().getStringConfig(timeUnitPropertyName, null,
                defaultTimeUnit.name());

        final long time = Long.parseLong(timeString);
        final TimeUnit timeUnit = TimeUnit.valueOf(timeUnitString);

        return timeUnit.toMillis(time);
    }

    private String formatProperty(final String format) {
        return String.format(format, getWebServiceClass().getSimpleName());
    }
}
