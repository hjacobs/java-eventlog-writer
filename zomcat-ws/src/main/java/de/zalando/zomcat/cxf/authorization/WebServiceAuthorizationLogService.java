package de.zalando.zomcat.cxf.authorization;

/**
 * Used to log denied WS calls into separate logFile.
 *
 * @author  jbuck
 */
public interface WebServiceAuthorizationLogService {

    /**
     * Logs a denied webServiceCall.
     *
     * @param  type
     * @param  localAddress
     * @param  remoteAddress
     * @param  clientIp
     * @param  loadBalancerIp
     * @param  endpoint
     * @param  isFatal
     */
    void logDeniedWebServiceCall(final WebServiceAuthorizationDenialType type, final String clientIp,
            final String loadBalancerIp, final String endpoint, final boolean isFatal);

}
