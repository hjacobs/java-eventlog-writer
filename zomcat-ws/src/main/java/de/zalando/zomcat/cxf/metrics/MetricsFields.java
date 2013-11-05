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
     * The total number of requests made to the Web Service.
     */
    public static final MetricsFields<Integer> REQUEST_COUNT = new MetricsFields<>("request.count");

    /**
     * The total number of errors occurred in the web service.
     */
    public static final MetricsFields<Integer> ERROR_COUNT = new MetricsFields<>("error.count");

    /**
     * The IP address of the web service requester.
     */
    public static final MetricsFields<String> CLIENT_IP = new MetricsFields<>("client.ip");

    /**
     * Size of the request message, in bytes.
     */
    public static final MetricsFields<Integer> REQUEST_SIZE = new MetricsFields<>("request.size");

    /**
     * Size of the response message, in bytes.
     */
    public static final MetricsFields<Integer> RESPONSE_SIZE = new MetricsFields<>("response.size");

    /**
     * Flow ID associated with the web service operation.
     */
    public static final MetricsFields<String> FLOW_ID = new MetricsFields<>("flow-id");

    /**
     * IP address of the web service provider.
     */
    public static final MetricsFields<String> SERVICE_IP = new MetricsFields<>("service.ip");

    /**
     * Host of the web service provider.
     */
    public static final MetricsFields<String> SERVICE_HOST = new MetricsFields<>("service.host");

    /**
     * Instance of the web service provider.
     */
    public static final MetricsFields<String> SERVICE_INSTANCE = new MetricsFields<>("service.instance");

    /**
     * Time that the request arrived to the service provider.
     */
    public static final MetricsFields<Long> REQUEST_TIME = new MetricsFields<>("request.time");

    /**
     * Time that the response left from the service provider.
     */
    public static final MetricsFields<Long> RESPONSE_TIME = new MetricsFields<>("response.time");

    /**
     * The execution time of the Web Service operation.
     */
    public static final MetricsFields<Long> DURATION = new MetricsFields<>("duration");

    /**
     * Name of the web service.
     */
    public static final MetricsFields<String> SERVICE_NAME = new MetricsFields<>("service.name");

    /**
     * Operation of the web service.
     */
    public static final MetricsFields<String> SERVICE_OPERATION = new MetricsFields<>("service.operation");

    /**
     * Total count of checked application faults.
     */
    public static final MetricsFields<Integer> CHECKED_APPLICATION_FAULT = new MetricsFields<>(
            "fault.application.checked");

    /**
     * Total count of unchecked application faults.
     */
    public static final MetricsFields<Integer> UNCHECKED_APPLICATION_FAULT = new MetricsFields<>(
            "fault.application.unchecked");

    /**
     * Total count of logical runtime faults.
     */
    public static final MetricsFields<Integer> LOGICAL_RUNTIME_FAULT = new MetricsFields<>("fault.runtime.logical");

    /**
     * Total count of runtime faults.
     */
    public static final MetricsFields<Integer> RUNTIME_FAULT = new MetricsFields<>("fault.runtime");

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
    @Override
    public String toString() {
        return key;
    }
}
