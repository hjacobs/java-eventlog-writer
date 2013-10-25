package de.zalando.zomcat.cxf;

import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.binding.soap.interceptor.SoapOutInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServiceMetricsOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Logger LOG = LoggerFactory.getLogger(WebServiceMetricsOutInterceptor.class);

    public WebServiceMetricsOutInterceptor() {
        super(Phase.PREPARE_SEND_ENDING);
        addAfter(SoapOutInterceptor.SoapOutEndingInterceptor.class.toString());
    }

    @Override
    public void handleMessage(final Message message) throws Fault {

        // Get response time
        long responseTime = System.currentTimeMillis();

        final HttpServletResponse response = (HttpServletResponse) message.get(AbstractHTTPDestination.HTTP_RESPONSE);
        if (response == null) {
            LOG.error("httpServletResponse must not be null.");
            return;
        }

        // Get info from opposite Interceptor
        String logMessage = (String) message.getExchange().get("de.zalando.wsmetrics");
        Long requestTime = (Long) message.getExchange().get("de.zalando.requestmillis");

        String executionTime = null;
        if (requestTime != null) {
            executionTime = Long.toString(responseTime - requestTime);
        }

        // Log the metrics
        LOG.info("{} {} {}", new Object[] {logMessage, null, executionTime});
    }

}
