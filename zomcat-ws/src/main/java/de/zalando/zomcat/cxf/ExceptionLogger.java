package de.zalando.zomcat.cxf;

import org.apache.cxf.logging.FaultListener;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

public class ExceptionLogger implements FaultListener {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionLogger.class);

    private static final Pattern NAMESPACE_PATTERN = Pattern.compile("\\{[^}]*\\}");

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean faultOccurred(final Exception exception, final String description, final Message message) {
        if (exception == null) {
            throw new IllegalStateException("Exception cannot be null");
        }

        final Loggable loggable;
        if (exception instanceof Loggable) {
            loggable = (Loggable) exception;
        } else if (exception.getCause() instanceof Loggable) {
            loggable = (Loggable) exception.getCause();
        } else {
            loggable = null;
        }

        if (loggable != null && !loggable.isLoggingEnabled()) {
            return false;
        }

        final Throwable loggableException = (Exception) loggable;

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

        final Logger log = getLogger(exception);
        final Throwable throwableToLog = loggableException != null ? loggableException : exception;
        log.error("Exception in {} processing {} bytes from {}: {}"
                , new Object[]{service, length, from, throwableToLog.getMessage(),
                throwableToLog
        });

        return false;
    }

    private Logger getLogger(Exception exception) {
        // find a nice log name
        Logger log = LOG;
        for (final StackTraceElement stackTraceElement : exception.getStackTrace()) {
            // get the first matching de.zalando class:
            if (stackTraceElement.getClass().getCanonicalName().startsWith("de.zalando")) {
                log = LoggerFactory.getLogger(stackTraceElement.getClass());
            }
            break;
        }
        return log;
    }

}
