package de.zalando.zomcat.jobs;

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;

import de.zalando.zomcat.flowid.FlowId;

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

        // add a flow-id request listener:
        final IRequestCycleListener flowIdRequestCycleListener = new AbstractRequestCycleListener() {
            @Override
            public void onBeginRequest(final RequestCycle cycle) {
                FlowId.clear();
                FlowId.generateAndPushFlowId();
                super.onBeginRequest(cycle);
            }

            @Override
            public void onDetach(final RequestCycle cycle) {
                super.onDetach(cycle);
                FlowId.popFlowId();
            }
        };
        this.getRequestCycleListeners().add(flowIdRequestCycleListener);
    }
}
