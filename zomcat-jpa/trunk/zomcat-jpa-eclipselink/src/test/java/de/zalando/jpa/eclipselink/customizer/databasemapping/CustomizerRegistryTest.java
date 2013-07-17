package de.zalando.jpa.eclipselink.customizer.databasemapping;

import org.eclipse.persistence.mappings.DirectToFieldMapping;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author  jbellmann
 */
public class CustomizerRegistryTest {

    private static CustomizerRegistry customizerRegistry;

    @BeforeClass
    public static void setUpOnce() {
        customizerRegistry = CustomizerRegistry.get();
        Assert.assertNotNull(customizerRegistry);
        customizerRegistry.clear();
    }

    /**
     * Clear registry if all Test were running.
     */
    @AfterClass
    public static void tearDownOnce() {
        Assert.assertNotNull(customizerRegistry);
        customizerRegistry.clear();
    }

    /**
     * Clear registry after every test.
     */
    @After
    public void tearDown() {
        customizerRegistry.clear();
    }

    @Test
    public void getRegistry() {
        CustomizerRegistry registry = CustomizerRegistry.get();
        Assert.assertNotNull(registry);

        // called from another  component
        CustomizerRegistry anotherOne = CustomizerRegistry.get();
        Assert.assertSame(registry, anotherOne);
    }

    @Test
    public void noOpConverterCustomizerShouldBeReturnedIfNoOneIsRegistered() {
        Object result = CustomizerRegistry.get().getConverterCustomizer(new DirectToFieldMapping());
        Assert.assertNotNull(result);
        Assert.assertTrue(NoOpConverterCustomizer.class.isAssignableFrom(result.getClass()));
    }

    @Test
    public void noOpColumnNameCustomizerShouldBeReturnedIfNoOneIsRegistered() {
        Object result = CustomizerRegistry.get().getColumnNameCustomizer(new DirectToFieldMapping());
        Assert.assertNotNull(result);
        Assert.assertSame(NoOpColumnNameCustomizer.class, result.getClass());
    }

}
