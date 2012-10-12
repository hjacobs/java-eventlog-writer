package de.zalando.zomcat.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.sql.DataSource;

import org.apache.commons.lang.time.FastDateFormat;

import org.apache.log4j.Logger;

import org.springframework.context.ApplicationContext;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;

import com.jolbox.bonecp.BoneCPDataSource;
import com.jolbox.bonecp.Statistics;
import com.jolbox.bonecp.spring.DynamicDataSourceProxy;

import de.zalando.zomcat.spread.HeartbeatInformation;
import de.zalando.zomcat.spread.SpreadServiceInformation;

/**
 * @author  Guy Youansi
 *
 * @web.servlet
 *   name            = "StatusServlet"
 *   load-on-startup = "0"
 * @web.servlet-mapping
 *   url-pattern = "/Status"
 */
public class StatusServlet extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(StatusServlet.class);

    private static final long serialVersionUID = -3244011674674485304L;

    private static final char NEW_LINE = '\n';
    private static final String DS_STRING = "DataSource.";

    public static final String VIEW_PARAMETER_NAME = "view";
    public static final String VIEW_VALUE_EHCACHE_SIZES_AS_JSON = "ehcacheSizesAsJson";

    private static final FastDateFormat FDF = FastDateFormat.getInstance("dd.MM.yyyy HH:mm:ss:SSSS");

    private static final Gson gson = new Gson();

    public ApplicationContext getApplicationContext(final ServletConfig servletConfig) throws ServletException {
        return WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
        IOException {
        final String viewParameter = request.getParameter(VIEW_PARAMETER_NAME);

        printNormal(request, response);
    }

    /**
     * print normal status view.
     *
     * @param   request   the http request
     * @param   response  the http response
     *
     * @throws  ServletException  if any servlet exception occured
     * @throws  IOException       if any other io exception occured
     */
    private void printNormal(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException, IOException {
        final StringBuilder pageContent = new StringBuilder();
        getVmInfos(pageContent);
        getDataSourceInfos(pageContent);
        getThreadInfos(pageContent);
        getSpreadHeartbeat(pageContent);

        response.setContentType("text/plain");

        final PrintWriter writer = response.getWriter();

        writer.println("Server's Status");
        writer.println();
        writer.println(pageContent);
        writer.flush();
        writer.close();

        pageContent.delete(0, pageContent.length());
    }

    private void getVmInfos(final StringBuilder pageContent) {
        try {
            RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
            pageContent.append("startTime=").append(new Date(mx.getStartTime())).append(NEW_LINE);
            pageContent.append("uptime=").append(mx.getUptime()).append(" ms").append(NEW_LINE);
            pageContent.append(NEW_LINE);
        } catch (Exception e) {
            // ignore
        }
    }

    private void getSpreadHeartbeat(final StringBuilder pageContent) throws ServletException {
        final ApplicationContext applicationContext = getApplicationContext(getServletConfig());

        HeartbeatInformation heartbeatOperationHandler = null;

        try {
            heartbeatOperationHandler = (HeartbeatInformation) applicationContext.getBean("heartbeatOperationHandler");
        } catch (final Exception e) {
            LOG.info("heartbeatOperationHandler not found => no spread message received information");
        }

        pageContent.append("last spread message received=");

        if (heartbeatOperationHandler != null) {
            pageContent.append(heartbeatOperationHandler.getLastMessageReceived());
        } else {
            pageContent.append("null");
        }

        pageContent.append(NEW_LINE);

        pageContent.append("last spread message received as date=");

        if ((heartbeatOperationHandler != null) && (heartbeatOperationHandler.getLastMessageReceived() != null)) {
            pageContent.append(FDF.format(heartbeatOperationHandler.getLastMessageReceived()));
        } else {
            pageContent.append("null");
        }

        pageContent.append(NEW_LINE);

        SpreadServiceInformation spreadService = null;

        try {
            spreadService = (SpreadServiceInformation) applicationContext.getBean("spreadService");
        } catch (final Exception e) {
            LOG.info("spreadService not found => no spread message received information");
        }

        pageContent.append("last spread message send=");

        if (spreadService != null) {
            pageContent.append(spreadService.getLastSuccessfulMessageTime());
        } else {
            pageContent.append("null");
        }

        pageContent.append(NEW_LINE);

        pageContent.append("last spread message send as date=");

        if ((spreadService != null) && (spreadService.getLastSuccessfulMessageTime() != null)) {
            pageContent.append(FDF.format(spreadService.getLastSuccessfulMessageTime()));
        } else {
            pageContent.append("null");
        }

        pageContent.append(NEW_LINE);

        pageContent.append("last failed spread message=");

        if (spreadService != null) {
            pageContent.append(spreadService.getLastFailureMessageTime());
        } else {
            pageContent.append("null");
        }

        pageContent.append(NEW_LINE);

        pageContent.append("last failed spread message as date=");

        if ((spreadService != null) && (spreadService.getLastFailureMessageTime() != null)) {
            pageContent.append(FDF.format(spreadService.getLastFailureMessageTime()));
        } else {
            pageContent.append("null");
        }

        pageContent.append(NEW_LINE);

        pageContent.append("total number of send spread message failures=");

        if (spreadService != null) {
            pageContent.append(spreadService.getNumberOfSendFailures());
        } else {
            pageContent.append("null");
        }

        pageContent.append(NEW_LINE);

    }

    private static MBeanServer getMBeanServer() {
        final ArrayList<MBeanServer> servers = MBeanServerFactory.findMBeanServer(null);
        if ((servers == null) || servers.isEmpty()) {
            LOG.debug("No servers available");
            return null;
        }

        // return first available server
        return servers.get(0);
    }

    /**
     * Gets some informations concerning thread's workload on the server.
     *
     * @param  pageContent  informations container.
     */
    private void getThreadInfos(final StringBuilder pageContent) {

        final Map<String, Object> statusValueMap = new TreeMap<String, Object>();

        try {
            final MBeanServer server = getMBeanServer();

            final Set<ObjectName> connectorSet = server.queryNames(new ObjectName("*:type=Connector,*"), null);
            for (final ObjectName connectorName : connectorSet) {
                final String[] connectorAttributes = {"minSpareThreads", "maxSpareThreads",};

                final String name = connectorName.getKeyProperty("port");
                for (final String attribute : connectorAttributes) {
                    statusValueMap.put("ConnectorPort[" + name + "]" + attribute.substring(0, 1).toUpperCase()
                            + attribute.substring(1), server.getAttribute(connectorName, attribute));

                }
            }

            final Set<ObjectName> threadPoolSet = server.queryNames(new ObjectName("*:type=ThreadPool,*"), null);
            for (final ObjectName threadPoolName : threadPoolSet) {
                final String[] threadPoolAttributes = {
                    "currentThreadCount", "currentThreadsBusy", "maxThreads", "threadPriority"
                };

                final String name = threadPoolName.getKeyProperty("name");
                for (final String attribute : threadPoolAttributes) {
                    statusValueMap.put("threadPool[" + name + "]" + attribute.substring(0, 1).toUpperCase()
                            + attribute.substring(1), server.getAttribute(threadPoolName, attribute));

                }
            }

            final Set<ObjectName> managerSet = server.queryNames(new ObjectName("*:type=Manager,*"), null);
            for (final ObjectName managerName : managerSet) {
                final String[] managerAttributes = {
                    "maxActiveSessions", "sessionCounter", "activeSessions", "expiredSessions", "rejectedSessions",
                    "duplicates", "sessionAverageAliveTime", "sessionMaxAliveTime"
                };

                for (final String attribute : managerAttributes) {
                    statusValueMap.put("Manager " + attribute.substring(0, 1).toUpperCase() + attribute.substring(1),
                        server.getAttribute(managerName, attribute));

                }
            }

            for (final Entry<String, Object> entry : statusValueMap.entrySet()) {
                pageContent.append(NEW_LINE).append(entry.getKey()).append('=').append(entry.getValue());
            }

            pageContent.append(NEW_LINE);
            pageContent.append(NEW_LINE);
        } catch (final NullPointerException e) {
            LOG.error(e, e);
        } catch (final MalformedObjectNameException e) {
            LOG.error(e, e);
        } catch (final AttributeNotFoundException e) {
            LOG.error(e, e);
        } catch (final InstanceNotFoundException e) {
            LOG.error(e, e);
        } catch (final MBeanException e) {
            LOG.error(e, e);
        } catch (final ReflectionException e) {
            LOG.error(e, e);
        }

    }

    /**
     * Gets some informations concerning all using data sources on the server.
     *
     * @param   pageContent  informations container.
     *
     * @throws  ServletException
     */
    private void getDataSourceInfos(final StringBuilder pageContent) throws ServletException {
        final ApplicationContext applicationContext = getApplicationContext(getServletConfig());

        if (applicationContext == null) {
            return;
        }

        // get all the dataSource objects from the context
        final String[] dataSourceBeanNames = applicationContext.getBeanNamesForType(DataSource.class);

        final Map<String, DynamicDataSourceProxy> availableDynamicDataSourceProxies =
            new TreeMap<String, DynamicDataSourceProxy>();

        DataSource ds;

        for (final String dsName : dataSourceBeanNames) {
            ds = (DataSource) applicationContext.getBean(dsName);
            if (ds instanceof DynamicDataSourceProxy) {
                availableDynamicDataSourceProxies.put(dsName, (DynamicDataSourceProxy) ds);
            }
        }

        int datasourceCounter = 0;

        DynamicDataSourceProxy ddsp;
        for (final Entry<String, DynamicDataSourceProxy> entry : availableDynamicDataSourceProxies.entrySet()) {
            ddsp = entry.getValue();

            pageContent.append(DS_STRING).append(datasourceCounter).append(".BoundDataSourceBeanName=").append(
                entry.getKey());
            pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter).append(".BoundDataSourceClass=")
                       .append(ddsp.getClass().getName());
            if (ddsp.getTargetDataSource() instanceof BoneCPDataSource) {
                BoneCPDataSource bcp = (BoneCPDataSource) ddsp.getTargetDataSource();

                try {
                    pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter)
                               .append(".NumActiveConnections=").append(bcp.getTotalFree() + bcp.getTotalLeased());
                    pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter)
                               .append(".NumIdleConnection=").append(bcp.getTotalFree());
                    pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter)
                               .append(".NumLeasedConnection=").append(bcp.getTotalLeased());
                    pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter)
                               .append(".MinIdleConnections=").append(bcp.getMinConnectionsPerPartition()
                                       * bcp.getPartitionCount());
                    pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter)
                               .append(".MaxIdleConnections=").append(bcp.getMaxConnectionsPerPartition()
                                       * bcp.getPartitionCount());
                    pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter)
                               .append(".MaxActiveConnections=").append(bcp.getMaxConnectionsPerPartition()
                                       * bcp.getPartitionCount());
                    pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter).append(".ConnectionURL=")
                               .append(bcp.getJdbcUrl());

                    if (bcp.isStatisticsEnabled()) {
                        Statistics stats = bcp.getPoolStatistics();
                        if (stats != null) {
                            pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter)
                                       .append(".NumberOfConnectionsRequested=").append(
                                           stats.getConnectionsRequested());
                            pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter)
                                       .append(".MaxConnectionWaitTime=").append(stats.getMaximumConnectionWaitTime());
                            pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter)
                                       .append(".CumulativeConnectionWaitTime=").append(
                                           stats.getCumulativeConnectionWaitTime());
                            pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter)
                                       .append(".ConnectionWaitTimeAvg=").append(stats.getConnectionWaitTimeAvg());
                            pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter)
                                       .append(".StatementCumulativeExecutionTime=").append(
                                           stats.getCumulativeStatementExecutionTime());
                            pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter)
                                       .append(".StatementsExecuted=").append(stats.getStatementsExecuted());
                            pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter)
                                       .append(".StatementExecutionTimeAvg=").append(
                                           stats.getStatementExecuteTimeAvg());
                            pageContent.append(NEW_LINE).append(DS_STRING).append(datasourceCounter)
                                       .append(".TotalCreatedConnectsion=").append(stats.getTotalCreatedConnections());
                        }
                    }

                } catch (NullPointerException npe) {
                    LOG.error("NullPointerException for BoneCP datasource", npe);
                }
            }

            pageContent.append(NEW_LINE);
            pageContent.append(NEW_LINE);

            datasourceCounter++;
        }

    }

}
