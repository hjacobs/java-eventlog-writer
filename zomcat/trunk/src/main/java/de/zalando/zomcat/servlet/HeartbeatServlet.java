package de.zalando.zomcat.servlet;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ContextLoader;

import de.zalando.zomcat.monitoring.HeartbeatStatusBean;

/**
 * Heartbeat servlet used to check if the service is alive.
 *
 * @author  pribeiro
 */
public class HeartbeatServlet extends HttpServlet {

    // Caching the HeartbeatStatusBean singleton reference. It will never be replaced as long as the container lives,
    // so we can store reference to it in the servlet
    private HeartbeatStatusBean heartbeatStatusBean;

    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);

        heartbeatStatusBean = (HeartbeatStatusBean) ContextLoader.getCurrentWebApplicationContext().getBean(
                HeartbeatStatusBean.BEAN_NAME);
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException {
        response.getWriter().println(heartbeatStatusBean.getLoadbalancerMessage());
    }

}
