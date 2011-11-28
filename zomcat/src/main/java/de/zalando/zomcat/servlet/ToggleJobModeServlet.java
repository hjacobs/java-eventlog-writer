package de.zalando.zomcat.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.ContextLoader;

import de.zalando.zomcat.flowid.FlowId;
import de.zalando.zomcat.jobs.JobsStatusBean;

/**
 * servlet for toggling heartbeatMode.
 *
 * @author  fbrick
 */
public class ToggleJobModeServlet extends HttpServlet {

    public static final String JOB_PARAMETER_NAME = "job";
    public static final String RUNNING_PARAMETER_NAME = "running";

    private static final long serialVersionUID = 622992660835204455L;

    private JobsStatusBean jobStatusBean;

    /**
     * @see  HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException {
        try {
            FlowId.clear();
            FlowId.generateAndPushFlowId();

            final String job = request.getParameter(JOB_PARAMETER_NAME);

            Boolean running = null;

            final String runningAsStr = request.getParameter(RUNNING_PARAMETER_NAME);

            if (runningAsStr != null) {
                running = Boolean.valueOf(runningAsStr);
            }

            if (job != null) {
                if (running == null) {
                    jobStatusBean.toggleJob(job);
                } else {
                    jobStatusBean.toggleJob(job, running);
                }

                response.sendRedirect(request.getContextPath() + JobsMonitorServlet.URL_PATH_JOBS_MONITOR);
            }
        } finally {
            FlowId.clear();
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
        jobStatusBean = (JobsStatusBean) ContextLoader.getCurrentWebApplicationContext().getBean(
                JobsStatusBean.BEAN_NAME);
    }
}
