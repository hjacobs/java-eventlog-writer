package de.zalando.data.jpa.auditing.servlet.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import de.zalando.data.jpa.auditing.AuditorContext;
import de.zalando.data.jpa.auditing.AuditorContextHolder;

/**
 * Filter that extracts the current username from request and puts it into an {@link AuditorContext}. So it will be
 * available for other components.
 *
 * @author  jbellmann
 */
public abstract class SpringDataJpaAuditingSupportFilter implements Filter {

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {

        final HttpServletRequest servletRequest = (HttpServletRequest) request;

        String auditor = getAuditor(servletRequest);

        // set the auditor
        AuditorContextHolder.getContext().setAuditor(auditor);

        try {

            // execute as normal
            chain.doFilter(request, response);
        } finally {

            // clear context for current Thread, also in case of an Exception
            AuditorContextHolder.clearContext();
        }

    }

    /**
     * @param   servletRequest
     *
     * @return
     */
    abstract String getAuditor(final HttpServletRequest servletRequest);

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }
}
