package de.zalando.zomcat.logging;

import java.util.UUID;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:loggingJmxTests.xml")
public class Log4jMBeanTest {

    @Autowired
    @Qualifier("loggingConfigurationProxy")
    private LoggingMBean loggingBean = null;

    @Before
    public void setUp() {

        /*
         * Add de.zalando.zomcat as logging category.
         */
        LogManager.getLogger("de.zalando.zomcat").setLevel(Level.toLevel("DEBUG"));
    }

    @Test
    public void testGetLoggingLevel() {

        String loggingLevel = loggingBean.getLoggerLevel("de.zalando.zomcat");
        Assert.assertEquals(LogManager.getLogger("de.zalando.zomcat").getLevel().toString(), loggingLevel);
    }

    @Test
    public void testGetLoggingLevelWithUnexistantCategory() {

        String loggingLevel = loggingBean.getLoggerLevel(UUID.randomUUID().toString());
        Assert.assertNull(loggingLevel);
    }

    @Test
    public void testSetLoggingLevel() {
        String loggingLevel = "DEBUG";

        loggingBean.setLoggerLevel("de.zalando.zomcat", loggingLevel);
        Assert.assertEquals(LogManager.getLogger("de.zalando.zomcat").getLevel().toString(), loggingLevel);
    }

    @Test
    public void testSetLoggingLevelToUnexistantCategory() {
        String loggingCategory = UUID.randomUUID().toString();
        String loggingLevel = "WARN";

        loggingBean.setLoggerLevel(loggingCategory, loggingLevel);
        Assert.assertEquals(LogManager.getLogger(loggingCategory).getLevel().toString(), loggingLevel);
    }

    @Test(expected = NullPointerException.class)
    public void testSetLoggingLevelWithNullCategoryParameter() {
        String loggingLevel = "DEBUG";

        loggingBean.setLoggerLevel(null, loggingLevel);
    }

    @Test(expected = NullPointerException.class)
    public void testSetLoggingLevelWithNullLevelParameter() {

        loggingBean.setLoggerLevel("de.zalando.zomcat", null);
    }

    @Test(expected = NullPointerException.class)
    public void testSetLoggingLevelWithAllNullParameters() {

        loggingBean.setLoggerLevel(null, null);
    }
}
