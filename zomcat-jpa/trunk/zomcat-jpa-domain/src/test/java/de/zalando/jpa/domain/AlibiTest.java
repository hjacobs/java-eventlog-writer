package de.zalando.jpa.domain;

import org.junit.Assert;
import org.junit.Test;

/**
 * This test is only to make Jenkins happy. Seems he doesn't like modules without tests.
 *
 * @author  jbellmann
 */
public class AlibiTest {

    @Test
    public void testNullCreatedBy() {
        Persistent p = new Persistent();
        p.setCreatedBy(null);
        Assert.assertEquals(null, p.getCreatedBy());
    }

    @Test
    public void testTrimCreatedBy() {
        Persistent p = new Persistent();
        p.setCreatedBy("klaus.meier@test.de     ");
        Assert.assertEquals("klaus.meier@test.de", p.getCreatedBy());
    }

    static class Persistent extends AbstractCreatable { }

}
