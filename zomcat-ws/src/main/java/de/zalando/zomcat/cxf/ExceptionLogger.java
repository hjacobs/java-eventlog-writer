package de.zalando.zomcat.cxf;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.logging.FaultListener;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionLogger implements FaultListener {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionLogger.class);

    private static final Pattern NAMESPACE_PATTERN = Pattern.compile("\\{[^}]*\\}");

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean faultOccurred(final Exception exception, final String description, final Message message) {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) message.get(
                AbstractHTTPDestination.HTTP_REQUEST);
        String length = null;
        String from = null;
        if (httpServletRequest != null) {
            from = httpServletRequest.getRemoteAddr();
            length = httpServletRequest.getHeader("Content-Length");

            final String forwardedFor = httpServletRequest.getHeader("X-Forwarded-For");
            if (forwardedFor != null) {
                from = forwardedFor + " via " + from;
            }
        }

        // strip some unnecessary chars,
        // because the CXF description is very verbose and looks like
        // '{http://../}StockWebServiceService#{http://../}bookIncomingReturnsBatch'
        String service = description.trim().replace("'", "");
        service = NAMESPACE_PATTERN.matcher(service).replaceAll("");

        // find a nice log name
        Logger log = LOG;
        for (final StackTraceElement stackTraceElement : exception.getStackTrace()) {

            // get the first matching de.zalando class:
            if (stackTraceElement.getClass().getCanonicalName().startsWith("de.zalando")) {
                log = LoggerFactory.getLogger(stackTraceElement.getClass());
            }
        }

        log.error("Exception in " + service + " processing " + length + " bytes from " + from + ": "
                + exception.getMessage(), exception.getCause() != null ? exception.getCause() : exception);

        // return false: do not log it somewhere else.
        return false;
    }

}
