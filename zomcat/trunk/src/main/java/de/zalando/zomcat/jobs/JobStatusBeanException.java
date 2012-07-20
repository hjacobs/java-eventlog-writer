package de.zalando.zomcat.jobs;

/**
 * exception which is thrown if there is any problem in handling the monitoring in {@link JobsStatusBean JobsStatusBean}.
 *
 * @author  fbrick
 */
public class JobStatusBeanException extends Exception {

    private static final long serialVersionUID = -3895042773806929542L;

    public JobStatusBeanException() {
        super();
    }

    public JobStatusBeanException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JobStatusBeanException(final String message) {
        super(message);
    }

    public JobStatusBeanException(final Throwable cause) {
        super(cause);
    }
}
