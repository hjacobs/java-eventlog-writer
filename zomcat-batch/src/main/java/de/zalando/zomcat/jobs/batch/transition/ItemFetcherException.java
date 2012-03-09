package de.zalando.zomcat.jobs.batch.transition;

public class ItemFetcherException extends RuntimeException {

    private static final long serialVersionUID = -5546147151989796487L;

    public ItemFetcherException(final String message) {
        super(message);
    }

    public ItemFetcherException(final Throwable cause) {
        super(cause);
    }

    public ItemFetcherException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
