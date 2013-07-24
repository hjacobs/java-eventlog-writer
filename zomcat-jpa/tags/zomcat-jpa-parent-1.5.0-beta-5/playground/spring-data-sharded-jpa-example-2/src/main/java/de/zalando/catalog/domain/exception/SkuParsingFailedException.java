package de.zalando.catalog.domain.exception;

public class SkuParsingFailedException extends RuntimeException {

    private static final long serialVersionUID = -5264570468790367268L;

    public SkuParsingFailedException() { }

    public SkuParsingFailedException(final String s) {
        super(s);
    }

    public SkuParsingFailedException(final String s, final Throwable throwable) {
        super(s, throwable);
    }

    public SkuParsingFailedException(final Throwable throwable) {
        super(throwable);
    }
}
