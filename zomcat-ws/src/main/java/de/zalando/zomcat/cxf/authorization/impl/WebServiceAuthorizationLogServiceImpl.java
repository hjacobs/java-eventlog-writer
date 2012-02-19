package de.zalando.zomcat.cxf.authorization.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

import de.zalando.zomcat.cxf.authorization.WebServiceAuthorizationDenialType;
import de.zalando.zomcat.cxf.authorization.WebServiceAuthorizationLogService;

/**
 * Used to log denied WS calls into separate logFile.
 *
 * @author  jbuck
 */
public class WebServiceAuthorizationLogServiceImpl implements WebServiceAuthorizationLogService {

    private static final Logger LOG = LoggerFactory.getLogger(WebServiceAuthorizationLogServiceImpl.class);

    @Override
    public void logDeniedWebServiceCall(final WebServiceAuthorizationDenialType type, final String clientIp,
            final String loadBalancerIp, final String endpoint, final boolean isFatal) {

        String[] data = new String[] {type.name(), clientIp, loadBalancerIp, endpoint};

        String logData = Joiner.on(",").useForNull("").join(data);
        if (isFatal) {
            LOG.error(logData);
        } else {
            LOG.info(logData);
        }
    }
}
