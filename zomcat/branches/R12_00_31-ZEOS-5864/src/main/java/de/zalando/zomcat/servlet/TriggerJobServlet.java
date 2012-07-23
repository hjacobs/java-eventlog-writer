package de.zalando.zomcat.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import org.springframework.web.context.ContextLoader;

import de.zalando.zomcat.ExecutionContext;
import de.zalando.zomcat.flowid.FlowId;
import de.zalando.zomcat.jobs.JobTypeStatusBean;
import de.zalando.zomcat.jobs.JobsStatusBean;
import de.zalando.zomcat.jobs.QuartzJobInfoBean;

/**
 * servlet for triggering job instantly.
 *
 * @author  fbrick
 */
public class TriggerJobServlet extends HttpServlet {

    private static final String JOB_PARAMETER_NAME = "job";

    private static final long serialVersionUID = 622992660835204455L;

    private static final Logger LOG = Logger.getLogger(TriggerJobServlet.class);

    private JobsStatusBean jobStatusBean;

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

            final String job = request.getParameter(JOB_PARAMETER_NAME);

            if (job != null) {
                final JobTypeStatusBean jobTypeStatusBean = jobStatusBean.getJobTypeStatusBean(job);

                if (jobTypeStatusBean == null) {
                    LOG.info("job " + job + " not found, job is not triggered");

                    response.sendRedirect(request.getContextPath() + JobsMonitorServlet.URL_PATH_JOBS_MONITOR);

                    return;
                }

                final QuartzJobInfoBean lastQuartzJobInfoBean = jobTypeStatusBean.getQuartzJobInfoBean();

                if (lastQuartzJobInfoBean == null) {
                    LOG.info("lastQuartzJobInfoBean not found for job " + job + ", job can not be triggered");

                    response.sendRedirect(request.getContextPath() + JobsMonitorServlet.URL_PATH_JOBS_MONITOR);

                    return;
                }

                final Scheduler scheduler = (Scheduler) ContextLoader.getCurrentWebApplicationContext().getBean(
                        lastQuartzJobInfoBean.getSchedulerName());

                if (scheduler == null) {
                    LOG.info("scheduler not found for job " + job + ", lastQuartzJobInfoBean = " + lastQuartzJobInfoBean
                            + ", job can not be triggered");

                    response.sendRedirect(request.getContextPath() + JobsMonitorServlet.URL_PATH_JOBS_MONITOR);

                    return;
                }

                if (LOG.isInfoEnabled()) {
                    LOG.info("starting triggering job " + job + " with lastQuartzJobInfoBean = " + lastQuartzJobInfoBean
                            + " ...");
                }

                try {
                    scheduler.triggerJob(lastQuartzJobInfoBean.getJobName(), lastQuartzJobInfoBean.getJobGroup());
                } catch (final SchedulerException e) {
                    LOG.error("failed to trigger job " + job + " with lastQuartzJobInfoBean = " + lastQuartzJobInfoBean,
                        e);

                    response.sendRedirect(request.getContextPath() + JobsMonitorServlet.URL_PATH_JOBS_MONITOR);

                    return;
                }

                if (LOG.isInfoEnabled()) {
                    LOG.info("... finished triggering job " + job + " with lastQuartzJobInfoBean = "
                            + lastQuartzJobInfoBean);
                }

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
        jobStatusBean = (JobsStatusBean) ContextLoader.getCurrentWebApplicationContext().getBean(
                JobsStatusBean.BEAN_NAME);
    }
}
