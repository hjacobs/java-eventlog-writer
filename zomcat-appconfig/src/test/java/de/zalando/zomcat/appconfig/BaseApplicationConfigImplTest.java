package de.zalando.zomcat.appconfig;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import de.zalando.appconfig.ConfigCtx;
import de.zalando.appconfig.Configuration;

public class BaseApplicationConfigImplTest {

    private Configuration configuration;

    private BaseApplicationConfigImpl appConfig;

    private static final FeatureToggle FEATURE = new FeatureToggle("one", "description") { };

    @Before
    public void setUp() throws Exception {
        configuration = mock(Configuration.class);
        appConfig = new BaseApplicationConfigImpl();
        appConfig.setConfig(configuration);
    }

    @Test
    public void testToggleOn() throws Exception {
        when(configuration.getStringConfig("feature.one", null, "OFF")).thenReturn("on");
        assertTrue(appConfig.isFeatureEnabled(FEATURE));
    }

    @Test
    public void testToggleOff() throws Exception {
        when(configuration.getStringConfig("feature.one", null, "OFF")).thenReturn("off");
        assertFalse(appConfig.isFeatureEnabled(FEATURE));
    }

    @Test
    public void testToggleOnForAppDomain() throws Exception {
        when(configuration.getStringConfig("feature.one", new ConfigCtx(2), "OFF")).thenReturn("off");
        when(configuration.getStringConfig("feature.one", new ConfigCtx(1), "OFF")).thenReturn("on");
        assertTrue(appConfig.isFeatureEnabled(FEATURE, 1));
        assertFalse(appConfig.isFeatureEnabled(FEATURE, 2));
    }

    @Test
    public void testToggleOffForAppDomain() throws Exception {
        when(configuration.getStringConfig("feature.one", new ConfigCtx(2), "OFF")).thenReturn("on");
        when(configuration.getStringConfig("feature.one", new ConfigCtx(1), "OFF")).thenReturn("off");
        assertFalse(appConfig.isFeatureEnabled(FEATURE, 1));
        assertTrue(appConfig.isFeatureEnabled(FEATURE, 2));
    }
}
