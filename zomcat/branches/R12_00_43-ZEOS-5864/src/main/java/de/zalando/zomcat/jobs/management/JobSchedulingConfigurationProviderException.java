package de.zalando.zomcat.jobs.management;

public class JobSchedulingConfigurationProviderException extends Exception {

    private static final long serialVersionUID = 7522348501877441568L;

    public JobSchedulingConfigurationProviderException() {
        super();
    }

    public JobSchedulingConfigurationProviderException(final String arg0, final Throwable arg1) {
        super(arg0, arg1);
    }

    public JobSchedulingConfigurationProviderException(final String arg0) {
        super(arg0);
    }

    public JobSchedulingConfigurationProviderException(final Throwable arg0) {
        super(arg0);
    }
}
