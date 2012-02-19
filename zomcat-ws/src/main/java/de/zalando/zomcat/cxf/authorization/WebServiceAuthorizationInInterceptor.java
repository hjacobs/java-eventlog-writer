package de.zalando.zomcat.cxf.authorization;

import static com.google.common.base.Objects.firstNonNull;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import org.apache.cxf.interceptor.security.AbstractAuthorizingInInterceptor;
import org.apache.cxf.interceptor.security.AccessDeniedException;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

/**
 * This interceptor checks if the request's host IP and / or XFF (xForwardingFor) is registered for the requested
 * WS-endPoint in the appConfig.
 *
 * @author  jbuck
 */
public class WebServiceAuthorizationInInterceptor extends AbstractAuthorizingInInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(WebServiceAuthorizationInInterceptor.class);

    @VisibleForTesting
    public static final String X_FORWARDED_FOR = "x-forwarded-for";
    public static boolean enabled = true;
    private static final String LOGGING_MODE = "LOGGING_MODE: ";

    private WebServiceAuthorizationLogService webServiceAuthorizationLogService;
    private WebServiceAuthorizationLevel authorizationLevel;
    private AccessConfig accessConfig;

    public WebServiceAuthorizationInInterceptor() {
        super();
    }

    public WebServiceAuthorizationInInterceptor(final AccessConfig accessConfig) {
        super();
        this.accessConfig = accessConfig;
    }

    /**
     * This method must be overridden because the inherited class uses it in its own implementation of handleMessage().
     * But as the authorization process is completely done in the handleMessage() method of this class, the upper class'
     * handleMessage() method is not used. So we do not need this method either.
     */
    @Override
    protected List<String> getExpectedRoles(final Method method) {
        return null;
    }

    /**
     * Parses the endPoint, clientIp and the loadBalancerIp from the users request data and afterwards checks if this
     * request is allowed to call this WS's endPoint. The allowed or denied host and / or loadBalancerIps are available
     * through the appConfig. If the user is not allowed to use the WS an AccessDeniedException will be thrown.
     */
    @Override
    public void handleMessage(final Message message) {

        // is this interceptor enabled
        if (!isInterceptorEnabled()) {
            return;
        }

        // check message
        if (message == null) {
            LOG.error("message is null in WebServiceAuthorizationInInterceptor so no authorization will be done");
            return;
        }

        // get httpServletRequest
        HttpServletRequest httpServletRequest = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);

        // if request is null, this is a response, so we won't handle this message
        if (httpServletRequest == null) {
            return;
        }

        // get data from request message
        WebServiceCallData authorizationRequest;
        try {
            authorizationRequest = getDataFromRequest(message, httpServletRequest);
        } catch (IllegalArgumentException e) {
            String errorMessage = "failed to authorize ws call because data needed for "
                    + "validation is missing in userRequest";

            // get data for logging
            String remoteAddress = httpServletRequest.getRemoteAddr();
            String endpoint = (String) message.get(Message.REQUEST_URI);

            // log denied call
            webServiceAuthorizationLogService.logDeniedWebServiceCall(WebServiceAuthorizationDenialType.MISSING_DATA,
                remoteAddress, "", endpoint, false);

            if (WebServiceAuthorizationLevel.LOGGING_MODE.equals(authorizationLevel)) {
                LOG.error(LOGGING_MODE + errorMessage, e);
                return;
            } else {
                LOG.error("{}", new AccessDeniedException(errorMessage), e);
                throw new AccessDeniedException(errorMessage + e.getMessage());
            }
        }

        // log start of authorization
        LOG.trace("starting authorization of WS call for endPoint {} from {} via {}",
            new Object[] {
                authorizationRequest.getEndpoint(), authorizationRequest.getClientIp(),
                authorizationRequest.getLoadBalancerIp()
            });

        // check if user is allowed to use this WS
        isUserAllowedToUseWebservice(authorizationRequest);
        LOG.trace("ws call {} has been successfully authorized", authorizationRequest);
    }

    /**
     * Checks the ENABLED flag, that can be set by the WebServiceAuthorizationInterceptorMBean and afterwards the
     * authorizationLevel from the appConfig.
     *
     * @return
     */
    private boolean isInterceptorEnabled() {

        // check if interceptor has been disabled by mBean
        if (!enabled) {
            LOG.debug("the WebServiceAuthorizationInInterceptor is completely disabled before "
                    + "checking the appConfig");

            return false;
        }

        // check if interceptor is enabled
        authorizationLevel = accessConfig.getWebServiceAuthorizationLevel();
        if ((authorizationLevel == null) || WebServiceAuthorizationLevel.DISABLED.equals(authorizationLevel)) {
            return false;
        }

        return true;
    }

    /**
     * Gets the data out of the request message and returns WebServiceCallData.
     *
     * @param   message
     *
     * @return  WebServiceCallData
     */
    public WebServiceCallData getDataFromRequest(final Message message, final HttpServletRequest httpServletRequest) {

        // get remoteAddr from httpServletRequest
        String remoteAddr = httpServletRequest.getRemoteAddr();
        if (remoteAddr == null) {
            logAndThrowIllegalArgumentException("remoteAddress must not be null");
        }

        // cut of port number
        if (remoteAddr.contains(":")) {
            remoteAddr = remoteAddr.substring(0, remoteAddr.lastIndexOf(':'));
        }

        // get webserviceEndpoint from message
        String endpoint = (String) message.get(Message.REQUEST_URI);

        // get host and XFF from message
        @SuppressWarnings("unchecked")
        Map<String, List<String>> protocolHeaders = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);

        WebServiceCallData webServiceCallData = getDataFromRequest(endpoint, protocolHeaders,
                firstNonNull(remoteAddr, ""));
        return webServiceCallData;
    }

    /**
     * Gets the relevant data from the message and returns WebServiceCallData.
     *
     * @return  WebServiceCallData
     */
    @VisibleForTesting
    public WebServiceCallData getDataFromRequest(final String endpoint, final Map<String, List<String>> protocolHeaders,
            final String remoteAddress) {

        // check if relevant data for authorization is given
        if (Strings.isNullOrEmpty(endpoint)) {
            logAndThrowIllegalArgumentException("endpoint must not be empty");
        }

        if (Strings.isNullOrEmpty(remoteAddress)) {
            logAndThrowIllegalArgumentException("remoteAddress must not be empty");
        }

        if (MapUtils.isEmpty(protocolHeaders)) {
            logAndThrowIllegalArgumentException("protocolHeaders must not be empty");
        }

        // get xForwardedForIp from request
        String xForwardedFor = "";
        List<String> xForwardedForList = protocolHeaders.get(X_FORWARDED_FOR);
        if (CollectionUtils.isNotEmpty(xForwardedForList)) {
            xForwardedFor = xForwardedForList.get(0); // the first entry is the client
        }

        return createWebServiceCallDataFromRequest(endpoint, remoteAddress, xForwardedFor);
    }

    /**
     * Logs errorMessage and throws IllegalArgumentException.
     *
     * @param  logMessage
     */
    private static void logAndThrowIllegalArgumentException(final String logMessage) {
        LOG.error(logMessage);
        throw new IllegalArgumentException(logMessage);
    }

    /**
     * Creates the WebServiceCallData object out of the request data. If we have just the hostId this is clientsIp. If
     * the xForwardedForIp is set the first IP is the the clientIp, and the clientIp is the loadBalancerIp.
     *
     * @param   endpoint
     * @param   remoteAddress
     * @param   xForwardedFor
     *
     * @return  WebServiceCallData
     */
    @VisibleForTesting
    public static WebServiceCallData createWebServiceCallDataFromRequest(final String endpoint,
            final String remoteAddress, final String xForwardedFor) {

        String loadBalancerIp = "";
        String clientIp = "";

        if (Strings.isNullOrEmpty(xForwardedFor)) {
            clientIp = remoteAddress;
        } else {
            clientIp = xForwardedFor;
            loadBalancerIp = remoteAddress;
        }

        return new WebServiceCallData(endpoint, clientIp, loadBalancerIp);
    }

    /**
     * Checks if request's IP and xFF occur in the application.properties.
     *
     * @param  authorizationRequest
     */
    @VisibleForTesting
    public void isUserAllowedToUseWebservice(final WebServiceCallData authorizationRequest) {

        // parse appConfig data
        List<WebServiceCallData> allowedList = parseAppConfigData(accessConfig.getAllowedRoles());
        List<WebServiceCallData> deniedList = parseAppConfigData(accessConfig.getDeniedRoles());

        isUserAllowedToUseWebservice(authorizationRequest, allowedList, deniedList);
    }

    /**
     * Checks if user is allowed to use the WS and if WS authorization is enabled. If so an AccessDeniedException will
     * be throws in case of denial, otherwise there will be a logging.
     *
     * @param  authorizationRequest
     * @param  allowedList
     * @param  deniedList
     * @param  isWsAuthorizationEnabled
     */
    @VisibleForTesting
    public void isUserAllowedToUseWebservice(final WebServiceCallData authorizationRequest,
            final List<WebServiceCallData> allowedList, final List<WebServiceCallData> deniedList) {

        try {
            checkAuthorization(authorizationRequest, allowedList, deniedList);
        } catch (AccessDeniedException e) {
            webServiceAuthorizationLogService.logDeniedWebServiceCall(WebServiceAuthorizationDenialType.IP_NOT_ALLOWED,
                authorizationRequest.getClientIp(), authorizationRequest.getLoadBalancerIp(),
                authorizationRequest.getEndpoint(), false);

            String logMessage = String.format("Unauthorized WS call for endPoint %s from client %s via %s",
                    authorizationRequest.getEndpoint(), authorizationRequest.getClientIp(),
                    authorizationRequest.getLoadBalancerIp());

            // if wsAuthentication is enabled throw exception, otherwise just log
            if (WebServiceAuthorizationLevel.LOGGING_MODE.equals(authorizationLevel)) {
                LOG.error(LOGGING_MODE + logMessage, e);
            } else {
                throw e;
            }
        }
    }

    /**
     * User is allowed to access WS if he occurs in the allowedList besides he is also contained in the denyList.
     *
     * @param   authorizationRequest
     * @param   allow
     * @param   deny
     *
     * @return
     */
    private static void checkAuthorization(final WebServiceCallData authorizationRequest,
            final List<WebServiceCallData> allow, final List<WebServiceCallData> deny) {

        for (WebServiceCallData webserviceCallData : deny) {
            if (webserviceCallData.contains(authorizationRequest)) {
                String message = String.format("webservice request will be denied, because the deny pattern: [%s]"
                            + " matches the request: [%s]", webserviceCallData, authorizationRequest);
                if (LOG.isDebugEnabled()) {
                    LOG.debug(message);
                }

                throw new AccessDeniedException(message);
            }
        }

        for (WebServiceCallData webserviceCallData : allow) {
            if (webserviceCallData.contains(authorizationRequest)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(String.format(
                            "webservice request will be accepted, because the allow pattern: [%s]"
                                + " matches the request: [%s]", webserviceCallData, authorizationRequest));
                }

                return;
            }
        }

        String message = String.format("webservice request will be denied, because the request: [%s]"
                    + " does not match any allowed pattern", authorizationRequest);
        throw new AccessDeniedException(message);
    }

    /**
     * Splits the attributes (i.e. endPoint, clientIp, loadBalancerIp) from the appConfig list at the whitespace
     * characters and generates WebserviceCallData elements out of it.
     *
     * @param   appConfigList
     *
     * @return  list of WebserviceCallData
     */
    @VisibleForTesting
    public static List<WebServiceCallData> parseAppConfigData(final List<String> appConfigList) {
        List<WebServiceCallData> webserviceCallDatas = new ArrayList<WebServiceCallData>();
        for (String roleAllowed : appConfigList) {
            Splitter splitter = Splitter.on(" ").trimResults();
            Iterator<String> split = splitter.split(roleAllowed).iterator();
            WebServiceCallData callData = new WebServiceCallData(split.next(), split.next(), split.next());
            webserviceCallDatas.add(callData);
        }

        return webserviceCallDatas;
    }

    public AccessConfig getApplicationConfig() {
        return accessConfig;
    }

    public void setApplicationConfig(final AccessConfig applicationConfig) {
        this.accessConfig = applicationConfig;
    }

    public WebServiceAuthorizationLevel getStatus() {
        return authorizationLevel;
    }

    public void setStatus(final WebServiceAuthorizationLevel status) {
        this.authorizationLevel = status;
    }

    public WebServiceAuthorizationLogService getWebServiceAuthorizationLogService() {
        return webServiceAuthorizationLogService;
    }

    public void setWebServiceAuthorizationLogService(
            final WebServiceAuthorizationLogService webServiceAuthorizationLogService) {
        this.webServiceAuthorizationLogService = webServiceAuthorizationLogService;
    }
}
