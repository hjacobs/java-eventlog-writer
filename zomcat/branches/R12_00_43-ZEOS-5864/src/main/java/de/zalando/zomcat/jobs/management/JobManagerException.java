package de.zalando.zomcat.jobs.management;

public class JobManagerException extends Exception {

    private static final long serialVersionUID = -1417350732174168954L;

    public JobManagerException() { }

    public JobManagerException(final String arg0) {
        super(arg0);
    }

    public JobManagerException(final Throwable arg0) {
        super(arg0);
    }

    public JobManagerException(final String arg0, final Throwable arg1) {
        super(arg0, arg1);
    }

}
