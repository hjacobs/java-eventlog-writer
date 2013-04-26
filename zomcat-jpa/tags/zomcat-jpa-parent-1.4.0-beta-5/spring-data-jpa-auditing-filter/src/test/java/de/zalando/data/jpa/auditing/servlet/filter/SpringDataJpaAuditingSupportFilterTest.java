package de.zalando.data.jpa.auditing.servlet.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.junit.Assert;
import org.junit.Test;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import de.zalando.data.jpa.auditing.AuditorContextHolder;

/**
 * @author  jbellmann
 */
public class SpringDataJpaAuditingSupportFilterTest {

    private static final String AUDITOR = "klaus.tester@zalando.de";

    @Test
    public void testFilter() throws IOException, ServletException {
        SpringDataJpaAuditingSupportFilter filter = new SpringDataJpaAuditingSupportFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-USERNAME", AUDITOR);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain mockFilterChain = new MockFilterChain(new AssertionServlet(AUDITOR));
        filter.doFilter(request, response, mockFilterChain);

        String auditorAfterFilter = AuditorContextHolder.getContext().getAuditor();
        Assert.assertNull(auditorAfterFilter);
    }

    @Test
    public void testFilterSetDefault() throws IOException, ServletException {
        SpringDataJpaAuditingSupportFilter filter = new SpringDataJpaAuditingSupportFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        // NO HEADER SET
        // request.addHeader("X-USERNAME", AUDITOR);

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain mockFilterChain = new MockFilterChain(new AssertionServlet("auditor@zalando.de"));
        filter.doFilter(request, response, mockFilterChain);

        String auditorAfterFilter = AuditorContextHolder.getContext().getAuditor();
        Assert.assertNull(auditorAfterFilter);
    }

    @SuppressWarnings("serial")
    static class AssertionServlet extends HttpServlet {

        private final String templateAuditor;

        public AssertionServlet(final String auditor) {
            this.templateAuditor = auditor;
        }

        @Override
        public void service(final ServletRequest req, final ServletResponse res) throws ServletException, IOException {
            String auditor = AuditorContextHolder.getContext().getAuditor();
            Assert.assertEquals(templateAuditor, auditor);
        }

    }

}
