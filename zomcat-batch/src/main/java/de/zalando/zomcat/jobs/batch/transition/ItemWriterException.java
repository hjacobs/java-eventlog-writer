package de.zalando.zomcat.jobs.batch.transition;

public class ItemWriterException extends RuntimeException {

    private static final long serialVersionUID = -5546147151989796487L;

    public ItemWriterException(final String message) {
        super(message);
    }

    public ItemWriterException(final Throwable cause) {
        super(cause);
    }

    public ItemWriterException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
