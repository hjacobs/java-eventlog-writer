package de.zalando.catalog.domain.exception;

public class SkuLoadingFailedException extends RuntimeException {

    private static final long serialVersionUID = 7388958612791613674L;

    public SkuLoadingFailedException() { }

    public SkuLoadingFailedException(final String s) {
        super(s);
    }

    public SkuLoadingFailedException(final String s, final Throwable throwable) {
        super(s, throwable);
    }

    public SkuLoadingFailedException(final Throwable throwable) {
        super(throwable);
    }
}
