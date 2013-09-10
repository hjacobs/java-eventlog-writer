package de.zalando.catalog.test.util;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import org.junit.experimental.theories.Theories;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContextManager;

@ContextConfiguration(locations = {"classpath:testContext.xml"})
public abstract class AbstractDBTest extends AbstractDBShardExecutorTest {
    private static boolean truncated = false;

    @BeforeClass
    public static final void init() {
        truncated = false;
    }

    @AfterClass
    public static final void destroy() {
        truncated = false;
    }

    @Before
    public void truncateAllTables() throws Exception {
        if (sprocService == null) {

            // if we run Theories we need to explicitly initialize spring
            final RunWith runWith = this.getClass().getAnnotation(RunWith.class);
            if (runWith != null && runWith.value() == Theories.class
                    || runWith != null && runWith.value() == Parameterized.class) {
                final TestContextManager testContextManager = new TestContextManager(this.getClass());
                testContextManager.prepareTestInstance(this);
            }
        }

        if (truncated == false) {
            sprocService.truncateAllTables();
            truncated = true;
        }
    }
}
