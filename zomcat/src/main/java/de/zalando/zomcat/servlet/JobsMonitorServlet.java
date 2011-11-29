package de.zalando.zomcat.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import org.springframework.web.context.ContextLoader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.zalando.zomcat.HeartbeatMode;
import de.zalando.zomcat.OperationMode;
import de.zalando.zomcat.jobs.FinishedWorkerBean;
import de.zalando.zomcat.jobs.GsonFinishedWorkerBeanAdapter;
import de.zalando.zomcat.jobs.GsonJobTypeStatusBeanAdapter;
import de.zalando.zomcat.jobs.GsonJobsStatusBeanAdapter;
import de.zalando.zomcat.jobs.GsonRunningWorkerBeanAdapter;
import de.zalando.zomcat.jobs.JobTypeStatusBean;
import de.zalando.zomcat.jobs.JobsStatusBean;
import de.zalando.zomcat.jobs.RunningWorker;
import de.zalando.zomcat.jobs.RunningWorkerBean;
import de.zalando.zomcat.monitoring.HeartbeatStatusBean;

/**
 * servlet for monitoring the jobs.
 *
 * @author  fbrick
 */
public class JobsMonitorServlet extends HttpServlet {

    public static final String URL_PATH_JOBS_MONITOR = "/jobs.monitor";

    private static final long serialVersionUID = 622992660835204455L;

    private JobsStatusBean jobsStatusBean;

    private HeartbeatStatusBean heartbeatStatusBean;

    private Gson gson;

    public static final String VIEW_PARAMETER_NAME = "view";
    public static final String VIEW_VALUE_JSON = "json";
    private static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss:SSS");

    /**
     * @see  javax.servlet.GenericServlet#init()
     */
    @Override
    public void init() throws ServletException {

        // get JobsStatusBean singleton, it will never be replaced as long as tomcat lives, so we can
        // store reference to it in the servlet
        jobsStatusBean = (JobsStatusBean) ContextLoader.getCurrentWebApplicationContext().getBean(
                JobsStatusBean.BEAN_NAME);

        // get HeartbeatStatusBean singleton, it will never be replaced as long as tomcat lives, so we can
        // store reference to it in the servlet
        heartbeatStatusBean = (HeartbeatStatusBean) ContextLoader.getCurrentWebApplicationContext().getBean(
                HeartbeatStatusBean.BEAN_NAME);

        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
                                .registerTypeAdapter(JobsStatusBean.class, new GsonJobsStatusBeanAdapter())
                                .registerTypeAdapter(JobTypeStatusBean.class, new GsonJobTypeStatusBeanAdapter())
                                .registerTypeAdapter(RunningWorkerBean.class, new GsonRunningWorkerBeanAdapter())
                                .registerTypeAdapter(FinishedWorkerBean.class, new GsonFinishedWorkerBeanAdapter())
                                .create();
    }

    /**
     * @see  HttpServlet#doGet(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException {
        final String viewParameter = request.getParameter(VIEW_PARAMETER_NAME);

        if ((viewParameter != null) && VIEW_VALUE_JSON.equals(viewParameter)) {
            printJson(response);
        } else {
            printNormal(request, response);
        }
    }

    private void printJson(final HttpServletResponse response) throws IOException {
        final PrintWriter writer = response.getWriter();

        writer.print(gson.toJson(jobsStatusBean));
    }

    private void printOperationMode(final PrintWriter writer, final HttpServletRequest request,
            final OperationMode actualOperationMode, final OperationMode printedOperationMode) {
        if (actualOperationMode.equals(printedOperationMode)) {
            writer.print("<strong class=\"" + actualOperationMode.toString().toLowerCase() + "\">");
            writer.print(printedOperationMode);
            writer.print("</strong>");
        } else {
            writer.print("<a class=\"" + printedOperationMode.toString().toLowerCase() + "\" href=\""
                    + request.getContextPath() + "/toggleOperationMode?"
                    + ToggleOperationModeServlet.MODE_PARAMETER_NAME + "=" + printedOperationMode + "&"
                    + ToggleOperationModeServlet.REDIRECT_PARAMETER_NAME + "=true" + "\">" + printedOperationMode
                    + "</a>");
        }
    }

    private void printHeartbeatMode(final PrintWriter writer, final HttpServletRequest request,
            final HeartbeatMode actualHeartbeatMode, final HeartbeatMode printedHeartbeatMode) {
        if (actualHeartbeatMode.equals(printedHeartbeatMode)) {
            writer.print("<strong class=\"" + actualHeartbeatMode.toString().toLowerCase() + "\">");
            writer.print(printedHeartbeatMode);
            writer.print("</strong>");
        } else {
            writer.print("<a class=\"" + printedHeartbeatMode.toString().toLowerCase() + "\" href=\""
                    + request.getContextPath() + "/toggleHeartbeatMode?"
                    + ToggleHeartbeatModeServlet.MODE_PARAMETER_NAME + "=" + printedHeartbeatMode + "&"
                    + ToggleHeartbeatModeServlet.REDIRECT_PARAMETER_NAME + "=true" + "\">" + printedHeartbeatMode
                    + "</a>");
        }
    }

    private void printJobMode(final PrintWriter writer, final String contextPath, final boolean disabled,
            final String jobName) {
        writer.print("<td>");
        if (disabled) {
            writer.print("<a href=\"" + contextPath + "/toggleJobMode?" + ToggleJobModeServlet.JOB_PARAMETER_NAME + "="
                    + jobName + "&" + ToggleJobModeServlet.RUNNING_PARAMETER_NAME + "=true"
                    + "\" class=\"enabled\">ENABLED</a>");
            writer.print(" <strong class=\"disabled\">DISABLED</strong>");
        } else {
            writer.print("<strong class=\"enabled\">ENABLED</strong> ");
            writer.print("<a href=\"" + contextPath + "/toggleJobMode?" + ToggleJobModeServlet.JOB_PARAMETER_NAME + "="
                    + jobName + "&" + ToggleJobModeServlet.RUNNING_PARAMETER_NAME + "=false"
                    + "\" class=\"disabled\">DISABLED</a>");
        }

        writer.print("</td>");
    }

    private void printTriggerJob(final PrintWriter writer, final String contextPath,
            final JobTypeStatusBean jobTypeStatusBean, final OperationMode actualOperationMode) {

        // ignore the cases where there is not enough information about the last quartz job
        // which was excecuted or perhaps the last job had job data map filled, which is not
        // supported yet
        if (jobTypeStatusBean.getLastQuartzJobInfoBean() == null) {
            writer.print("<td>not implemented yet</td>");
        } else if (jobTypeStatusBean.isDisabled() || OperationMode.MAINTENANCE.equals(actualOperationMode)) {
            writer.print("<td>disabled</td>");
        } else {
            writer.print("<td>");
            writer.print("<a href=\"" + contextPath + "/triggerJob?job=" + jobTypeStatusBean.getJobClass().getName()
                    + "\">trigger now</a>");
            writer.print("</td>");
        }
    }

    private void printNormal(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final PrintWriter writer = response.getWriter();

        writer.println("<html>\n<head>\n<title>Jobs - Monitor</title>\n" + "<style>\n"
                + "html { font: 13px Arial, Helvetica, sans-serif; }\n" + "table { border-collapse: collapse; }\n"
                + "td, th { font-size: 13px; } td { border: 1px solid #ddd; margin:0; padding: 1px 4px; } td.warn { background: #ffa500; } td.error { background: #ff9999; } td.ok { background: #99ff99; }\n"
                + "tr:hover { background-color: #eee; }\n"
                + "strong.normal, strong.ok, strong.enabled { background-color: #99ff99; padding: 2px 8px; }\n"
                + "strong.maintenance, strong.deploy, strong.disabled { background-color: #ff9999; padding: 2px 8px; }\n"
                + "a.normal, a.ok, a.enabled { color: #000; background-color: #eee; padding: 2px 8px; }\n"
                + "a.maintenance, a.deploy, a.disabled { color: #000; background-color: #eee; padding: 2px 8px; }\n"
                + "a.normal:hover, a.ok:hover, a.enabled:hover, a.maintenance:hover, a.deploy:hover, a.disabled:hover { background-color: #ddd; text-decoration: none; }\n"
                + ".hint { color: #888; }\n" + "</style>\n" + "<script>\n" + "function toggleHistory(name) {\n"
                + "var node = document.getElementsByName(name);\n" + "for (var i = node.length-1; i>=0; i--) {\n"
                + "if (node[i].style.display && node[i].style.display == 'none') {\n" + "node[i].style.display = '';\n"
                + "} else {\n" + "node[i].style.display = 'none';\n" + "}\n" + "}\n" + "}\n" + "</script>\n"
                + "</head>\n<body>");

        writer.print("OperationMode: ");

        printOperationMode(writer, request, jobsStatusBean.getOperationModeAsEnum(), OperationMode.NORMAL);

        writer.println(" / ");

        printOperationMode(writer, request, jobsStatusBean.getOperationModeAsEnum(), OperationMode.MAINTENANCE);

        writer.print("\n<p/>\n<p class=\"hint\">In <i>");
        writer.print(OperationMode.NORMAL);
        writer.print("</i> mode the jobs are running normally. In <i>");
        writer.print(OperationMode.MAINTENANCE);
        writer.print(
            "</i> the jobs are running OUT and will NOT START AGAIN! After restart/redeploy of tomcat they are back to <i>");
        writer.print(OperationMode.NORMAL);
        writer.println("</i> mode. If you do not restart/redeploy tomcat, then DO NOT FORGET TO SWITCH BACK!</p>");

        writer.print("<p/>\nHeartbeatMode: ");

        printHeartbeatMode(writer, request, heartbeatStatusBean.getHeartbeatModeAsEnum(), HeartbeatMode.OK);

        writer.println(" / ");

        printHeartbeatMode(writer, request, heartbeatStatusBean.getHeartbeatModeAsEnum(), HeartbeatMode.DEPLOY);

        writer.println("\n<p/>\n<p class=\"hint\">In <i>");
        writer.print(HeartbeatMode.OK);
        writer.print(
            "</i> mode the /heartbeat.jsp the loadbalancer needs for checks sends back <i>OK: Zalando JVM is running</i>. In <i>");
        writer.print(HeartbeatMode.DEPLOY); //
        writer.println("</i> the /heartbeat.jsp sends back <i>Deploy: Zalando JVM is in Updateprocess</i>.");
        writer.println("Then the loadbalancer removes the tomcat after latest 20 seconds!");
        writer.print("After restart/redeploy of tomcat they are back to <i>");
        writer.print(HeartbeatMode.OK);
        writer.println("</i> mode. If you do not restart/redeploy tomcat, then DO NOT FORGET TO SWITCH BACK!</p>");

        writer.println("\nJobs:");

        if (jobsStatusBean.getNumberOfDifferentJobTypes() == 0) {
            writer.println(" NONE (at the moment no jobs were already running)");
        } else {
            writer.print("<br/>\n<table>\n");
            writer.print("<tr>");
            writer.println("<th>trigger</th>");
            writer.println("<th>enabled/disabled</th>");
            writer.print("<th>class</th>");
            writer.print("<th width=\"100\">description</th>");
            writer.print("<th>running workers</th>");
            writer.print("<th>last modified</th>");
            writer.print("<th>worker id</th>");
            writer.print("<th>startTime</th>");
            writer.print("<th>internalStartTime</th>");
            writer.print("<th>progress</th>");
            writer.print("<th>flow-id</th>");
            writer.print("</tr>");

            int count = 0;

            for (final JobTypeStatusBean jobTypeStatusBean : jobsStatusBean.getJobTypeStatusBeans()) {
                final Collection<RunningWorker> runningWorkers = jobTypeStatusBean.getRunningWorkers();

                if ((runningWorkers != null) && !runningWorkers.isEmpty()) {
                    boolean first = true;

                    for (final RunningWorker worker : runningWorkers) {
                        printRow(writer, first, count, jobTypeStatusBean, worker, runningWorkers.size(),
                            request.getContextPath(), jobsStatusBean.getOperationModeAsEnum());

                        first = false;
                    }
                } else {
                    printRow(writer, true, count, jobTypeStatusBean, null, 0, request.getContextPath(),
                        jobsStatusBean.getOperationModeAsEnum());
                }

                final List<FinishedWorkerBean> history = jobTypeStatusBean.getHistory();

                if ((history != null) && !history.isEmpty()) {
                    boolean first = true;

                    for (final FinishedWorkerBean finishedWorkerBean : history) {
                        printRow(writer, first, count, finishedWorkerBean, history.size());

                        first = false;
                    }
                }

                count++;
            }

            writer.println("</table>");
        }

        printInformation(writer);

        writer.println("</body>\n</html>");
    }

    private void printRow(final PrintWriter writer, final boolean firstRow, final int count,
            final JobTypeStatusBean jobTypeStatusBean, final RunningWorker worker, final int runningWorkersSize,
            final String contextPath, final OperationMode actualOperationMode) {
        writer.print("<tr>");

        if (firstRow) {
            printTriggerJob(writer, contextPath, jobTypeStatusBean, actualOperationMode);
            printJobMode(writer, contextPath, jobTypeStatusBean.isDisabled(),
                jobTypeStatusBean.getJobClass().getName());
        } else {
            writer.print("<td></td><td></td>");
        }

        if (firstRow) {
            printCell(writer, jobTypeStatusBean.getJobClass().getName(), runningWorkersSize);
            if (StringUtils.isBlank(jobTypeStatusBean.getDescription())) {
                printCell(writer, "&nbsp;", runningWorkersSize);
            } else {
                printCell(writer, jobTypeStatusBean.getDescription(), runningWorkersSize);
            }

            printCell(writer, jobTypeStatusBean.getRunningWorker(), runningWorkersSize);
            printCell(writer,
                jobTypeStatusBean.getLastModifiedFormatted()
                    + " <input type=\"button\" onclick=\"javascript:toggleHistory('test_" + count
                    + "');\" value=\"history\"/>", runningWorkersSize);
        }

        if (worker != null) {
            printCell(writer, worker.getId(), 1);
            printCell(writer, DTF.print(worker.getStartTime().getMillis()), 1);
            printCell(writer, DTF.print(worker.getInternalStartTime().getMillis()), 1);

            writer.print("<td>(");
            writer.print(worker.getActualProcessedItemNumber());
            writer.print("/");
            writer.print(worker.getTotalNumberOfItemsToBeProcessed());
            writer.println(")</td>");
            printCell(writer, worker.getJobHistoryId(), 1);
        } else {
            writer.println("<td>&nbsp;</td>\n<td>&nbsp;</td>\n<td>&nbsp;</td>\n<td>&nbsp;</td>\n<td>&nbsp;</td>");
        }

        writer.println("</tr>");
    }

    private void printRow(final PrintWriter writer, final boolean firstRow, final int count,
            final FinishedWorkerBean finishedWorkerBean, final int historySize) {
        writer.print("<tr style=\"color:#AAAAAA;display:none;\" name=\"test_" + count + "\">");

        final String fiveLongColumns = "<td rowspan=\"" + historySize + "\">&nbsp;</td><td rowspan=\"" + historySize
                + "\">&nbsp;</td><td rowspan=\"" + historySize + "\">&nbsp;</td><td rowspan=\"" + historySize
                + "\">&nbsp;</td><td rowspan=\"" + historySize + "\">&nbsp;</td>";

        if (firstRow) {
            writer.print(fiveLongColumns);
        }

        if (finishedWorkerBean != null) {
            printCell(writer, DTF.print(finishedWorkerBean.getEndTime().getMillis()), 1);
            printCell(writer, finishedWorkerBean.getId(), 1);
            printCell(writer, DTF.print(finishedWorkerBean.getStartTime().getMillis()), 1);
            printCell(writer, DTF.print(finishedWorkerBean.getInternalStartTime().getMillis()), 1);

            printCell(writer,
                "(" + finishedWorkerBean.getActualProcessedItemNumber() + "/"
                    + finishedWorkerBean.getTotalNumberOfItemsToBeProcessed() + ")", 1);
            printCell(writer, finishedWorkerBean.getJobHistoryId(), 1);
        } else {

            // print 6 emtpy cells:
            writer.println(
                "<td>&nbsp;</td>\n<td>&nbsp;</td>\n<td>&nbsp;</td>\n<td>&nbsp;</td>\n<td>&nbsp;</td>\n<td>&nbsp;</td>\n");
        }

        writer.println("</tr>");
    }

    private void printCell(final PrintWriter writer, final Integer value, final int rowSpan) {
        String s = null;

        if (value != null) {
            s = value.toString();
        }

        printCell(writer, s, rowSpan);
    }

    private void printCell(final PrintWriter writer, final String value, final int rowSpan) {
        writer.print("<td");

        if (rowSpan > 1) {
            writer.print(" rowspan=\"");
            writer.print(rowSpan);
            writer.print("\"");
        }

        writer.print(">");

        if (value == null) {
            writer.print("&nbsp;");
        } else {
            writer.print(value);
        }

        writer.println("</td>");
    }

    private void printInformation(final PrintWriter writer) {
        writer.println("<p/>\n<h3>Legend:</h3><ul>");
        writer.println("<li id=\"info_class\">class: Fully qualified class name of Jobs</li>");
        writer.println("<li id=\"info_running_workers\">running workers: Anzahl der momentan laufenden Jobs</li>");
        writer.println("<li id=\"info_last_modified\">last modified: "
                + " Zeitpunkt der letzten &Auml;nderung. Bei noch laufenden Jobs"
                + " ist das entweder der letzte Start- oder Finish-Zeit eines Jobs. ");
        writer.println(
            "<li id=\"info_worker_id\">worker id: Die global eindeutige von Zalando vergebene workerId</li>");
        writer.println("<li id=\"info_start_time\">startTime: Der Zeitpunkt wo der Job erzeugt wurde. "
                + " Es kann sein, dass er noch l&auml;ngere Zeit auf die Ausf&uuml;hrung warten muss, "
                + " wenn die Ausf&uuml;hrung synchronisiert ist. Die genaue Start-Zeit ist die internalStartTime.</li>");
        writer.println("<li id=\"info_internal_start_time\">internalStartTime: Die interne Start-Zeit des Jobs. "
                + " Hier wird er wirklich ausgef&uuml;hrt im Gegensatz zur startTime, wo der Jobs nur erzeugt wurde.</li>");
        writer.println("<li id=\"info_progress\">progress: "
                + " Optionale Anzeige, welches Element von der maximal zu bearbeitenden Elemente"
                + " zur Zeit bearbeitet wird.</li>");
        writer.println("<li id=\"info_activate_status\">enabled/disabled: "
                + " Flag ob dieser spezielle Job aktiv l&auml;uft oder nicht."
                + " Selbst wenn der Job per Hand getriggert wird, wird er nicht aktiv ausgef&uuml;hrt.</li>");
        writer.println("<li id=\"info_trigger\">trigger: Hier kann man den Job aktiv per Hand triggern. "
                + " Handelt es sich bei dem Job um einen Job, der bereits synchronisiert l&auml;ft, "
                + " dann wird nur ein neuer Job erzeugt, der danach erneut startet. "
                + " Wenn es sich bei dem Job um einen mit einer gef&uuml;llten Job data map handelt, "
                + " dann ist das Triggern per Hand zur Zeit (noch) nicht m&ouml;glich.</li>\n</ul>");
    }

    /**
     * @see  HttpServlet#doPost(HttpServletRequest, HttpServletResponse)
     */
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException {
        doGet(request, response);
    }
}
