package de.zalando.zomcat.servlet;

import java.io.IOException;

import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.springframework.web.context.ContextLoader;

import de.zalando.zomcat.ExecutionContext;
import de.zalando.zomcat.HeartbeatMode;
import de.zalando.zomcat.flowid.FlowId;
import de.zalando.zomcat.monitoring.HeartbeatStatusBean;

/**
 * servlet for toggling heartbeatMode.
 *
 * @author  fbrick
 */
public class ToggleHeartbeatModeServlet extends HttpServlet {

    public static final String MODE_PARAMETER_NAME = "mode";
    public static final String REDIRECT_PARAMETER_NAME = "redirect";

    private static final long serialVersionUID = 622992660835204455L;

    private static final Logger LOG = Logger.getLogger(ToggleHeartbeatModeServlet.class);

    private HeartbeatStatusBean heartbeatStatusBean;

    /**
     * @see  HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException {
        try {
            ExecutionContext.clear();
            FlowId.clear();
            FlowId.generateAndPushFlowId();

            final String mode = request.getParameter(MODE_PARAMETER_NAME);

            boolean redirect = false;

            final String redirectAsStr = request.getParameter(REDIRECT_PARAMETER_NAME);

            if (redirectAsStr != null) {
                redirect = Boolean.valueOf(redirectAsStr);
            }

            if (mode == null) {
                final String heartbeatMode = heartbeatStatusBean.toggleHeartbeatMode();

                LOG.info("toggled heartbeat mode to: " + heartbeatMode);

                redirect = true;
            } else {
                try {
                    final HeartbeatMode heartbeatMode = HeartbeatMode.valueOf(mode);

                    heartbeatStatusBean.setHeartbeatMode(heartbeatMode);

                    if (!redirect) {
                        response.getWriter().println(heartbeatMode);
                    }
                } catch (final IllegalArgumentException e) {
                    final String errorMessage = "unknown mode: '" + mode + "', please use one of these: "
                            + Arrays.asList(HeartbeatMode.values());

                    LOG.info(errorMessage);

                    if (!redirect) {
                        response.getWriter().println(errorMessage);
                    }
                }
            }

            if (redirect) {
                response.sendRedirect(request.getContextPath() + JobsMonitorServlet.URL_PATH_JOBS_MONITOR);
            }
        } finally {
            FlowId.clear();
            ExecutionContext.clear();
        }
    }

    /**
     * @see  HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException {
        doGet(request, response);
    }

    /**
     * @see  javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {

        // get HeartbeatStatusBean singleton, it will never be replaced as long
        // as tomcat lives, so we can
        // store reference to it in the servlet
        heartbeatStatusBean = (HeartbeatStatusBean) ContextLoader.getCurrentWebApplicationContext().getBean(
                HeartbeatStatusBean.BEAN_NAME);
    }
}
