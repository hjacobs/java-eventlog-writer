package de.zalando.zomcat.appconfig;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class BaseApplicationConfigImplTest {

    private MockConfiguration configuration;

    private BaseApplicationConfigImpl appConfig;

    private static final FeatureToggle FEATURE = new FeatureToggle("one", "description") { };

    @Before
    public void setUp() throws Exception {
        configuration = new MockConfiguration();
        appConfig = new BaseApplicationConfigImpl();
        appConfig.setConfig(configuration);
    }

    @Test
    public void testToggleOn() throws Exception {
        configuration.setValue("feature.one", "on");
        assertTrue(appConfig.isFeatureEnabled(FEATURE));
    }

    @Test
    public void testToggleOff() throws Exception {
        configuration.setValue("feature.one", "off");
        assertFalse(appConfig.isFeatureEnabled(FEATURE));
    }

    @Test
    public void testToggleOnForAppDomain() throws Exception {
        configuration.setValue("feature.one", "off");
        configuration.setValue("feature.one", 1, "on");
        assertTrue(appConfig.isFeatureEnabled(FEATURE, 1));
        assertFalse(appConfig.isFeatureEnabled(FEATURE, 2));
    }

    @Test
    public void testToggleOffForAppDomain() throws Exception {
        configuration.setValue("feature.one", "on");
        configuration.setValue("feature.one", 1, "off");
        assertFalse(appConfig.isFeatureEnabled(FEATURE, 1));
        assertTrue(appConfig.isFeatureEnabled(FEATURE, 2));
    }
}
