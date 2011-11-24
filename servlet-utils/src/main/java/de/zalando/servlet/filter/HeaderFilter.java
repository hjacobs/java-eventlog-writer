package de.zalando.servlet.filter;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * @author  hjacobs
 */

public class HeaderFilter implements Filter {

    private FilterConfig filterConfig;

    private Map<String, String> headersMap;

    public void init(final FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;

        final String headerParam = filterConfig.getInitParameter("header");
        if (headerParam == null) {

            // "No headers were found in the web.xml (init-param) for the HeaderFilter !"
            return;
        }

        // Init the header list :
        headersMap = new HashMap<String, String>();

        final String[] headers = headerParam.split("\\|");
        for (String header : headers) {
            parseHeader(header);
        }

    }

    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {
        if (headersMap != null) {

            // Add the header to the response
            Set<Entry<String, String>> headers = headersMap.entrySet();
            for (Entry<String, String> header : headers) {
                ((HttpServletResponse) response).setHeader(header.getKey(), header.getValue());
            }
        }

        // Continue
        chain.doFilter(request, response);
    }

    public void destroy() {
        this.filterConfig = null;
        this.headersMap = null;
    }

    private void parseHeader(final String header) {
        String headerName = header.substring(0, header.indexOf(":"));
        if (!headersMap.containsKey(headerName)) {
            headersMap.put(headerName, header.substring(header.indexOf(":") + 1));
        }
    }
}
