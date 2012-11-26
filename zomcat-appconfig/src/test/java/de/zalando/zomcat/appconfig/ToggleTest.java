package de.zalando.zomcat.appconfig;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertThat;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;

public class ToggleTest {

    @Test
    public void testOn() throws Exception {
        for (String value : new String[] {"on", "ON", "On", "oN"}) {
            Toggle actual = Toggle.fromString(value);
            assertThat(actual, is(Toggle.ON));
            assertTrue(actual.asBoolean());
        }
    }

    @Test
    public void testOff() throws Exception {
        for (String value : new String[] {"off", "oFf", "Off", "OFF"}) {
            Toggle actual = Toggle.fromString(value);
            assertThat(actual, is(Toggle.OFF));
            assertFalse(actual.asBoolean());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseError() throws Exception {
        Toggle.fromString("foo");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull() throws Exception {
        Toggle.fromString(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseEmpty() throws Exception {
        Toggle.fromString("");
    }
}
