package de.zalando.zomcat.cxf;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Preconditions;

/**
 * A convenience enumeration providing all HTTP headers used in Zalando's service requests.
 *
 * <p>Methods provided by this enumeration allow handling of Headers in HTTP requests/responses
 *
 * @author  rreis
 */
public enum HttpHeaders {

    /**
     * The host where an application is running.
     *
     * <p>This is not an actual HTTP Header. It is provided as a convenience to get only the HOST part from a
     * HOST_INSTANCE header.
     *
     * @see  HOST_INSTANCE
     */
    HOST,
    /**
     * The instance where an application is running.
     *
     * <p>This is not an actual HTTP Header. It is provided as a convenience to get only the INSTANCE part from a
     * HOST_INSTANCE header.
     *
     * @see  HOST_INSTANCE
     */
    INSTANCE,
    /**
     * The host and instance where an application is running, in the format <code>host:instance</code>. E.g. <code>
     * fesn01:9120</code>.
     */
    HOST_INSTANCE,
    /**
     * The Flow Id of the related service request/response.
     */
    FLOW_ID,
    /**
     * The Execution Context of the related service request/response.
     */
    EXECUTION_CONTEXT;

    /**
     * Returns a string representation of this header.
     *
     * @return  the string representation of the header, as written in a HTTP Request.
     */
    public String toString() {
        return "x-" + name().toLowerCase().replace('_', '-');
    }

    /**
     * Returns the value contained in the specified HTTP Request, for this header.
     *
     * @param   request  the HTTP Request
     *
     * @return  the corresponding value, or <code>null</code> if this header is not present in the request.
     */
    public String get(final HttpServletRequest request) {
        Preconditions.checkNotNull(request, "request can't be null");

        String value = request.getHeader(toString());

        // Some headers need to be treated in a special way
        if (value == null) {
            switch (this) {

                case HOST :
                case INSTANCE :
                    value = getHostOrInstance(request);
                    break;

                case FLOW_ID :
                    value = request.getHeader(toString().toUpperCase());
                    break;

                default :
                    break;
            }
        }

        return value;
    }

    /**
     * Used when this header is a HOST or INSTANCE. Since in the HTTP Request there is only a HOST_INSTANCE header,
     * returns the corresponding part accordingly.
     *
     * @param   request  the HTTP request.
     *
     * @return  the HOST or INSTANCE part, or <code>null</code> if HOST_INSTANCE is not present.
     *
     * @see     HOST_INSTANCE
     */
    private String getHostOrInstance(final HttpServletRequest request) {
        String value = request.getHeader(HOST_INSTANCE.toString());
        if (value == null) {
            return null;
        }

        int separatorIndex = value.indexOf(':');
        if (separatorIndex < 0) {
            return null;
        }

        // If it's a HOST, it's left from the : - otherwise it's on the right.
        if (HOST.equals(this)) {
            return value.substring(0, separatorIndex);
        } else {
            return value.substring(separatorIndex + 1, value.length());
        }
    }

    /**
     * Sets the specified value for this header in the provided HTTP response.
     *
     * <p>If the response already had a HTTP header with the same name, its value is overwritten.
     *
     * @param  response  the HTTP Response
     * @param  value     the value to set
     */
    public void set(final HttpServletResponse response, final String value) {
        Preconditions.checkNotNull(response, "response can't be null");
        Preconditions.checkNotNull(value, "value can't be null");

        response.setHeader(toString(), value);
    }
}
