package de.zalando.zomcat.cxf;

/**
 * A convenience interface providing all HTTP headers used in Zalando's service requests.
 *
 * @author  rreis
 */
public interface HttpHeaders {

    /**
     * The host and instance where an application is running, in the format <code>host:instance</code>. E.g. <code>
     * fesn01:9120</code>.
     */
    String HOST_INSTANCE = "x-host-instance";

    /**
     * The Flow Id of the related service request/response.
     */
    String FLOW_ID = "x-flow-id";

}
