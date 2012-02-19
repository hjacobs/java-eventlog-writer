package de.zalando.zomcat.cxf.authorization;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.junit.Ignore;

@Ignore
public class AccessConfigForTest implements AccessConfig {

    @Override
    public List<String> getAllowedRoles() {
        return newArrayList(WebServiceAuthorizationInterceptorTest.GIFT_VOUCHER_SERVICE + " "
                    + WebServiceAuthorizationInterceptorTest.HOST_IP_PATTERN_1 + " ",


                WebServiceAuthorizationInterceptorTest.GIFT_VOUCHER_SERVICE + " "
                    + WebServiceAuthorizationInterceptorTest.HOST_IP_PATTERN_2 + " "
                    + WebServiceAuthorizationInterceptorTest.LOAD_BALANCER_IP_PATTERN_1,


                WebServiceAuthorizationInterceptorTest.ORDER_SERVICE + " "
                    + WebServiceAuthorizationInterceptorTest.HOST_IP_PATTERN_3 + " ",


                WebServiceAuthorizationInterceptorTest.ORDER_SERVICE + " "
                    + WebServiceAuthorizationInterceptorTest.HOST_IP_PATTERN_4 + " ");
    }

    @Override
    public List<String> getDeniedRoles() {
        return newArrayList(WebServiceAuthorizationInterceptorTest.ORDER_SERVICE + " "
                    + WebServiceAuthorizationInterceptorTest.HOST_IP_PATTERN_1 + " ");
    }

    @Override
    public WebServiceAuthorizationLevel getWebServiceAuthorizationLevel() {
        return WebServiceAuthorizationLevel.ENABLED;
    }

}
