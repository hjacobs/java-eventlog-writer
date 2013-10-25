package de.zalando.zomcat.cxf;

import javax.servlet.http.HttpServletRequest;

import javax.xml.namespace.QName;

import org.apache.cxf.interceptor.AbstractInDatabindingInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.model.BindingOperationInfo;
import org.apache.cxf.service.model.ServiceModelUtil;
import org.apache.cxf.staxutils.DepthXMLStreamReader;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An inbound interceptor used to log web service calls.
 *
 * @author  rreis
 */
public class WebServiceMetricsInInterceptor extends AbstractInDatabindingInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(WebServiceMetricsInInterceptor.class);

    enum xHeaders {
        X_FLOW_ID,
        X_HOST_INSTANCE;

        public String toString() {
            return name().toLowerCase().replace('_', '-');
        }
    }

    /**
     * Constructs a new instance of.
     */
    public WebServiceMetricsInInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

    @Override
    public void handleMessage(final Message message) throws Fault {

        // The WS operation is extracted by delving into the received XML
        DepthXMLStreamReader xmlReader = getXMLStreamReader(message);
        Exchange exchange = message.getExchange();
        BindingOperationInfo bop = getBindingOperationInfo(xmlReader, exchange, isRequestor(message));
        String operation = bop != null ? bop.getName().getLocalPart() : null;

        final HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        if (request == null) {
            LOG.error("httpServletRequest must not be null.");
            return;
        }

        String logMessage = getFlowId(request) + " " + request.getRemoteAddr() + " " + request.getLocalAddr() + " "
                + request.getHeader("x-host-instance") + " "
                + ((QName) message.getExchange().get(Message.WSDL_SERVICE)).getLocalPart() + " " + operation;

        // Log the metrics
        LOG.info("{} {} null", logMessage, request.getContentLength());

        // Store the metrics and request time in milliseconds for OutInterceptor processing
        message.getExchange().put("de.zalando.wsmetrics", logMessage);
        message.getExchange().put("de.zalando.requestmillis", System.currentTimeMillis());
    }

    protected BindingOperationInfo getBindingOperationInfo(final DepthXMLStreamReader xmlReader,
            final Exchange exchange, final boolean client) {
        QName name = xmlReader == null ? new QName("http://cxf.apache.org/jaxws/provider", "invoke")
                                       : xmlReader.getName();

        BindingOperationInfo bop = ServiceModelUtil.getOperationForWrapperElement(exchange, name, client);
        if (bop == null) {
            bop = super.getBindingOperationInfo(exchange, name, client);
        }

        return bop;
    }

    protected String getFlowId(final HttpServletRequest request) {

        String flowId = request.getHeader(xHeaders.X_FLOW_ID.toString());
        return flowId != null ? flowId : request.getHeader(xHeaders.X_FLOW_ID.toString().toUpperCase());
    }
}
