package de.zalando.address.domain.util.builder;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

import de.zalando.address.domain.util.builder.processor.DefaultAddressProcessor;

import de.zalando.utils.Pair;

import junit.framework.Assert;

public class DefaultAddressProcessorTest {
    @Test
    public void testTrim() throws Exception {
        final DefaultAddressProcessor processor = new DefaultAddressProcessor();
        Pair<String, String> normalizeCity = processor.normalizeCity("   bla      ");
        String actual = normalizeCity.getFirst();
        Assert.assertEquals("bla", actual);

        Pair<String, String> actualStreet = processor.normalizeStreet("   bla      ");
        Assert.assertEquals("bla", actualStreet.getFirst());
        actual = processor.normalizeZip("   bla      ");
        Assert.assertEquals("bla", actual);
        actual = processor.normalizeCountryCode("   bla      ");
        Assert.assertEquals("bla", actual);
    }

    @Test
    public void testGuess() throws Exception {
        final DefaultAddressProcessor processor = new DefaultAddressProcessor();

        final AddressGuess actual = processor.guessStreetNameAndNumber("bla str 1");
        assertEquals("bla str 1", actual.getStreetName());
        assertEquals("", actual.getHouseNumber());
    }
}
