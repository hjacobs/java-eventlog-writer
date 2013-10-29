package de.zalando.zomcat.cxf;

import javax.servlet.http.HttpServletRequest;

import javax.xml.namespace.QName;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An inbound interceptor used to log web service calls.
 *
 * @author  rreis
 */
public class MetricsCollectorInInterceptor extends AbstractPhaseInterceptor<Message> {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsCollectorInInterceptor.class);

    /**
     * Constructs a new instance of.
     */
    public MetricsCollectorInInterceptor() {
        super(Phase.USER_LOGICAL);
    }

    @Override
    public void handleMessage(final Message message) throws Fault {

        // The WS operation is extracted by delving into the received XML
        final HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        if (request == null) {
            LOG.error("httpServletRequest must not be null.");
            return;
        }

        String logMessage = getFlowId(request) + " " + request.getRemoteAddr() + " " + request.getLocalAddr() + " "
                + request.getHeader(HttpHeaders.HOST_INSTANCE) + " "
                + ((QName) message.get(Message.WSDL_SERVICE)).getLocalPart() + " "
                + ((QName) message.get(Message.WSDL_OPERATION)).getLocalPart();

        // Log the metrics
        LOG.info("{} {} null", logMessage, request.getContentLength());

        // Store the metrics and request time in milliseconds for OutInterceptor processing
        message.getExchange().put("de.zalando.wsmetrics", logMessage);
        message.getExchange().put("de.zalando.requestmillis", System.currentTimeMillis());
    }

    protected String getFlowId(final HttpServletRequest request) {

        String flowId = request.getHeader(HttpHeaders.FLOW_ID);
        return flowId != null ? flowId : request.getHeader(HttpHeaders.FLOW_ID.toUpperCase());
    }
}
