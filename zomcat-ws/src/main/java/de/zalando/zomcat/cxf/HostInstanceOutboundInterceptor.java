package de.zalando.zomcat.cxf;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.zomcat.configuration.AppInstanceContextProvider;

/**
 * This interceptor inserts the host and instance of an application into the header section of a <code>Message</code>.
 *
 * @author  Rodrigo Reis [rodrigo.reis (at) zalando.de]
 */
public class HostInstanceOutboundInterceptor extends AbstractPhaseInterceptor<Message> {
    private static final Logger LOG = LoggerFactory.getLogger(HostInstanceOutboundInterceptor.class);

    /**
     * The HTTP Header for host and instance.
     */
    public static final String X_HOST_INSTANCE = "x-host-instance";

    /**
     * The context provider of the current Application instance.
     */
    public static AppInstanceContextProvider provider = AppInstanceContextProvider.fromManifestOnFilesystem();

    /**
     * Constructs a new instance of this interceptor.
     */
    public HostInstanceOutboundInterceptor() {
        super(Phase.PRE_STREAM);
    }

    @Override
    public void handleMessage(final Message message) throws Fault {
        LOG.debug("Entered host instance interceptor...");

        // Host and instance code provided by AppInstanceContextProvider
        String hostInstance = provider.getHost() + ":" + provider.getInstanceCode();

        // Get message headers
        @SuppressWarnings("unchecked")
        Map<String, List<String>> map = (Map<String, List<String>>) message.get(Message.PROTOCOL_HEADERS);

        // If no header structure, we'll create a new one.
        if (map == null) {
            map = new TreeMap<String, List<String>>();
            message.put(Message.PROTOCOL_HEADERS, map);
        }

        // Insert host and instance into the message headers
        map.put(X_HOST_INSTANCE, Arrays.asList(hostInstance));

        if (isRequestor(message)) {
            // no response header available. do nothing, do not remove the
            // context
        } else {

            // this is the response and the end of the call.
            // add the host and instance code to the response and remove it from our
            // context.
            final HttpServletResponse httpServletResponse = (HttpServletResponse) message.get(
                    AbstractHTTPDestination.HTTP_RESPONSE);

            httpServletResponse.setHeader(X_HOST_INSTANCE, hostInstance);

        }
    }
}
