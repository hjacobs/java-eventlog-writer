package de.zalando.zomcat.cxf.authorization;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cxf.interceptor.security.AccessDeniedException;

import org.apache.log4j.Logger;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.zalando.utils.StopWatch;

/**
 * Load-test for webServiceAuthorizationInterceptor.
 *
 * @author  jbuck
 */
public class WebServiceAuthorizationInterceptorLoadTest {

    private static final Logger LOG = Logger.getLogger(WebServiceAuthorizationInterceptorLoadTest.class);
    private static final int NUMBER_OF_THREADS = 100;
    private static final int LOOPS = 1000;
    private int counter = 0;

    private final WebServiceAuthorizationInInterceptor webServiceAuthorizationInterceptor =
        new WebServiceAuthorizationInInterceptor();

    @Test
    @Ignore("No load tests in unit tests")
    public void test() {

        // initialize context
        AccessConfigForTest accessConfig = new AccessConfigForTest();
        webServiceAuthorizationInterceptor.setApplicationConfig(accessConfig);

        Map<String, List<String>> protocolHeaders = newHashMap();
        protocolHeaders.put(WebServiceAuthorizationInInterceptor.X_FORWARDED_FOR,
            newArrayList(WebServiceAuthorizationInterceptorTest.VALID_HOST_IP,
                WebServiceAuthorizationInterceptorTest.UNVALID_LOAD_BALANCER_IP));

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // start threads
        List<Thread> runningThreads = Lists.newArrayList();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            Thread thread = new MyThread(protocolHeaders);
            thread.start();
            runningThreads.add(thread);
        }

        // wait until all threads are dead
        LOG.debug("runningThreads.size() = " + runningThreads.size());
        while (runningThreads.size() > 0) {
            Iterator<Thread> iterator = runningThreads.iterator();
            while (iterator.hasNext()) {
                Thread thread = iterator.next();
                if (!thread.isAlive()) {
                    iterator.remove();
                }
            }
        }

        LOG.debug("runningThreads.size() = " + runningThreads.size());
        LOG.debug(String.format("total time of test = [%s], number of failed authorizations = [%s]",
                stopWatch.getTimeFormated(), counter));
    }

    /**
     * Thread that executes the methods getDataFromRequest and isUserAllowedToUseWebservice for 'LOOPS'-times.
     *
     * @author  JJ
     */
    private class MyThread extends Thread {
        Map<String, List<String>> protocolHeaders = newHashMap();

        public MyThread(final Map<String, List<String>> protocolHeaders) {
            super();
            this.protocolHeaders = protocolHeaders;
        }

        @Override
        public void run() {
            for (int i = 0; i < LOOPS; i++) {
                WebServiceCallData webServiceCallData = webServiceAuthorizationInterceptor.getDataFromRequest(
                        WebServiceAuthorizationInterceptorTest.GIFT_VOUCHER_SERVICE, protocolHeaders,
                        WebServiceAuthorizationInterceptorTest.VALID_HOST_IP);
                try {
                    webServiceAuthorizationInterceptor.isUserAllowedToUseWebservice(webServiceCallData);
                } catch (AccessDeniedException e) {
                    e.printStackTrace();
                    counter++;
                }
            }
        }
    }

}
