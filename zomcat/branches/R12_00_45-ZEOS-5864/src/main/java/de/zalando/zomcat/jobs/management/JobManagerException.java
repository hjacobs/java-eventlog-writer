package de.zalando.zomcat.jobs.management;

/**
 * Exception thrown by JobManager components.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public class JobManagerException extends Exception {

    private static final long serialVersionUID = -1417350732174168954L;

    public JobManagerException() { }

    public JobManagerException(final String message) {
        super(message);
    }

    public JobManagerException(final Throwable cause) {
        super(cause);
    }

    public JobManagerException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
