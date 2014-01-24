package de.zalando.zomcat.crypto;

public class ZomcatCryptoException extends Exception {

    private static final long serialVersionUID = 1L;

    public ZomcatCryptoException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ZomcatCryptoException(final String message) {
        super(message);
    }

}
