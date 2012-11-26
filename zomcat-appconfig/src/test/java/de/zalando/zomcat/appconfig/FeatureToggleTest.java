package de.zalando.zomcat.appconfig;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FeatureToggleTest {

    @Test
    public void testGetToggleName() throws Exception {
        assertThat(createFeatureToggle("one").getToggleName(), is("one"));
    }

    @Test
    public void testGetAppConfigName() throws Exception {
        assertThat(createFeatureToggle("one").getAppConfigName(), is("feature.one"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadToggle() throws Exception {
        createFeatureToggle("feature.nono");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullName() throws Exception {
        createFeatureToggle(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyName() throws Exception {
        createFeatureToggle("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullDescription() throws Exception {
        createFeatureToggle("value", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyDescription() throws Exception {
        createFeatureToggle("value", "");
    }

    private FeatureToggle createFeatureToggle(final String value) {
        return createFeatureToggle(value, "description");
    }

    private FeatureToggle createFeatureToggle(final String value, final String description) {
        return new FeatureToggle(value, description) { };
    }
}
