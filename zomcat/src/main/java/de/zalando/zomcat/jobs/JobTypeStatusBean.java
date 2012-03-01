package de.zalando.zomcat.jobs;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import de.zalando.zomcat.util.LinkedBoundedQueue;

/**
 * a single type of job with status.
 *
 * @author  fbrick
 */
public class JobTypeStatusBean {

    private static final int MAX_HISTORY_ENTRIES = 50;

    private static final Logger LOG = Logger.getLogger(JobTypeStatusBean.class);

    private static final String KEY_JOB_CLASS = "JOB_CLASS";
    private static final String KEY_RUNNING_WORKER = "RUNNING_WORKER";
    private static final String KEY_LAST_MODIFIED = "LAST_MODIFIED";

    private final Class<?> jobClass;
    private final JobConfig jobConfig;

    private final String description;
    private DateTime lastModified = null;
    private boolean disabled = false;
    private final Map<Integer, RunningWorker> id2RunningWorker = Collections.synchronizedMap(
            new HashMap<Integer, RunningWorker>());
    private final LinkedBoundedQueue<FinishedWorkerBean> history = new LinkedBoundedQueue<FinishedWorkerBean>(
            MAX_HISTORY_ENTRIES);

    // the last QuartzJobInfoBean, to trigger it again
    private QuartzJobInfoBean lastQuartzJobInfoBean = null;

    public JobTypeStatusBean(final Class<?> jobClass, final String description, final JobConfig jobConfig,
            final QuartzJobInfoBean lastQuartzJobInfoBean) {
        this.jobClass = jobClass;
        this.description = description;
        this.jobConfig = jobConfig;
        this.lastQuartzJobInfoBean = lastQuartzJobInfoBean;
    }

    /**
     * @return  the jobClass
     */
    public Class<?> getJobClass() {
        return jobClass;
    }

    /**
     * @return  the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return  the runningWorker size
     */
    public int getRunningWorker() {
        return id2RunningWorker.size();
    }

    /**
     * increment number of running workers by 1.
     */
    public void incrementRunningWorker(final RunningWorker runningWorker,
            final QuartzJobInfoBean lastQuartzJobInfoBean) {
        final RunningWorker existingRunningWorker = id2RunningWorker.get(runningWorker.getId());

        // check if found, otherwise there would be something very strange in
        // system. This should not happen!
        if (existingRunningWorker != null) {
            final String message = "failed to add running worker with id = " + runningWorker.getId() + ", class = "
                    + runningWorker.getClass() + ", hashCode = " + runningWorker.hashCode()
                    + " because there is already a runningWorker with this id and hashCode = "
                    + existingRunningWorker.hashCode() + "!";

            LOG.fatal(message, new JobStatusBeanException(message));

            return;
        }

        id2RunningWorker.put(runningWorker.getId(), runningWorker);

        this.lastQuartzJobInfoBean = lastQuartzJobInfoBean;

        lastModified = new DateTime();
    }

    /**
     * decrement number of running workers by 1.
     */
    public void decrementRunningWorker(final RunningWorker runningWorker) {
        final RunningWorker removedRunningWorker = id2RunningWorker.remove(runningWorker.getId());

        if (removedRunningWorker == null) {
            final String message = "failed to remove running worker with id = " + runningWorker.getId() + ", class = "
                    + runningWorker.getClass();

            LOG.fatal(message, new JobStatusBeanException(message));

            return;
        }

        final FinishedWorkerBean finishedWorkerBean = new FinishedWorkerBean(removedRunningWorker);

        history.add(finishedWorkerBean);

        lastModified = new DateTime();
    }

    /**
     * @return  the history of {@link FinishedWorkerBean FinishedWorkerBean}'s. This list is a copy of the original
     *          list. The bean's are the same objects!
     */
    public List<FinishedWorkerBean> getHistory() {
        final List<FinishedWorkerBean> list = new LinkedList<FinishedWorkerBean>();

        final Iterator<FinishedWorkerBean> iter = history.iterator();

        while (iter.hasNext()) {
            list.add(iter.next());
        }

        return list;
    }

    public void addFinishedWorkerBean(final FinishedWorkerBean finishedWorkerBean) {
        history.add(finishedWorkerBean);
    }

    /**
     * @return  the lastModified
     */
    public DateTime getLastModified() {
        return lastModified;
    }

    private static final DateTimeFormatter DTF = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss:SSS");

    /**
     * @return  the last modified as a formatted String or <code>null</code> if not changed at all so far
     */
    public String getLastModifiedFormatted() {
        if (lastModified == null) {
            return null;
        }

        return DTF.print(lastModified);
    }

    /**
     * @return  map representation of this bean. the returned map is never <code>null</code>.
     */
    public Map<String, String> toMap() {
        final Map<String, String> map = new HashMap<String, String>();

        if (jobClass == null) {
            map.put(KEY_JOB_CLASS, null);
        } else {
            map.put(KEY_JOB_CLASS, jobClass.toString());
        }

        map.put(KEY_RUNNING_WORKER, Integer.toString(id2RunningWorker.size()));

        if (lastModified == null) {
            map.put(KEY_LAST_MODIFIED, null);
        } else {
            map.put(KEY_LAST_MODIFIED, Long.toString(lastModified.getMillis()));
        }

        return map;
    }

    @Override
    public String toString() {
        return "JobTypeStatusBean [jobClass=" + jobClass + ", runningWorker=" + id2RunningWorker.size()
                + ", lastModified=" + lastModified + ", disabled=" + disabled + ", history=" + history
                + ", lastQuartzJobInfoBean = " + lastQuartzJobInfoBean + "]";
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(final boolean disabled) {
        this.disabled = disabled;
    }

    public void toggleMode() {
        disabled = !disabled;
    }

    /**
     * @return  the runningWorkers as a copy of {@link RunningWorkerBean RunningWorkerBean}'s
     */
    public Collection<RunningWorker> getRunningWorkers() {

        // use a sorted map to get sorted order by id
        final SortedMap<Integer, RunningWorker> id2Worker = new TreeMap<Integer, RunningWorker>();

        for (final RunningWorker runningWorker : id2RunningWorker.values()) {
            id2Worker.put(runningWorker.getId(), new RunningWorkerBean(runningWorker));
        }

        return id2Worker.values();
    }

    public JobConfig getJobConfig() {
        return jobConfig;
    }

    /**
     * @return  the lastQuartzJobInfoBean the {@link QuartzJobInfoBean QuartzJobInfoBean} to trigger job again
     */
    public QuartzJobInfoBean getQuartzJobInfoBean() {
        return lastQuartzJobInfoBean;
    }
}
