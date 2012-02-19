package de.zalando.zomcat.cxf.authorization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.cxf.interceptor.security.AccessDeniedException;

import org.junit.Before;
import org.junit.Test;

import de.zalando.zomcat.cxf.authorization.impl.WebServiceAuthorizationLogServiceImpl;

/**
 * Tests if a user request for a WS gets access or not.
 *
 * @author  jbuck
 */
public class WebServiceAuthorizationInterceptorTest {

    public static final String HOST_IP_PATTERN_1 = "10.*.10.1";
    public static final String HOST_IP_PATTERN_2 = "10.*.10.3";
    public static final String HOST_IP_PATTERN_3 = "10.*.*.1";
    public static final String HOST_IP_PATTERN_4 = "10.*.10.1";
    public static final String LOAD_BALANCER_IP_PATTERN_1 = "192.#.1";

    public static final String ORDER_SERVICE = "/ws/orderService";
    public static final String GIFT_VOUCHER_SERVICE = "/ws/giftVoucherService";
    public static final String SECRET_SERVICE = "/ws/secretService";

    public static final String VALID_HOST_IP = "10.20.10.1";
    public static final String VALID_HOST_IP_2 = "10.20.10.3";
    public static final String UNVALID_HOST_IP = "10.20.10.2";
    public static final String VALID_LOAD_BALANCER_IP = "192.20.10.1";
    public static final String UNVALID_LOAD_BALANCER_IP = "192.20.10.2";

    private String requestHostIp;
    private WebServiceCallData incomingRequestData;
    private final List<WebServiceCallData> allowedList = new ArrayList<WebServiceCallData>();
    private final List<WebServiceCallData> deniedList = new ArrayList<WebServiceCallData>();

    private final List<String> listFromAppConfig = new ArrayList<String>();

    private final WebServiceAuthorizationInInterceptor webServiceAuthorizationInterceptor =
        new WebServiceAuthorizationInInterceptor();

    @Before
    public void setUp() {
        incomingRequestData = new WebServiceCallData(GIFT_VOUCHER_SERVICE, requestHostIp, "");

        allowedList.add(new WebServiceCallData(GIFT_VOUCHER_SERVICE, HOST_IP_PATTERN_1, "*"));
        allowedList.add(new WebServiceCallData(GIFT_VOUCHER_SERVICE, HOST_IP_PATTERN_1, LOAD_BALANCER_IP_PATTERN_1));

        allowedList.add(new WebServiceCallData(ORDER_SERVICE, HOST_IP_PATTERN_3, "*"));
        allowedList.add(new WebServiceCallData(ORDER_SERVICE, HOST_IP_PATTERN_4, "*"));

        deniedList.add(new WebServiceCallData(ORDER_SERVICE, HOST_IP_PATTERN_1, ""));

        listFromAppConfig.add("");

        WebServiceAuthorizationLogService webServiceAuthorizationLogService =
            new WebServiceAuthorizationLogServiceImpl();
        webServiceAuthorizationInterceptor.setWebServiceAuthorizationLogService(webServiceAuthorizationLogService);
    }

    // *****************************************************************************
    // testing setDataFromWebserviceRequest
    // *****************************************************************************

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testSetDataFromWebserviceRequestWithoutXff() {
        Map<String, List<String>> protocolHeaders = newHashMap();
        protocolHeaders.put(WebServiceAuthorizationInInterceptor.X_FORWARDED_FOR, (List) newArrayList());

        WebServiceCallData webServiceCallData = webServiceAuthorizationInterceptor.getDataFromRequest(
                GIFT_VOUCHER_SERVICE, protocolHeaders, VALID_HOST_IP);
        assertEquals(new WebServiceCallData(GIFT_VOUCHER_SERVICE, VALID_HOST_IP, null), webServiceCallData);
    }

// @SuppressWarnings({ "unchecked", "rawtypes" })
// @Test
// public void testSetDataFromWebserviceRequestWithoutXffWithPortnumber() {
// Map<String, List<String>> protocolHeaders = newHashMap();
// protocolHeaders.put(WebServiceAuthorizationInInterceptor.HOST, newArrayList(VALID_HOST_IP + ":8080"));
// protocolHeaders.put(WebServiceAuthorizationInInterceptor.X_FORWARDED_FOR, (List) newArrayList());
// WebServiceCallData webServiceCallData = webServiceAuthorizationInterceptor.getDataFromRequest(
// GIFT_VOUCHER_SERVICE, protocolHeaders, VALID_HOST_IP + ":8080");
// assertEquals(new WebServiceCallData(GIFT_VOUCHER_SERVICE, VALID_HOST_IP, null), webServiceCallData);
// }

    @Test
    public void testSetDataFromWebserviceRequestWithXff() {
        Map<String, List<String>> protocolHeaders = newHashMap();
        protocolHeaders.put(WebServiceAuthorizationInInterceptor.X_FORWARDED_FOR,
            newArrayList(VALID_LOAD_BALANCER_IP, UNVALID_LOAD_BALANCER_IP));

        WebServiceCallData webServiceCallData = webServiceAuthorizationInterceptor.getDataFromRequest(
                GIFT_VOUCHER_SERVICE, protocolHeaders, VALID_HOST_IP);
        assertEquals(new WebServiceCallData(GIFT_VOUCHER_SERVICE, VALID_LOAD_BALANCER_IP, VALID_HOST_IP),
            webServiceCallData);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test(expected = IllegalArgumentException.class)
    public void testSetDataFromWebserviceRequestNoEndpoint() {
        Map<String, List<String>> protocolHeaders = newHashMap();
        protocolHeaders.put(WebServiceAuthorizationInInterceptor.X_FORWARDED_FOR, (List) newArrayList());

        WebServiceCallData webServiceCallData = webServiceAuthorizationInterceptor.getDataFromRequest(null,
                protocolHeaders, VALID_HOST_IP);
        assertEquals(new WebServiceCallData(GIFT_VOUCHER_SERVICE, VALID_HOST_IP, null), webServiceCallData);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test(expected = IllegalArgumentException.class)
    public void testSetDataFromWebserviceRequestNoHostIp() {
        Map<String, List<String>> protocolHeaders = newHashMap();
        protocolHeaders.put(WebServiceAuthorizationInInterceptor.X_FORWARDED_FOR, (List) newArrayList());

        WebServiceCallData webServiceCallData = webServiceAuthorizationInterceptor.getDataFromRequest(
                GIFT_VOUCHER_SERVICE, protocolHeaders, "");
        assertEquals(new WebServiceCallData(GIFT_VOUCHER_SERVICE, VALID_HOST_IP, null), webServiceCallData);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetDataFromWebserviceRequestNoProtocolHeader() {
        Map<String, List<String>> protocolHeaders = newHashMap();
        WebServiceCallData webServiceCallData = webServiceAuthorizationInterceptor.getDataFromRequest(
                GIFT_VOUCHER_SERVICE, protocolHeaders, "");
        assertEquals(new WebServiceCallData(GIFT_VOUCHER_SERVICE, VALID_HOST_IP, null), webServiceCallData);
    }

    // *****************************************************************************
    // testing createWebServiceCallDataFromReques
    // *****************************************************************************

    @Test
    public void testCreateWebServiceCallDataFromRequest() {
        WebServiceCallData callDataFromRequest = webServiceAuthorizationInterceptor.createWebServiceCallDataFromRequest(
                GIFT_VOUCHER_SERVICE, VALID_HOST_IP, "");
        assertEquals(new WebServiceCallData(GIFT_VOUCHER_SERVICE, VALID_HOST_IP, null), callDataFromRequest);
    }

    @Test
    public void testCreateWebServiceCallDataFromRequestWithXff() {
        WebServiceCallData callDataFromRequest = webServiceAuthorizationInterceptor.createWebServiceCallDataFromRequest(
                GIFT_VOUCHER_SERVICE, VALID_HOST_IP, VALID_LOAD_BALANCER_IP);
        assertEquals(new WebServiceCallData(GIFT_VOUCHER_SERVICE, VALID_LOAD_BALANCER_IP, VALID_HOST_IP),
            callDataFromRequest);
    }

    // *****************************************************************************
    // testing denied list, all positive tests but with the valid clientIp added to
    // the deniedList
    // *****************************************************************************
    /**
     * valid clientIp that gets inserted into the denyList.
     */
    @Test(expected = AccessDeniedException.class)
    public void testValidHostIpWithDenyEntry() {
        incomingRequestData.setClientIp(VALID_HOST_IP);
        deniedList.add(new WebServiceCallData(GIFT_VOUCHER_SERVICE, VALID_HOST_IP, ""));
        webServiceAuthorizationInterceptor.setStatus(WebServiceAuthorizationLevel.ENABLED);
        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData, allowedList, deniedList);
    }

    @Test(expected = AccessDeniedException.class)
    public void testDenyEverything() {
        incomingRequestData.setClientIp(UNVALID_HOST_IP);
        incomingRequestData.setEndpoint(GIFT_VOUCHER_SERVICE);
        incomingRequestData.setLoadBalancerIp("");
        deniedList.clear();
        deniedList.add(new WebServiceCallData("#", "#", "#"));
        allowedList.clear();
        allowedList.add(new WebServiceCallData(GIFT_VOUCHER_SERVICE, UNVALID_HOST_IP, "#"));
        webServiceAuthorizationInterceptor.setStatus(WebServiceAuthorizationLevel.ENABLED);

        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData, allowedList, deniedList);
    }

    @Test
    public void testDenyNothingAllowOneIp() {
        incomingRequestData.setClientIp(UNVALID_HOST_IP);
        incomingRequestData.setEndpoint(GIFT_VOUCHER_SERVICE);
        incomingRequestData.setLoadBalancerIp("");
        deniedList.clear();
        allowedList.clear();
        allowedList.add(new WebServiceCallData(GIFT_VOUCHER_SERVICE, UNVALID_HOST_IP, "#"));
        webServiceAuthorizationInterceptor.setStatus(WebServiceAuthorizationLevel.ENABLED);

        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData, allowedList, deniedList);
    }

    @Test
    public void testDenyNothingAllowOneIpWithWhitespaces() {
        incomingRequestData = new WebServiceCallData("  " + GIFT_VOUCHER_SERVICE + "   ", UNVALID_HOST_IP, "");
        deniedList.clear();
        allowedList.clear();
        allowedList.add(new WebServiceCallData(GIFT_VOUCHER_SERVICE, UNVALID_HOST_IP, "#"));
        webServiceAuthorizationInterceptor.setStatus(WebServiceAuthorizationLevel.ENABLED);

        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData, allowedList, deniedList);
    }

    @Test(expected = AccessDeniedException.class)
    public void testAllowEverythingBesidesOneIp() {
        incomingRequestData.setClientIp(UNVALID_HOST_IP);
        incomingRequestData.setEndpoint(GIFT_VOUCHER_SERVICE);
        incomingRequestData.setLoadBalancerIp("");

        // deny one IP
        deniedList.clear();
        deniedList.add(new WebServiceCallData(GIFT_VOUCHER_SERVICE, UNVALID_HOST_IP, "#"));

        // allow everything
        allowedList.clear();
        allowedList.add(new WebServiceCallData("#", "#", "#"));
        webServiceAuthorizationInterceptor.setStatus(WebServiceAuthorizationLevel.ENABLED);

        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData, allowedList, deniedList);
    }

    /**
     * valid clientIp that gets inserted into the denyList and valid loadbalancerIp.
     */
    @Test(expected = AccessDeniedException.class)
    public void testValidHostIpAndValidLoadBalancerIpWithDenyEntry() {
        incomingRequestData.setClientIp(VALID_HOST_IP);
        incomingRequestData.setLoadBalancerIp(VALID_LOAD_BALANCER_IP);
        deniedList.add(new WebServiceCallData(GIFT_VOUCHER_SERVICE, VALID_HOST_IP, ""));
        webServiceAuthorizationInterceptor.setStatus(WebServiceAuthorizationLevel.ENABLED);
        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData, allowedList, deniedList);
    }

    /**
     * valid clientIp that gets inserted into the denyList but authorization is disabled.
     */
    @Test
    public void testValidHostIpAndWsAuthorizationDisabledWithDenyEntry() {
        incomingRequestData.setClientIp(VALID_HOST_IP);
        deniedList.add(new WebServiceCallData(GIFT_VOUCHER_SERVICE, VALID_HOST_IP, ""));

        webServiceAuthorizationInterceptor.setStatus(WebServiceAuthorizationLevel.LOGGING_MODE);
        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData, allowedList, deniedList);
        assertTrue("if the webService authorization is disabled a user request with not valid clientIp should "
                + "just lead to a logging and not throw an Exception", true);
    }

    // *****************************************************************************
    // testing parseAppConfigData
    // *****************************************************************************

    @Test
    public void testParseAppConfigData() {
        AccessConfigForTest testAccessConfig = new AccessConfigForTest();
        List<WebServiceCallData> webServiceCallData = webServiceAuthorizationInterceptor.parseAppConfigData(
                testAccessConfig.getAllowedRoles());

        assertTrue(String.format("parsing should generate four webServiceCallData objects, but size = [%s]",
                webServiceCallData.size()), webServiceCallData.size() == 4);

        assertEquals(new WebServiceCallData(GIFT_VOUCHER_SERVICE, HOST_IP_PATTERN_1, null), webServiceCallData.get(0));
        assertEquals(new WebServiceCallData(GIFT_VOUCHER_SERVICE, HOST_IP_PATTERN_2, LOAD_BALANCER_IP_PATTERN_1),
            webServiceCallData.get(1));
        assertEquals(new WebServiceCallData(ORDER_SERVICE, HOST_IP_PATTERN_3, null), webServiceCallData.get(2));
        assertEquals(new WebServiceCallData(ORDER_SERVICE, HOST_IP_PATTERN_4, null), webServiceCallData.get(3));
    }

    // *****************************************************************************
    // testing allowed list
    // *****************************************************************************

    /**
     * this test calls the isUserAllowedToUseWebservice(incomingRequestData) function with a valid clientIp.
     */
    @Test
    public void testUpperValidationMethodWithValidHostIp() {
        incomingRequestData.setClientIp(VALID_HOST_IP);
        incomingRequestData.setLoadBalancerIp(null);

        AccessConfigForTest testAccessConfig = new AccessConfigForTest();
        webServiceAuthorizationInterceptor.setApplicationConfig(testAccessConfig);
        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData);
        assertTrue("user with valid clientIp should access WS", true);
    }

    /**
     * clientIp is valid and endPoint is wildCard.
     */
    @Test
    public void testWildcardEndpoint() {
        String tempHostIp = "10.22.17.1";
        incomingRequestData.setClientIp(tempHostIp);
        incomingRequestData.setLoadBalancerIp(null);

        WebServiceCallData webServiceCallData = new WebServiceCallData("#", tempHostIp, null);
        allowedList.add(webServiceCallData);

        webServiceAuthorizationInterceptor.setStatus(WebServiceAuthorizationLevel.ENABLED);
        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData, allowedList, deniedList);
        assertTrue("user with valid clientIp and endPoint wildCard should access WS", true);

        // remove wildCard endpoint
        allowedList.remove(webServiceCallData);
    }

    /**
     * valid clientIp.
     */
    @Test
    public void testValidHostIp() {
        incomingRequestData.setClientIp(VALID_HOST_IP);
        incomingRequestData.setLoadBalancerIp(null);

        webServiceAuthorizationInterceptor.setStatus(WebServiceAuthorizationLevel.ENABLED);
        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData, allowedList, deniedList);
        assertTrue("user with valid clientIp should access WS", true);
    }

    /**
     * valid clientIp and valid loadbalancerIp.
     */
    @Test
    public void testValidHostIpAndValidLoadBalancerIp() {
        incomingRequestData.setClientIp(VALID_HOST_IP);
        incomingRequestData.setLoadBalancerIp(VALID_LOAD_BALANCER_IP);

        webServiceAuthorizationInterceptor.setStatus(WebServiceAuthorizationLevel.ENABLED);
        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData, allowedList, deniedList);
        assertTrue("user with valid clientIp and valid loadBalancerIp should access WS", true);
    }

    /**
     * valid clientIp and not valid loadbalancerIp.
     */
    @Test(expected = AccessDeniedException.class)
    public void testValidHostIpAndUnvalidLoadBalancerIp() {
        incomingRequestData.setClientIp(VALID_HOST_IP_2);
        incomingRequestData.setLoadBalancerIp(UNVALID_LOAD_BALANCER_IP);

        webServiceAuthorizationInterceptor.setStatus(WebServiceAuthorizationLevel.ENABLED);
        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData, allowedList, deniedList);
    }

    /**
     * not valid clientIp and valid loadbalancerIp.
     */
    @Test(expected = AccessDeniedException.class)
    public void testUnvalidHostIpAndValidLoadBalancerIp() {
        incomingRequestData.setClientIp(UNVALID_HOST_IP);
        incomingRequestData.setLoadBalancerIp(VALID_LOAD_BALANCER_IP);
        webServiceAuthorizationInterceptor.setStatus(WebServiceAuthorizationLevel.ENABLED);
        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData, allowedList, deniedList);
    }

    /**
     * not valid clientIp and not valid loadbalancerIp.
     */
    @Test(expected = AccessDeniedException.class)
    public void testUnvalidHostIpAndUnvalidLoadBalancerIp() {
        incomingRequestData.setClientIp(UNVALID_HOST_IP);
        incomingRequestData.setLoadBalancerIp(UNVALID_LOAD_BALANCER_IP);
        webServiceAuthorizationInterceptor.setStatus(WebServiceAuthorizationLevel.ENABLED);
        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData, allowedList, deniedList);
    }

    /**
     * valid clientIp but wrong endPoint.
     */
    @Test(expected = AccessDeniedException.class)
    public void testValidHostIpAndWrongEndpoint() {
        incomingRequestData.setClientIp(VALID_HOST_IP);
        incomingRequestData.setEndpoint(SECRET_SERVICE);
        webServiceAuthorizationInterceptor.setStatus(WebServiceAuthorizationLevel.ENABLED);
        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData, allowedList, deniedList);
    }

    /**
     * not valid clientIp and webService authorization is disabled.
     */
    @Test
    public void testUnvalidHostIpAndWsAuthorizationDisabled() {
        incomingRequestData.setClientIp(UNVALID_HOST_IP);
        webServiceAuthorizationInterceptor.setStatus(WebServiceAuthorizationLevel.LOGGING_MODE);
        webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(incomingRequestData, allowedList, deniedList);
        assertTrue("if the webService authorization is disabled a user request with not valid clientIp should "
                + "just lead to a logging and not throw an Exception", true);
    }
}
