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
import de.zalando.zomcat.OperationMode;
import de.zalando.zomcat.flowid.FlowId;
import de.zalando.zomcat.jobs.JobsStatusBean;

/**
 * servlet for monitoring the jobs.
 *
 * @author  fbrick
 */
public class ToggleOperationModeServlet extends HttpServlet {

    public static final String MODE_PARAMETER_NAME = "mode";
    public static final String REDIRECT_PARAMETER_NAME = "redirect";

    private static final long serialVersionUID = 622992660835204455L;

    private static final Logger LOG = Logger.getLogger(ToggleOperationModeServlet.class);

    private JobsStatusBean jobsStatusBean;

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
                final String operationMode = jobsStatusBean.toggleOperationMode();

                LOG.info("toggled operation mode to: " + operationMode);

                redirect = true;
            } else {
                try {
                    final OperationMode operationMode = OperationMode.valueOf(mode);

                    jobsStatusBean.setOperationMode(operationMode);

                    if (!redirect) {
                        response.getWriter().println(operationMode);
                    }
                } catch (final IllegalArgumentException e) {
                    final String errorMessage = "unknown mode: '" + mode + "', please use one of these: "
                            + Arrays.asList(OperationMode.values());

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

        // get JobsStatusBean singleton, it will never be replaced as long as
        // tomcat lives, so we can
        // store reference to it in the servlet
        jobsStatusBean = (JobsStatusBean) ContextLoader.getCurrentWebApplicationContext().getBean(
                JobsStatusBean.BEAN_NAME);
    }
}
