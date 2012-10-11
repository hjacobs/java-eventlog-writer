package de.zalando.address.domain.util.builder.processor;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.zalando.address.domain.util.builder.AddressBuilder;

import de.zalando.domain.address.Address;
import de.zalando.domain.globalization.ISOCountryCode;

@RunWith(value = Parameterized.class)
public class BelgianAddressProcessorGuessTest {

    private final String city;

    private final String zip;

    private final String streetName;

    private final String expectedCity;

    private final String expectedZip;

    private final String expectedName;

    private final String expectedNr;

    private final String expectedAdditional;

    @Parameters
    public static Collection<Object[]> getParameters() {
        final Object[][] data = new Object[][] {
            //J-
            {"Gent", "9000", "Gebroeders Van Eyckstraat 16", "Gent", "9000", "Gebroeders Van Eyckstraat", "16", null},
            {"Brussel", "1049", "Weltstraat 200", "Brussel", "1049", "Weltstraat", "200", null},
            {"Hasselt", "3500", "XIX Acacialaan 22 B", "Hasselt", "3500", "XIX Acacialaan", "22b", null},
            {"Hasselt", "3500", "Acacialaan 22b", "Hasselt", "3500", "Acacialaan", "22b", null},
            {"Hasselt", "3500", "Acacialaan 22b foo", "Hasselt", "3500", "Acacialaan", "22b", "foo"},
            {"Hasselt", "3500", "Acacialaan 22 b foo", "Hasselt", "3500", "Acacialaan", "22b", "foo"},
            {"MECHELEN", "2800", "Hallestraat 14", "Mechelen", "2800", "Hallestraat", "14", null},
            {"Waterloo", "1410", "Hallestraat 15 bus 5", "Waterloo", "1410", "Hallestraat", "15", "bus 5"},
            {"Waterloo", "1410", "Hallestraat 15, bus 5", "Waterloo", "1410", "Hallestraat", "15", "bus 5"},
            {
                "Waterloo", "1410", "Hallestraat 15 t.a.v. foobar", "Waterloo",
                "1410", "Hallestraat", "15", "t.a.v. foobar"
            },
            {
                "Waterloo", "1410", "Hallestraat 15 t.a.v. foo bar 1 2 3", "Waterloo",
                "1410", "Hallestraat", "15", "t.a.v. foo bar 1 2 3"
            },
            {
                "Waterloo", "1410", "Hallestraat 15 T.a.v. foo bar 1 2 3", "Waterloo",
                "1410", "Hallestraat", "15", "T.a.v. foo bar 1 2 3"
            },
            {
                "Waterloo", "1410", "Hallestraat 15 TAV. meneer foobar", "Waterloo",
                "1410", "Hallestraat", "15", "TAV. meneer foobar"
            },
            {
                "Brussels", "1000", "83 Michel Ange T11",
                "Brussels", "1000", "Michel Ange", "83", "T11"
            },
            {
                "Brussels", "1000", "83 Michel Ange TAV. 11",
                "Brussels", "1000", "Michel Ange", "83", "TAV. 11"
            },
            {
                "Brussels", "1000", "83 Michel Ange T.AV. 11",
                "Brussels", "1000", "Michel Ange", "83", "T.AV. 11"
            },
            //J+
        };
        return Arrays.asList(data);

    }

    public BelgianAddressProcessorGuessTest(final String city, final String zip, final String streetName,
            final String expectedCity, final String expectedZip, final String expectedName, final String expectedNr,
            final String expectedAdditional) {
        this.city = city;
        this.zip = zip;
        this.streetName = streetName;
        this.expectedCity = expectedCity;
        this.expectedZip = expectedZip;
        this.expectedName = expectedName;
        this.expectedNr = expectedNr;
        this.expectedAdditional = expectedAdditional;
    }

    @Test
    public void testGuessStreetNumber() throws Exception {

        final Address address = AddressBuilder.forCountry(ISOCountryCode.BE).city(city).zip(zip)
                                              .streetWithHouseNumber(streetName).build();
        assertThat(String.format("orig street [%s] street name", streetName), address.getStreetName(),
            is(expectedName));
        assertThat(String.format("orig street [%s] additional", streetName), address.getAdditional(),
            is(expectedAdditional));
        assertThat(String.format("orig street [%s] city", streetName), address.getCity(), is(expectedCity));
        assertThat(String.format("orig street [%s] zip", streetName), address.getZip(), is(expectedZip));
        assertThat(String.format("orig street [%s] house number", streetName), address.getHouseNumber(),
            is(expectedNr));

    }
}
