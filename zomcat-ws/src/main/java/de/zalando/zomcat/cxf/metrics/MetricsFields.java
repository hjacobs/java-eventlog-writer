package de.zalando.zomcat.cxf.metrics;

/**
 * An enumeration containing the metrics fields used to collect information from a Web Service operation.
 *
 * <p>Typically these fields are stored in a <code>WebServiceMetrics</code> object, used as a conteiner for metrics.
 *
 * @author  rreis
 * @see     WebServiceMetrics
 */
public class MetricsFields<T> {

    /**
     * The IP address of the web service requester.
     */
    public static final MetricsFields<String> CLIENT_IP = new MetricsFields<>("de.zalando.cxf.exchange.client.ip");

    /**
     * Time that the request left from the service requester.
     */
    public static final MetricsFields<Long> CLIENT_REQUEST_TIME = new MetricsFields<>(
            "de.zalando.cxf.exchange.client.request.time");

    /**
     * Time that the response arrived to the service requestor.
     */
    public static final MetricsFields<Long> CLIENT_RESPONSE_TIME = new MetricsFields<>(
            "de.zalando.cxf.exchange.client.response.time");

    /**
     * Size of the request message, in bytes.
     */
    public static final MetricsFields<Integer> REQUEST_SIZE = new MetricsFields<>(
            "de.zalando.cxf.exchange.request.size");

    /**
     * Size of the response message, in bytes.
     */
    public static final MetricsFields<Integer> RESPONSE_SIZE = new MetricsFields<>(
            "de.zalando.cxf.exchange.response.size");

    /**
     * Flow ID associated with the web service operation.
     */
    public static final MetricsFields<String> FLOW_ID = new MetricsFields<>("de.zalando.cxf.exchange.flow-id");

    /**
     * IP address of the web service provider.
     */
    public static final MetricsFields<String> SERVICE_IP = new MetricsFields<>("de.zalando.cxf.exchange.service.ip");

    /**
     * Host of the web service provider.
     */
    public static final MetricsFields<String> SERVICE_HOST = new MetricsFields<>(
            "de.zalando.cxf.exchange.service.host");

    /**
     * Instance of the web service provider.
     */
    public static final MetricsFields<String> SERVICE_INSTANCE = new MetricsFields<>(
            "de.zalando.cxf.exchange.service.instance");

    /**
     * Time that the request arrived to the service provider.
     */
    public static final MetricsFields<Long> SERVICE_REQUEST_TIME = new MetricsFields<>(
            "de.zalando.cxf.exchange.service.request.time");

    /**
     * Time that the response left from the service provider.
     */
    public static final MetricsFields<Long> SERVICE_RESPONSE_TIME = new MetricsFields<>(
            "de.zalando.cxf.exchange.service.response.time");

    /**
     * Name of the web service.
     */
    public static final MetricsFields<String> SERVICE_NAME = new MetricsFields<>(
            "de.zalando.cxf.exchange.service.name");

    /**
     * Operation of the web service.
     */
    public static final MetricsFields<String> SERVICE_OPERATION = new MetricsFields<>(
            "de.zalando.cxf.exchange.service.operation");

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
