package de.zalando.zomcat.jobs;

import java.util.List;
import java.util.Map;

import de.zalando.zomcat.OperationMode;

public interface JobsStatusMBean {

    /**
     * toggles operationMode.
     *
     * @return  the new {@link OperationMode OperationMode}
     */
    String toggleOperationMode();

    /**
     * @return  the operationMode
     */
    String getOperationMode();

    /**
     * @param  the  new {@link OperationMode OperationMode} to set
     */
    void setOperationMode(OperationMode operationMode);

    /**
     * @param  the  new {@link OperationMode OperationMode} to set
     */
    void setOperationMode(String operationMode);

    /**
     * @return  list of {@link JobTypeStatusBean JobTypeStatusBean}'s
     */
    List<JobTypeStatusBean> getJobTypeStatusBeans();

    /**
     * @return  number of different job types
     */
    int getNumberOfDifferentJobTypes();

    /**
     * @return  total number of running workers
     */
    int getTotalNumberOfRunningWorkers();

    /**
     * @return  list of {@link JobTypeStatusBean JobTypeStatusBean}'s with values as String-String-Map for jmx.
     */
    List<Map<String, String>> getListOfJobTypeStatusBeans();

    /**
     * @param   jobName  the fully qualified job class name
     * @param   running  flag if job should be set to running or stopped
     *
     * @return  flag if successful or not, if <code>null</code> then an error occured (perhaps non-existing job), then
     *          please look into logfile for details
     */
    Boolean toggleJob(String jobName, boolean running);

    /**
     * @param   jobName  the fully qualified job class name
     *
     * @return  if successful or not
     */
    boolean triggerJob(String jobName);
}
