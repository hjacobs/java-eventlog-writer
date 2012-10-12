package de.zalando.zomcat.jobs.management.persistence;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;
import de.zalando.zomcat.jobs.management.persistence.model.DatabaseJobSchedulingConfiguration;

/**
 * Mapper for {@link JobSchedulingConfiguration}s loaded from Database.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public final class GetJobScheduingConfigsResultMapper
    implements ParameterizedRowMapper<DatabaseJobSchedulingConfiguration> {

    // layout of ordinary configuration option
    private static final String COL_ID = "result_id";
    private static final String COL_JOB_CLASS = "result_job_class";
    private static final String COL_JOB_CRON_EXPRESSION = "result_job_cron_expression";
    private static final String COL_JOB_DESCRIPTION = "result_job_description";
    private static final String COL_JOB_ACTIVE = "result_job_active";
    private static final String COL_JOB_PROCESSING_LIMIT = "result_job_processing_limit";
    private static final String COL_JOB_STARTUP_PROCESSING_LIMIT = "result_job_startup_processing_limit";
    private static final String COL_JOB_APP_INSTANCE_KEYS = "result_job_app_instance_keys";
    private static final String COL_JOB_DATA = "result_job_data";
    private static final String COL_JOB_GROUP_NAME = "result_job_group_name";
    private static final String COL_JOB_GROUP_DESCRIPTION = "result_job_group_description";
    private static final String COL_JOB_GROUP_ACTIVE = "result_job_group_active";
    private static final String COL_JOB_GROUP_APP_INSTANCE_KEYS = "result_job_group_app_instance_keys";
    private static final String COL_CREATED = "result_created";
    private static final String COL_LAST_MODIFIED = "result_last_modified";

    /**
     * Map an Array to a List.
     *
     * @param   objectArray  The SQL Array Object
     *
     * @return  The List of Strings represented by Array
     *
     * @throws  SQLException  if an {@link SQLException} occurs during processing of SQL Array
     */
    private List<String> mapArrayToList(final Array objectArray) throws SQLException {
        final List<String> retVal = Lists.newArrayList();
        if (objectArray != null) {
            final ResultSet resultSet = objectArray.getResultSet();
            while (resultSet.next()) {
                final String arrayElement = resultSet.getString(2);
                retVal.add(arrayElement);
            }

            resultSet.close();
        }

        return retVal;
    }

    /**
     * Map an SQL Array to a {@link Set} of {@link String}s.
     *
     * @param   objectArray  The {@link Array} to process
     *
     * @return  The {@link Set} of {@link String}s represented by {@link Array}
     *
     * @throws  SQLException  if any unanticipated SQL/JDBC error occurs during processing
     */
    private Set<String> mapArrayToSet(final Array objectArray) throws SQLException {
        final Set<String> retVal = Sets.newHashSet(mapArrayToList(objectArray));
        return retVal;
    }

    @Override
    public DatabaseJobSchedulingConfiguration mapRow(final ResultSet rs, final int arg1) throws SQLException {

        final List<String> jobData = mapArrayToList(rs.getArray(COL_JOB_DATA));
        final Set<String> jobAppInstanceKeys = mapArrayToSet(rs.getArray(COL_JOB_APP_INSTANCE_KEYS));
        final Set<String> jobGroupAppInstanceKeys = mapArrayToSet(rs.getArray(COL_JOB_GROUP_APP_INSTANCE_KEYS));

        final DatabaseJobSchedulingConfiguration retVal = new DatabaseJobSchedulingConfiguration();
        retVal.setId(rs.getInt(COL_ID));
        retVal.setJobClass(rs.getString(COL_JOB_CLASS));
        retVal.setJobCronExpression(rs.getString(COL_JOB_CRON_EXPRESSION));
        retVal.setJobActive(rs.getBoolean(COL_JOB_ACTIVE));
        retVal.setJobProcessingLimit(rs.getInt(COL_JOB_PROCESSING_LIMIT));
        retVal.setJobStartupProcessingLimit(rs.getInt(COL_JOB_STARTUP_PROCESSING_LIMIT));
        retVal.setJobAppInstanceKeys(jobAppInstanceKeys);
        retVal.setJobDescription(rs.getString(COL_JOB_DESCRIPTION));
        retVal.setJobData(jobData);
        retVal.setJobGroupName(rs.getString(COL_JOB_GROUP_NAME));
        retVal.setJobGroupActive(rs.getBoolean(COL_JOB_GROUP_ACTIVE));
        retVal.setJobGroupAppInstanceKeys(jobGroupAppInstanceKeys);
        retVal.setJobGroupDescription(rs.getString(COL_JOB_GROUP_DESCRIPTION));
        retVal.setCreated(new Date(rs.getTimestamp(COL_CREATED).getTime()));
        retVal.setLastModified(new Date(rs.getTimestamp(COL_LAST_MODIFIED).getTime()));

        return retVal;
    }

}
