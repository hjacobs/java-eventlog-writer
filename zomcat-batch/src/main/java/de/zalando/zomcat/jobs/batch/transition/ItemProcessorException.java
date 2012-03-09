package de.zalando.zomcat.jobs.batch.transition;

public class ItemProcessorException extends RuntimeException {

    private static final long serialVersionUID = -5546147151989796487L;

    public ItemProcessorException(final String message) {
        super(message);
    }

    public ItemProcessorException(final Throwable cause) {
        super(cause);
    }

    public ItemProcessorException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
