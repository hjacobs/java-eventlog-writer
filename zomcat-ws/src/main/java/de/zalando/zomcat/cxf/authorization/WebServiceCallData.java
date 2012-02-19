package de.zalando.zomcat.cxf.authorization;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

/**
 * For the authentication the relevant parameters are clientIp, endpoint and loadBalancerIp.
 *
 * @author  jbuck
 */
public class WebServiceCallData {

    private static final String WILDCARD = "#";
    private String clientIp;
    private String endpoint;
    private String loadBalancerIp;

    public WebServiceCallData(final String endpoint, final String clientIp, final String loadBalancerIp) {
        super();
        this.clientIp = StringUtils.trimToEmpty(clientIp);
        this.endpoint = StringUtils.trimToEmpty(endpoint);
        this.loadBalancerIp = StringUtils.trimToEmpty(loadBalancerIp);
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(final String clientIp) {
        this.clientIp = clientIp;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(final String endpoint) {
        this.endpoint = endpoint;
    }

    public String getLoadBalancerIp() {
        return loadBalancerIp;
    }

    public void setLoadBalancerIp(final String loadBalancerIp) {
        this.loadBalancerIp = loadBalancerIp;
    }

    /**
     * Checks if the passed webserviceCallData matches with the data of the current object.
     *
     * @param   webserviceCallData
     *
     * @return
     */
    public boolean contains(final WebServiceCallData webserviceCallData) {

        // compare endPoint
        if (!this.endpoint.equals(webserviceCallData.endpoint) && !(this.endpoint.equals(WILDCARD))) {
            return false;
        }

        // compare clientIp
        RoutingPattern routingPattern = RoutingPattern.compile(this.clientIp);
        boolean clientIpMatches = routingPattern.matches(webserviceCallData.clientIp);

        if (!clientIpMatches) {
            return false;
        }

        // compare loadBalancerIp if it is set in the request
        boolean loadBalancerIpMatches = true;
        if (!Strings.isNullOrEmpty(webserviceCallData.loadBalancerIp)) {
            routingPattern = RoutingPattern.compile(this.loadBalancerIp);
            loadBalancerIpMatches = routingPattern.matches(webserviceCallData.loadBalancerIp);
        }

        return clientIpMatches && loadBalancerIpMatches;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(endpoint, clientIp, loadBalancerIp);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        WebServiceCallData other = (WebServiceCallData) obj;
        if (endpoint == null) {
            if (other.endpoint != null) {
                return false;
            }
        } else if (!endpoint.equals(other.endpoint)) {
            return false;
        }

        if (clientIp == null) {
            if (other.clientIp != null) {
                return false;
            }
        } else if (!clientIp.equals(other.clientIp)) {
            return false;
        }

        if (loadBalancerIp == null) {
            if (other.loadBalancerIp != null) {
                return false;
            }
        } else if (!loadBalancerIp.equals(other.loadBalancerIp)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WebServiceCallData [clientIp=");
        builder.append(clientIp);
        builder.append(", endpoint=");
        builder.append(endpoint);
        builder.append(", loadBalancerIp=");
        builder.append(loadBalancerIp);
        builder.append(']');
        return builder.toString();
    }
}
