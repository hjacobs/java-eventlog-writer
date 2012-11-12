package de.zalando.zomcat.jobs.management;

/**
 * Exception thrown by JobSchedulingConfigurationProvider implementations.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public class JobSchedulingConfigurationProviderException extends Exception {

    private static final long serialVersionUID = 7522348501877441568L;

    public JobSchedulingConfigurationProviderException() {
        super();
    }

    public JobSchedulingConfigurationProviderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public JobSchedulingConfigurationProviderException(final String message) {
        super(message);
    }

    public JobSchedulingConfigurationProviderException(final Throwable cause) {
        super(cause);
    }
}
