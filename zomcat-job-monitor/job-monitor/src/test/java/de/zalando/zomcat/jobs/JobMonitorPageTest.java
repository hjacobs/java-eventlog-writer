package de.zalando.zomcat.jobs;

import org.apache.wicket.WicketRuntimeException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author  henning
 */
public class JobMonitorPageTest {

    /**
     * simple useless test.
     */
    @Test(expected = WicketRuntimeException.class)
    public void testConstructorException() {
        JobMonitorPage page = new JobMonitorPage(null);
        Assert.fail(page.toString());
    }

}
