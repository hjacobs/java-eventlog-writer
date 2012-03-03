package de.zalando.zomcat.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.SmartLifecycle;

import org.springframework.scheduling.quartz.CronTriggerBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerBean;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
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
public class SchedulerFactory implements ApplicationContextAware, SmartLifecycle {

    private static final Logger LOG = LoggerFactory.getLogger(SchedulerFactory.class);

    private static final String CONFIGURATION_FILE_NAME = "scheduler.conf";

    private ApplicationContext applicationContext;

    private final List<SchedulerFactoryBean> schedulers = Lists.newArrayList();

    private static long getMillis(final String s) {
        int len = s.length();
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
        Map<String, String> map = Maps.newHashMap();
        for (int i = startCol; i < cols.length; i++) {
            String[] keyValue = cols[i].split("=");
            Preconditions.checkElementIndex(1, keyValue.length, "invalid key=value pair in job data");
            map.put(keyValue[0], keyValue[1]);
        }

        return map;
    }

    private String lowerCaseFirst(final String s) {
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }

    private SchedulerFactoryBean createSchedulerFactoryBean(final AutowireCapableBeanFactory beanFactory,
            final String name, final Trigger trigger) {
        final DiscardingThreadPoolTaskExecutor taskExecutor = new DiscardingThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1);
        taskExecutor.setMaxPoolSize(1);
        taskExecutor.setQueueCapacity(0);
        beanFactory.initializeBean(taskExecutor, name + "Executor");

        final SchedulerFactoryBean factory = new SchedulerFactoryBean();
        factory.setTaskExecutor(taskExecutor);
        factory.setTriggers(new Trigger[] {trigger});
        factory.setApplicationContext(applicationContext);
        factory.setApplicationContextSchedulerContextKey("applicationContext");
        beanFactory.initializeBean(factory, name + "Factory");
        return factory;
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

        final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();

        final String className = cols[4];
        final Class clazz = Class.forName(className);
        final String name = lowerCaseFirst(clazz.getSimpleName());
        final String repeatInterval = cols[1];
        final String startDelay = cols[3];

        Preconditions.checkArgument(name.endsWith("Job"), "job class name must end with 'Job': " + name);

        final SimpleTriggerBean trigger = new SimpleTriggerBean();
        trigger.setRepeatInterval(getMillis(repeatInterval));
        trigger.setStartDelay(getMillis(startDelay));
        trigger.setJobDetail(new JobDetail(name, clazz));
        trigger.setJobDataAsMap(getJobData(cols, 5));
        trigger.setName(name + "Trigger");
        beanFactory.initializeBean(trigger, name + "Trigger");

        final SchedulerFactoryBean factory = createSchedulerFactoryBean(beanFactory, name, trigger);
        schedulers.add(factory);

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
        Preconditions.checkArgument(cols.length >= 9, "too few columns");

        final AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();

        final String className = cols[7];
        final Class clazz = Class.forName(className);
        final String name = lowerCaseFirst(clazz.getSimpleName());
        final String cronExpression = StringUtils.join(Arrays.copyOfRange(cols, 1, 7), " ");

        Preconditions.checkArgument(name.endsWith("Job"), "job class name must end with 'Job': " + name);

        final CronTriggerBean trigger = new CronTriggerBean();
        trigger.setCronExpression(cronExpression);
        trigger.setJobDetail(new JobDetail(name, clazz));
        trigger.setJobDataAsMap(getJobData(cols, 8));
        trigger.setName(name + "Trigger");
        beanFactory.initializeBean(trigger, name + "Trigger");

        final SchedulerFactoryBean factory = createSchedulerFactoryBean(beanFactory, name, trigger);
        schedulers.add(factory);

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
    public void setApplicationContext(final ApplicationContext ac) throws BeansException {
        applicationContext = ac;

        InputStream stream = getClass().getResourceAsStream("/" + CONFIGURATION_FILE_NAME);

        Preconditions.checkNotNull(stream, "Scheduler configuration missing: " + CONFIGURATION_FILE_NAME);

        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line;
        String[] cols;
        try {
            int i = 1;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    cols = line.split("\\s+");
                    try {
                        createScheduler(cols);
                    } catch (Exception ex) {
                        throw new RuntimeException("Configuration error: Could not create scheduler from '"
                                + StringUtils.join(cols, " ") + "' (line " + i + " in " + CONFIGURATION_FILE_NAME + ")",
                            ex);
                    }
                }

                i++;
            }
        } catch (IOException ex) {
            throw new RuntimeException("Could not read scheduler configuration " + CONFIGURATION_FILE_NAME, ex);
        }

    }

    @Override
    public boolean isRunning() {
        for (SchedulerFactoryBean scheduler : schedulers) {
            if (scheduler.isRunning()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void start() {
        for (SchedulerFactoryBean scheduler : schedulers) {
            scheduler.start();
        }
    }

    @Override
    public void stop() {
        for (SchedulerFactoryBean scheduler : schedulers) {
            scheduler.stop();
        }
    }

    @Override
    public void stop(final Runnable r) {
        stop();
        r.run();
    }

    @Override
    public boolean isAutoStartup() {
        return true;
    }

    @Override
    public int getPhase() {
        return 2147483646;
    }

    public void destroy() throws SchedulerException {
        for (SchedulerFactoryBean scheduler : schedulers) {
            scheduler.destroy();
        }
    }

}
