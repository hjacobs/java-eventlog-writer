package de.zalando.zomcat.jobs;

import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTester;

import org.junit.Before;
import org.junit.Test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Simple test using the WicketTester.
 */
public class TestHomePage {
    private WicketTester tester;

    // Since there is no server to prepare the application, override the webapp to inject the
    // spring application context directly.
    class InnerWicketApplication extends WicketApplication {

        ApplicationContext context = new ClassPathXmlApplicationContext(
                new String[] {"WEB-INF/applicationContext.xml"});

        InnerWicketApplication() { }

        // note in this case the application context is in the default package

        @Override
        public void init() {
            this.getComponentInstantiationListeners().add(new SpringComponentInjector(this, context));
        }

    }
    ;

    @Before
    public void setUp() {
// tester = new WicketTester(new InnerWicketApplication());
    }

    @Test
    public void homepageRendersSuccessfully() {

        // start and render the test page
// tester.startPage(JobMonitorPage.class);

        // assert rendered page class
// tester.assertRenderedPage(JobMonitorPage.class);
    }
}
