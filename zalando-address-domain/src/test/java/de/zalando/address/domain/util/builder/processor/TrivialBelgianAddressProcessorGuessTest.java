package de.zalando.address.domain.util.builder.processor;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertThat;

import org.junit.Test;

import de.zalando.address.domain.util.builder.AddressBuilder;

import de.zalando.domain.address.Address;
import de.zalando.domain.globalization.ISOCountryCode;

public class TrivialBelgianAddressProcessorGuessTest {

    @Test
    public void testGuessStreetNumber() throws Exception {

        final Address address = AddressBuilder.forCountry(ISOCountryCode.BE).city("HASSELT").zip("3500")
                                              .streetWithHouseNumber("Grote steenweg 10A").build();

        assertThat(address.getCity(), is("Hasselt"));
        assertThat(address.getStreetName(), is("Grote Steenweg"));
        assertThat(address.getHouseNumber(), is("10a"));
        assertThat(address.getZip(), is("3500"));
    }
}
