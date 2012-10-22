package de.zalando.payment.domain.exception;

public class PaymentWebServiceException extends Exception {

    public PaymentWebServiceException(final String message) {
        super(message);
    }

    public PaymentWebServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
