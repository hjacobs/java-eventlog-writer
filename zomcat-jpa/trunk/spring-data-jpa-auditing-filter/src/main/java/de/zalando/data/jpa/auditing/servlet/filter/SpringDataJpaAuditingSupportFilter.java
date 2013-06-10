package de.zalando.data.jpa.auditing.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.data.jpa.auditing.AuditorContext;
import de.zalando.data.jpa.auditing.AuditorContextHolder;

/**
 * Filter that extracts the current username from request-header and puts it into an {@link AuditorContext}. So it will
 * be available for other components.
 *
 * @author  jbellmann
 */
public class SpringDataJpaAuditingSupportFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(SpringDataJpaAuditingSupportFilter.class);

    protected static String DEFAULT_HEADER_NAME = "X-USERNAME";
    protected static String DEFAULT_AUDITOR = "auditor@zalando.de";

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        // there is nothing to do
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {

        final HttpServletRequest servletRequest = (HttpServletRequest) request;

        String auditorHeaderValue = servletRequest.getHeader(getDefaultHeaderName());

        // TODO, if this happen, should we raise an exception,should we make it
        // configurable ?
        if (auditorHeaderValue == null) {
            auditorHeaderValue = getDefaultAuditor();

            // siehe monitoringfilter
            LOG.warn("No Value for Header-Key '{}' found! Set default auditor to AuditorContextHolder: '{}'",
                DEFAULT_HEADER_NAME, DEFAULT_AUDITOR);
        } else {
            auditorHeaderValue = performHeaderValue(auditorHeaderValue);
        }

        AuditorContextHolder.getContext().setAuditor(auditorHeaderValue);

        try {

            // execute as normal
            chain.doFilter(request, response);
        } finally {

            // clear context for current Thread, also in case of an Exception
            AuditorContextHolder.clearContext();
        }

    }

    /**
     * Implement this, if you have to modify the plain value from the header.
     *
     * @param   auditorHeaderValue
     *
     * @return
     */
    protected String performHeaderValue(final String auditorHeaderValue) {
        return auditorHeaderValue;
    }

    @Override
    public void destroy() {
        // there is nothing to do
    }

    /**
     * Override this.
     *
     * @return
     */
    protected String getDefaultAuditor() {
        return DEFAULT_AUDITOR;
    }

    /**
     * Override this.
     *
     * @return
     */
    protected String getDefaultHeaderName() {
        return DEFAULT_HEADER_NAME;
    }
}
