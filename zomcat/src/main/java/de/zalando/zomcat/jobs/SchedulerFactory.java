package de.zalando.zomcat.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.quartz.JobDetail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import de.zalando.zomcat.util.DiscardingThreadPoolTaskExecutor;

/**
 * convenience spring factory bean to create schedulers, triggers and taskexecutors using just one simple plain text
 * configuration file scheduler.conf. Example configuration:
 *
 * <p>every 30s after 10s de.zalando.orderengine.backend.jobs.ProcessPaymentNotificationsJob</p>
 *
 * Lines starting with # (comments) and empty lines are ignored in scheduler.conf.
 *
 * @author  henning
 */
public class SchedulerFactory implements BeanDefinitionRegistryPostProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerFactory.class);

    private static final String CONFIGURATION_FILE_NAME = "scheduler.conf";

    private static final String POOL_SIZE_JOB_DATA_KEY = "pool";

    private BeanDefinitionRegistry beanDefinitionRegistry;

    private static long getMillis(final String s) {
        final int len = s.length();
        if (s.endsWith("h")) {

            // hours
            return 60 * 60 * 1000 * Long.valueOf(s.substring(0, len - 1));
        } else if (s.endsWith("m")) {

            // minutes
            return 60 * 1000 * Long.valueOf(s.substring(0, len - 1));
        } else if (s.endsWith("s")) {

            // seconds
            return 1000 * Long.valueOf(s.substring(0, len - 1));
        }

        // millis
        return Long.valueOf(s);
    }

    private Map<String, String> getJobData(final String[] cols, final int startCol) {
        final Map<String, String> map = Maps.newHashMap();
        for (int i = startCol; i < cols.length; i++) {
            final String[] keyValue = cols[i].split("=");
            Preconditions.checkElementIndex(1, keyValue.length, "invalid key=value pair in job data");
            map.put(keyValue[0], keyValue[1]);
        }

        return map;
    }

    private String lowerCaseFirst(final String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    private void createSchedulerFactoryBean(final String name, final Map<String, String> jobData) {
        int poolSize = 1;

        if (jobData != null && jobData.containsKey(POOL_SIZE_JOB_DATA_KEY)) {
            try {
                poolSize = Integer.valueOf(jobData.get(POOL_SIZE_JOB_DATA_KEY));
            } catch (final NumberFormatException nfe) {
                throw new IllegalArgumentException("invalid thread pool size (not an integer)", nfe);
            }
        }

        Preconditions.checkArgument(poolSize >= 1, "thread pool size must be at least 1");

        GenericBeanDefinition def = new GenericBeanDefinition();
        def.setBeanClass(DiscardingThreadPoolTaskExecutor.class);
        def.getPropertyValues().add("corePoolSize", poolSize);
        def.getPropertyValues().add("maxPoolSize", poolSize);
        def.getPropertyValues().add("queueCapacity", 0);
        beanDefinitionRegistry.registerBeanDefinition(name + "Executor", def);

        def = new GenericBeanDefinition();
        def.setBeanClass(SchedulerFactoryBean.class);
        def.getPropertyValues().add("taskExecutor", new RuntimeBeanReference(name + "Executor"));
        def.getPropertyValues().add("triggers", new RuntimeBeanReference(name + "Trigger"));
        def.getPropertyValues().add("applicationContextSchedulerContextKey", "applicationContext");
        def.getPropertyValues().add("exposeSchedulerInRepository", true);

        beanDefinitionRegistry.registerBeanDefinition(name + "Scheduler", def);
    }

    /**
     * format: every INTERVAL after DELAY CLASS JOB_DATA
     *
     * @param   cols
     *
     * @throws  Exception
     */
    private void createSimpleScheduler(final String[] cols) throws Exception {
        Preconditions.checkArgument(cols.length >= 5, "too few columns");
        Preconditions.checkArgument("after".equals(cols[2]), "3rd column must contain the word 'after'");

        final String className = cols[4];
        final Class clazz = Class.forName(className);
        final String name = lowerCaseFirst(clazz.getSimpleName());
        final String repeatInterval = cols[1];
        final String startDelay = cols[3];
        final Map<String, String> jobData = getJobData(cols, 5);

        Preconditions.checkArgument(name.endsWith("Job"), "job class name must end with 'Job': " + name);

        final GenericBeanDefinition def = new GenericBeanDefinition();
        def.setBeanClass(SimpleTriggerBean.class);
        def.getPropertyValues().add("repeatInterval", getMillis(repeatInterval));
        def.getPropertyValues().add("startDelay", getMillis(startDelay));
        def.getPropertyValues().add("jobDetail", new JobDetail(name, clazz));
        def.getPropertyValues().add("jobDataAsMap", jobData);
        beanDefinitionRegistry.registerBeanDefinition(name + "Trigger", def);

        createSchedulerFactoryBean(name, jobData);

        LOG.info("Configured {} to run every {} with start delay {}", new Object[] {name, repeatInterval, startDelay});
    }

    /**
     * format: cron SEC MIN HOUR DOM MON DOW CLASS JOB_DATA
     *
     * @param   cols
     *
     * @throws  Exception
     */
    private void createCronScheduler(final String[] cols) throws Exception {
        Preconditions.checkArgument(cols.length >= 8, "too few columns");

        final String className = cols[7];
        final Class clazz = Class.forName(className);
        final String name = lowerCaseFirst(clazz.getSimpleName());
        final String cronExpression = StringUtils.join(Arrays.copyOfRange(cols, 1, 7), " ");
        final Map<String, String> jobData = getJobData(cols, 8);

        Preconditions.checkArgument(name.endsWith("Job"), "job class name must end with 'Job': " + name);

        final GenericBeanDefinition def = new GenericBeanDefinition();
        def.setBeanClass(CronTriggerBean.class);
        def.getPropertyValues().add("cronExpression", cronExpression);
        def.getPropertyValues().add("jobDetail", new JobDetail(name, clazz));
        def.getPropertyValues().add("jobDataAsMap", jobData);
        beanDefinitionRegistry.registerBeanDefinition(name + "Trigger", def);

        createSchedulerFactoryBean(name, jobData);

        LOG.info("Configured {} to run at {}", new Object[] {name, cronExpression});
    }

    private void createScheduler(final String[] cols) throws Exception {
        if ("every".equals(cols[0])) {
            createSimpleScheduler(cols);
        } else if ("cron".equals(cols[0])) {
            createCronScheduler(cols);
        } else {
            throw new IllegalArgumentException("Unsupported scheduler type: " + cols[0]);
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(final BeanDefinitionRegistry bdr) {
        this.beanDefinitionRegistry = bdr;

        final InputStream stream = getClass().getResourceAsStream("/" + CONFIGURATION_FILE_NAME);

        Preconditions.checkNotNull(stream, "Scheduler configuration missing: " + CONFIGURATION_FILE_NAME);

        String line;
        String[] cols;

        final BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        try {
            int i = 1;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    cols = line.split("\\s+");
                    try {
                        createScheduler(cols);
                    } catch (final Exception ex) {
                        throw new RuntimeException("Configuration error: Could not create scheduler from '"
                                + StringUtils.join(cols, " ") + "' (line " + i + " in " + CONFIGURATION_FILE_NAME + ")",
                            ex);
                    }
                }

                i++;
            }
        } catch (final IOException ex) {
            throw new RuntimeException("Could not read scheduler configuration " + CONFIGURATION_FILE_NAME, ex);
        } finally {
            try {
                br.close();
            } catch (final IOException ex) { }
        }
    }

    @Override
    public void postProcessBeanFactory(final ConfigurableListableBeanFactory clbf) { }

}
