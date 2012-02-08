package de.zalando;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start
 * class.
 *
 * @see  de.zalando.Start#main(String[])
 */
public class WicketApplication extends WebApplication {

    /**
     * @see  org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<JobMonitorPage> getHomePage() {
        return JobMonitorPage.class;
    }

    /**
     * @see  org.apache.wicket.Application#init()
     */
    @Override
    public void init() {
        super.init();

        // add your configuration here
        this.getComponentInstantiationListeners().add(new SpringComponentInjector(this));
    }
}
