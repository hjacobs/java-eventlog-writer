package de.zalando.zomcat.jobs.management.impl;

import java.util.Date;

/**
 * Simple Run History Entry for {@link DefaultJobManager} managed Jobs.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public class DefaultJobManagerManagedJobHistory {

    /**
     * Start Date of Job Run.
     */
    private Date startDate;

    /**
     * Finished Date of Job Run.
     */
    private Date stopDate;

    /**
     * Exception that may have been returned on Job Finished.
     */
    private Exception resultException;

    public DefaultJobManagerManagedJobHistory() {
        super();
    }

    /**
     * This method is called when a Job starts. @see {@link DefaultJobManager}
     *
     * @param  startDate  The Start {@link Date} of the Job
     */
    public void jobStarted(final Date startDate) {
        this.startDate = startDate;
    }

    /**
     * This method is calles when the Job finished. @see {@link DefaultJobManager}
     *
     * @param  stopDate      The Stop {@link Date} of the Job
     * @param  jobException  The {@link Exception} associated with Jobs end
     */
    public void jobStopped(final Date stopDate, final Exception jobException) {
        this.stopDate = stopDate;
        this.resultException = jobException;
    }

    /**
     * Getter states whether or not the respective JobExecution finished with an Exception.
     *
     * @return
     */
    public boolean hasResultException() {
        return resultException != null;
    }

    /**
     * Getter for the Result {@link Exception}.
     *
     * @return
     */
    public Exception getResultException() {
        return resultException;
    }

    /**
     * Getter for Jobs Start {@link Date}.
     *
     * @return
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Getter for Jobs Stop {@link Date}.
     *
     * @return
     */
    public Date getStopDate() {
        return stopDate;
    }

    /**
     * Getter for the Runtime of the Job as Milliseconds.
     *
     * @return  The Runtime of the respective Job Execution as Milliseconds
     */
    public long getRuntimeMillis() {
        return stopDate.getTime() - startDate.getTime();
    }

}
