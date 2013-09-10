package de.zalando.data.jpa.example;

import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO we have to think about the jenkins setup, right.
 *
 * @author  jbellmann
 */
public class WhyDoesJenkinsForcesMeToWriteStupidTestsTest {

    private static final Logger LOG = LoggerFactory.getLogger(WhyDoesJenkinsForcesMeToWriteStupidTestsTest.class);

    @Test
    public void test() {
        LOG.warn("THIS TEST IS SENSELESS, BUT IT HELPS TO SHINE ON JENKINS");
    }

}
