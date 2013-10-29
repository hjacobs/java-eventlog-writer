package de.zalando.zomcat.cxf;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.zomcat.io.StatsCollectorOutputStream;
import de.zalando.zomcat.io.StatsCollectorOutputStreamCallback;

public class MetricsCollectorOutInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsCollectorOutInterceptor.class);

    public MetricsCollectorOutInterceptor() {
        super(Phase.PRE_STREAM);
    }

    @Override
    public void handleMessage(final Message message) throws Fault {

        // Get response time
        long responseTime = System.currentTimeMillis();

        // Get info from opposite Interceptor
        String logMessage = (String) message.getExchange().get("de.zalando.wsmetrics");
        Long requestTime = (Long) message.getExchange().get("de.zalando.requestmillis");

        // Calculate execution time, if applicable - i.e., we're on the Out Interceptor.
        Long executionTime = null;
        if (requestTime != null) {
            executionTime = Long.valueOf(responseTime - requestTime);
        }

        // Wrap the message's output stream inside other with the ability to count the response size in bytes.
        StatsCollectorOutputStream statsOs = new StatsCollectorOutputStream(message.getContent(OutputStream.class));
        message.setContent(OutputStream.class, statsOs);

        // Register our callback, which will log the stats when response size is available.
        statsOs.registerCallback(new LoggingOutCallBack(logMessage + " {} " + executionTime));

        final HttpServletResponse response = (HttpServletResponse) message.get(AbstractHTTPDestination.HTTP_RESPONSE);
        if (response == null) {
            LOG.error("httpServletResponse must not be null.");
            return;
        }
    }

    class LoggingOutCallBack implements StatsCollectorOutputStreamCallback {

        private String logMessage;

        public LoggingOutCallBack(final String logMessage) {
            this.logMessage = logMessage;
        }

        @Override
        public void onClose(final StatsCollectorOutputStream os) {
            LOG.info(logMessage, os.getBytesWritten());
        }
    }

}
