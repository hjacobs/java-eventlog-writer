package de.zalando.zomcat.jobs.management.persistence.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.zalando.dbutils.sproc.SProcParameter;
import de.zalando.dbutils.sproc.SqlType;

import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobGroupConfig;
import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;

/**
 * Simple Database Entity for fetching {@link JobSchedulingConfiguration}s from Database. Implemented same as AppConfig
 * for each Application - requires SProc to be present in respective Application Database API Schema. Zalando
 * PartnerService contains sample for SProc and Database Tables required for Database configuration of Jobs
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public final class DatabaseJobSchedulingConfiguration {

    @SProcParameter(name = "result_id", type = SqlType.INTEGER)
    private Integer id;
    @SProcParameter(name = "result_job_class", type = SqlType.VARCHAR)
    private String jobClass;
    @SProcParameter(name = "result_job_cron_expression", type = SqlType.VARCHAR)
    private String jobCronExpression;
    @SProcParameter(name = "result_job_description", type = SqlType.BOOLEAN)
    private String jobDescription;
    @SProcParameter(name = "result_job_active", type = SqlType.BOOLEAN)
    private Boolean jobActive;
    @SProcParameter(name = "result_job_processing_limit", type = SqlType.INTEGER)
    private Integer jobProcessingLimit;
    @SProcParameter(name = "result_job_startup_processing_limit", type = SqlType.INTEGER)
    private Integer jobStartupProcessingLimit;
    @SProcParameter(name = "result_job_app_instance_keys", type = SqlType.ARRAY)
    private Set<String> jobAppInstanceKeys;
    @SProcParameter(name = "result_job_data", type = SqlType.ARRAY)
    private List<String> jobData;
    @SProcParameter(name = "result_job_group_name", type = SqlType.VARCHAR)
    private String jobGroupName;
    @SProcParameter(name = "result_job_group_description", type = SqlType.VARCHAR)
    private String jobGroupDescription;
    @SProcParameter(name = "result_job_group_active", type = SqlType.BOOLEAN)
    private Boolean jobGroupActive;
    @SProcParameter(name = "result_job_group_app_instance_keys", type = SqlType.ARRAY)
    private Set<String> jobGroupAppInstanceKeys;
    @SProcParameter(name = "result_created", type = SqlType.TIMESTAMP)
    private Date created;
    @SProcParameter(name = "result_last_modified", type = SqlType.TIMESTAMP)
    private Date lastModified;

    /**
     * Getter for Field: id
     *
     * @return  the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Setter for Field: id
     *
     * @param  id  the id to set
     */
    public void setId(final Integer id) {
        this.id = id;
    }

    /**
     * Getter for Field: jobClass
     *
     * @return  the jobClass
     */
    public String getJobClass() {
        return jobClass;
    }

    /**
     * Setter for Field: jobClass
     *
     * @param  jobClass  the jobClass to set
     */
    public void setJobClass(final String jobClass) {
        this.jobClass = jobClass;
    }

    /**
     * Getter for Field: jobCronExpression
     *
     * @return  the jobCronExpression
     */
    public String getJobCronExpression() {
        return jobCronExpression;
    }

    /**
     * Setter for Field: jobCronExpression
     *
     * @param  jobCronExpression  the jobCronExpression to set
     */
    public void setJobCronExpression(final String jobCronExpression) {
        this.jobCronExpression = jobCronExpression;
    }

    /**
     * Getter for Field: jobActive
     *
     * @return  the jobActive
     */
    public Boolean getJobActive() {
        return jobActive;
    }

    /**
     * Setter for Field: jobActive
     *
     * @param  jobActive  the jobActive to set
     */
    public void setJobActive(final Boolean jobActive) {
        this.jobActive = jobActive;
    }

    /**
     * Getter for Field: jobAppInstanceKeys
     *
     * @return  the jobAppInstanceKeys
     */
    public Set<String> getJobAppInstanceKeys() {
        return jobAppInstanceKeys;
    }

    /**
     * Setter for Field: jobAppInstanceKeys
     *
     * @param  jobAppInstanceKeys  the jobAppInstanceKeys to set
     */
    public void setJobAppInstanceKeys(final Set<String> jobAppInstanceKeys) {
        this.jobAppInstanceKeys = jobAppInstanceKeys;
    }

    /**
     * Getter for Field: jobGroupAppInstanceKeys
     *
     * @return  the jobGroupAppInstanceKeys
     */
    public Set<String> getJobGroupAppInstanceKeys() {
        return jobGroupAppInstanceKeys;
    }

    /**
     * Setter for Field: jobGroupAppInstanceKeys
     *
     * @param  jobGroupAppInstanceKeys  the jobGroupAppInstanceKeys to set
     */
    public void setJobGroupAppInstanceKeys(final Set<String> jobGroupAppInstanceKeys) {
        this.jobGroupAppInstanceKeys = jobGroupAppInstanceKeys;
    }

    /**
     * Getter for Field: jobData
     *
     * @return  the jobData
     */
    public List<String> getJobData() {
        return jobData;
    }

    /**
     * Setter for Field: jobData
     *
     * @param  jobData  the jobData to set
     */
    public void setJobData(final List<String> jobData) {
        this.jobData = jobData;
    }

    /**
     * Getter for Field: jobGroupName
     *
     * @return  the jobGroupName
     */
    public String getJobGroupName() {
        return jobGroupName;
    }

    /**
     * Setter for Field: jobGroupName
     *
     * @param  jobGroupName  the jobGroupName to set
     */
    public void setJobGroupName(final String jobGroupName) {
        this.jobGroupName = jobGroupName;
    }

    /**
     * Getter for Field: jobGroupActive
     *
     * @return  the jobGroupActive
     */
    public Boolean getJobGroupActive() {
        return jobGroupActive;
    }

    /**
     * Setter for Field: jobGroupActive
     *
     * @param  jobGroupActive  the jobGroupActive to set
     */
    public void setJobGroupActive(final Boolean jobGroupActive) {
        this.jobGroupActive = jobGroupActive;
    }

    /**
     * Getter for Field: created
     *
     * @return  the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * Setter for Field: created
     *
     * @param  created  the created to set
     */
    public void setCreated(final Date created) {
        this.created = created;
    }

    /**
     * Getter for Field: lastModified
     *
     * @return  the lastModified
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * Setter for Field: lastModified
     *
     * @param  lastModified  the lastModified to set
     */
    public void setLastModified(final Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Getter for Field: jobProcessingLimit
     *
     * @return  the jobProcessingLimit
     */
    public Integer getJobProcessingLimit() {
        return jobProcessingLimit;
    }

    /**
     * Setter for Field: jobProcessingLimit
     *
     * @param  jobProcessingLimit  the jobProcessingLimit to set
     */
    public void setJobProcessingLimit(final Integer jobProcessingLimit) {
        this.jobProcessingLimit = jobProcessingLimit;
    }

    /**
     * Getter for Field: jobStartupProcessingLimit
     *
     * @return  the jobStartupProcessingLimit
     */
    public Integer getJobStartupProcessingLimit() {
        return jobStartupProcessingLimit;
    }

    /**
     * Setter for Field: jobStartupProcessingLimit
     *
     * @param  jobStartupProcessingLimit  the jobStartupProcessingLimit to set
     */
    public void setJobStartupProcessingLimit(final Integer jobStartupProcessingLimit) {
        this.jobStartupProcessingLimit = jobStartupProcessingLimit;
    }

    /**
     * Getter for Field: jobDescription
     *
     * @return  the jobDescription
     */
    public String getJobDescription() {
        return jobDescription;
    }

    /**
     * Setter for Field: jobDescription
     *
     * @param  jobDescription  the jobDescription to set
     */
    public void setJobDescription(final String jobDescription) {
        this.jobDescription = jobDescription;
    }

    /**
     * Getter for Field: jobGroupDescription
     *
     * @return  the jobGroupDescription
     */
    public String getJobGroupDescription() {
        return jobGroupDescription;
    }

    /**
     * Setter for Field: jobGroupDescription
     *
     * @param  jobGroupDescription  the jobGroupDescription to set
     */
    public void setJobGroupDescription(final String jobGroupDescription) {
        this.jobGroupDescription = jobGroupDescription;
    }

    /**
     * Simple Conversion Method - does the Mapping from Database to non Database Entity Model.
     *
     * @return
     */
    public JobSchedulingConfiguration toJobSchedulingConfiguration() {
        final Map<String, String> jobDataMap = Maps.newHashMap();
        for (final String curJobData : jobData) {
            final String[] curJobDataArray = curJobData.split("=");
            if (curJobDataArray != null && curJobDataArray.length == 2) {
                jobDataMap.put(curJobDataArray[0], curJobDataArray[1]);
            }
        }

        JobGroupConfig jobGroupConfig = null;
        if (jobGroupName != null && jobGroupActive != null && jobGroupAppInstanceKeys != null) {
            jobGroupConfig = new JobGroupConfig(jobGroupName, jobGroupActive, Sets.newHashSet(jobGroupAppInstanceKeys));
        }

        final JobConfig jobConfig = new JobConfig(Sets.newHashSet(jobAppInstanceKeys), jobProcessingLimit,
                jobStartupProcessingLimit, jobActive, jobGroupConfig);

        return new JobSchedulingConfiguration(this.jobCronExpression, jobClass, jobDescription, jobDataMap, jobConfig);
    }
}
