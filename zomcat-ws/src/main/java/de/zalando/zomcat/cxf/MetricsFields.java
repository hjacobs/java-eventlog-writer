package de.zalando.zomcat.cxf;

/**
 * An enumeration containing the metrics fields used to collect information from a Web Service operation.
 *
 * <p>Typically these fields are stored in a <code>WebServiceMetrics</code> object, used as a conteiner for metrics.
 *
 * @author  rreis
 * @see     WebServiceMetrics
 */
public enum MetricsFields {

    /**
     * The IP address of the web service requester.
     */
    CLIENT_IP("de.zalando.cxf.exchange.client.ip"),
    /**
     * Time that the request left from the service requester.
     */
    CLIENT_REQUEST_TIME("de.zalando.cxf.exchange.client.request.time"),
    /**
     * Time that the response arrived to the service requestor.
     */
    CLIENT_RESPONSE_TIME("de.zalando.cxf.exchange.client.response.time"),
    /**
     * Size of the request message, in bytes.
     */
    REQUEST_SIZE("de.zalando.cxf.exchange.request.size"),
    /**
     * Size of the response message, in bytes.
     */
    RESPONSE_SIZE("de.zalando.cxf.exchange.response.size"),
    /**
     * Flow ID associated with the web service operation.
     */
    FLOW_ID("de.zalando.cxf.exchange.flow-id"),
    /**
     * IP address of the web service provider.
     */
    SERVICE_IP("de.zalando.cxf.exchange.service.ip"),
    /**
     * Host of the web service provider.
     */
    SERVICE_HOST("de.zalando.cxf.exchange.service.host"),
    /**
     * Instance of the web service provider.
     */
    SERVICE_INSTANCE("de.zalando.cxf.exchange.service.instance"),
    /**
     * Time that the request arrived to the service provider.
     */
    SERVICE_REQUEST_TIME("de.zalando.cxf.exchange.service.request.time"),
    /**
     * Time that the response left from the service provider.
     */
    SERVICE_RESPONSE_TIME("de.zalando.cxf.exchange.service.response.time"),
    /**
     * Name of the web service.
     */
    SERVICE_NAME("de.zalando.cxf.exchange.service.name"),
    /**
     * Operation of the web service.
     */
    SERVICE_OPERATION("de.zalando.cxf.exchange.service.operation");

    /**
     * Key for this metrics field.
     */
    private final String key;

    /**
     * Constructs a new instance with the specified key.
     *
     * @param  key  the key for this instance.
     */
    private MetricsFields(final String key) {
        this.key = key;
    }

    /**
     * Returns the key associated to this field.
     *
     * @return  the key associated to this field.
     */
    public String getKey() {
        return key;
    }
}
