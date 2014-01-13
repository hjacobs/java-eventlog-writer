package de.zalando.zomcat.util;

import org.junit.Assert;
import org.junit.Test;

/**
 */
public class FileBackedToggleTest {

    @Test
    public void testDefault() throws Exception {

        FileBackedToggle toggle = new FileBackedToggle("target/toggle1-enabled", false);
        toggle.set(true);
        Assert.assertTrue(toggle.get());
        toggle.set(false);
        Assert.assertFalse(toggle.get());
    }

    @Test
    public void testReverse() throws Exception {

        FileBackedToggle toggle = new FileBackedToggle("target/toggle1-disabled", true);
        toggle.set(true);
        Assert.assertTrue(toggle.get());
        toggle.set(false);
        Assert.assertFalse(toggle.get());
    }

    @Test
    public void testToggle() throws Exception {

        FileBackedToggle toggle = new FileBackedToggle("target/toggle1-enabled", false);
        toggle.set(true);
        Assert.assertTrue(toggle.get());
        Assert.assertFalse(toggle.toggle());
        Assert.assertFalse(toggle.get());
        Assert.assertTrue(toggle.toggle());
        Assert.assertTrue(toggle.get());
    }
}
