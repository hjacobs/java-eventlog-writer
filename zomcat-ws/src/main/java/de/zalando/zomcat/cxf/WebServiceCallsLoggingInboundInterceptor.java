package de.zalando.zomcat.cxf;

import javax.servlet.http.HttpServletRequest;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An inbound interceptor used to log web service calls.
 *
 * @author  rreis
 */
public class WebServiceCallsLoggingInboundInterceptor extends AbstractSoapInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(WebServiceCallsLoggingInboundInterceptor.class);

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
    public WebServiceCallsLoggingInboundInterceptor() {

        super(Phase.PRE_INVOKE);
    }

    @Override
    public void handleMessage(final SoapMessage message) throws Fault {
        LOG.debug("WSCallInterceptor called...");

        final HttpServletRequest request = (HttpServletRequest) message.get(AbstractHTTPDestination.HTTP_REQUEST);
        if (request == null) {
            LOG.error("httpServletRequest must not be null.");
        } else {
            QName service = (QName) message.get("javax.xml.ws.wsdl.service");
            QName operation = (QName) message.get("javax.xml.ws.wsdl.operation");

            LOG.debug("{} {} {} {} {} {} {}",
                new Object[] {
                    getFlowId(request), request.getRemoteAddr(), request.getLocalAddr(),
                    request.getHeader(xHeaders.X_HOST_INSTANCE.toString()),
                    service != null ? service.getLocalPart() : null,
                    operation != null ? operation.getLocalPart() : null, request.getContentLength()
                });
        }
    }

    private String getFlowId(final HttpServletRequest request) {
        String flowId = request.getHeader(xHeaders.X_FLOW_ID.toString());

        return flowId != null ? flowId : request.getHeader(xHeaders.X_FLOW_ID.toString().toUpperCase());
    }
}
