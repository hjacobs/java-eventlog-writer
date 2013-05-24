package de.zalando.zomcat.jobs.management.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import de.zalando.zomcat.jobs.Job;
import de.zalando.zomcat.jobs.JobConfig;
import de.zalando.zomcat.jobs.JobConfigSource;
import de.zalando.zomcat.jobs.management.JobSchedulingConfiguration;
import de.zalando.zomcat.jobs.management.JobSchedulingConfigurationProvider;
import de.zalando.zomcat.jobs.management.JobSchedulingConfigurationProviderException;

public final class ClasspathJobSchedulerConfigProvider extends AbstractJobSchedulerConfigProvider
    implements JobSchedulingConfigurationProvider {

    private static final String CONFIGURATION_FILE_NAME = "scheduler.conf";

    @Autowired
    private JobConfigSource jobConfigSource;

    private String schedulingConfigurationFilename = CONFIGURATION_FILE_NAME;

    /**
     * Default Constructor using scheduling.conf File expected to be in Applications Classpath Root.
     */
    public ClasspathJobSchedulerConfigProvider() { }

    /**
     * Constructor using provided Filename for scheduling configuration.
     *
     * @param  schedulingConfigurationFilename  The Filename of scheduling configuration file
     */
    public ClasspathJobSchedulerConfigProvider(final String schedulingConfigurationFilename) {
        Preconditions.checkNotNull(schedulingConfigurationFilename);
        this.schedulingConfigurationFilename = schedulingConfigurationFilename;
    }

    /**
     * format: every INTERVAL after DELAY CLASS JOB_DATA
     *
     * @param   cols
     *
     * @throws  Exception
     */
    private JobSchedulingConfiguration createSimpleScheduler(final String[] cols) throws Exception {
        Preconditions.checkArgument(cols.length >= 5, "too few columns");
        Preconditions.checkArgument("after".equals(cols[2]), "3rd column must contain the word 'after'");

        final String className = cols[4];
        final String repeatInterval = cols[1];
        final String startDelay = cols[3];
        final Map<String, String> jobData = getJobData(cols, 5);

        final JobConfig jobConfig = jobConfigSource.getJobConfig((Job) Class.forName(className).newInstance());
        return new JobSchedulingConfiguration(getMillis(startDelay), getMillis(repeatInterval), className, null,
                jobData, jobConfig);

    }

    /**
     * format: cron SEC MIN HOUR DOM MON DOW CLASS JOB_DATA
     *
     * @param   cols
     *
     * @throws  Exception
     */
    private JobSchedulingConfiguration createCronScheduler(final String[] cols) throws Exception {
        Preconditions.checkArgument(cols.length >= 8, "too few columns");

        final String className = cols[7];
        final String cronExpression = StringUtils.join(Arrays.copyOfRange(cols, 1, 7), " ");
        final Map<String, String> jobData = getJobData(cols, 8);

        // ZEOS-17539 - create a JobConfig instance exactly the same way it is being done for Simple Jobs
        final JobConfig jobConfig = jobConfigSource.getJobConfig((Job) Class.forName(className).newInstance());
        return new JobSchedulingConfiguration(cronExpression, className, null, jobData, jobConfig);
    }

    private JobSchedulingConfiguration createScheduler(final String[] cols) throws Exception {
        JobSchedulingConfiguration retVal = null;
        if ("every".equals(cols[0])) {
            retVal = createSimpleScheduler(cols);
        } else if ("cron".equals(cols[0])) {
            retVal = createCronScheduler(cols);
        } else {
            throw new IllegalArgumentException("Unsupported scheduler type: " + cols[0]);
        }

        return retVal;
    }

    @Override
    public List<JobSchedulingConfiguration> provideSchedulerConfigs()
        throws JobSchedulingConfigurationProviderException {
        final InputStream stream = getClass().getResourceAsStream("/" + schedulingConfigurationFilename);

        Preconditions.checkNotNull(stream, "Scheduler configuration missing: " + schedulingConfigurationFilename);

        String line;
        String[] cols;
        final List<JobSchedulingConfiguration> configs = Lists.newArrayList();

        final BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        try {
            int i = 1;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    cols = line.split("\\s+");
                    try {
                        configs.add(createScheduler(cols));
                    } catch (final Exception ex) {
                        throw new RuntimeException("Configuration error: Could not create scheduler from '"
                                + StringUtils.join(cols, " ") + "' (line " + i + " in "
                                + schedulingConfigurationFilename + ")", ex);
                    }
                }

                i++;
            }
        } catch (final IOException ex) {
            throw new RuntimeException("Could not read scheduler configuration " + schedulingConfigurationFilename, ex);
        } finally {
            try {
                br.close();
            } catch (final IOException ex) { }
        }

        return configs;
    }
}
