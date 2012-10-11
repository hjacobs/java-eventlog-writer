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
public class DanishAddressProcessorGuessTest {

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
            {
                "København K", "DK-1218", "Christiansborg Slotsplads 1", "København K", "1218",
                "Christiansborg Slotsplads", "1", null
            },
            {   "Frederiksberg", "2000", "Finsensvej 78",
                "Frederiksberg", "2000", "Finsensvej", "78", null
            },
            {   "Rødekro", "6230", "Smedevænget 1",
                "Rødekro", "6230", "Smedevænget", "1", null
            },
            {   "København Ø", "2300", "Østerbrogade 100",
                "København Øst", "2300", "Østerbrogade", "100", null
            },
            {   "Aarhus C", "8000", "Vestergade 10",
                "Aarhus City", "8000", "Vestergade", "10", null
            },
            {   "Esbjerg", "6700", "Vimmelskaftet 10",
                "Esbjerg", "6700", "Vimmelskaftet", "10", null
            },
            {   "Aabenraa", "6200", "Søparken 3, 2.th",
                "Aabenraa", "6200", "Søparken", "3 2.th", null
            },
            {   "Aabenraa", "6200", "Søparken 3, 2th",
                "Aabenraa", "6200", "Søparken", "3 2.th", null
            },
            {   "Slagelse", "4200", "Tagensvej 5a",
                "Slagelse", "4200", "Tagensvej", "5a", null
            },
            {   "Valby", "2500", "Karensgade 10b, 4tv",
                "Valby", "2500", "Karensgade", "10b 4.tv", null
            },
            {   "Valby", "2500", "Karensgade 10b, 4.tv",
                "Valby", "2500", "Karensgade", "10b 4.tv", null
            },
            {   "Valby", "2500", "Karensgade 10b, 4.tv, Disney Land",
                "Valby", "2500", "Karensgade", "10b 4.tv", "Disney Land"
            },
            {   "Valby", "2500", "Karensgade 10b 4.tv, Disney Land",
                "Valby", "2500", "Karensgade", "10b 4.tv", "Disney Land"
            },
            {   "Valby", "2500", "Karensgade 10b 4.th, Disney Land",
                "Valby", "2500", "Karensgade", "10b 4.th", "Disney Land"
            },
            {   "Valby", "2500", "Karensgade 10b 4, Disney Land",
                "Valby", "2500", "Karensgade", "10b 4", "Disney Land"
            },
            {   "Valby", "2500", "Karensgade 10b 4",
                "Valby", "2500", "Karensgade", "10b 4", null
            },
            //J+
            /*,
             *
             * // Grönland
             * {"Nuuk", "3900", "Farip Aqqutaa 8 Postboks 1002", "Nuuk", "3900", "Farip Aqqutaa", "8", "Postboks 1002"},
             *
             * // Färöer-Inselen
             *{"Tórshavn", "FO-110", "Tjóðveldi Postrúm 143", "Tórshavn", "FO-110", null, "Tjóðveldi Postrúm 143"} */
        };
        return Arrays.asList(data);

    }

    public DanishAddressProcessorGuessTest(final String city, final String zip, final String streetName,
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

        // for (int i = 0; i < 2500; ++i) {
        final Address address = AddressBuilder.forCountry(ISOCountryCode.DK).city(city).zip(zip)
                                              .streetWithHouseNumber(streetName).build();
        assertThat(String.format("orig street [%s] street name", streetName), address.getStreetName(),
            is(expectedName));
        assertThat(String.format("orig street [%s] additional", streetName), address.getAdditional(),
            is(expectedAdditional));
        assertThat(String.format("orig street [%s] city", streetName), address.getCity(), is(expectedCity));
        assertThat(String.format("orig street [%s] zip", streetName), address.getZip(), is(expectedZip));
        assertThat(String.format("orig street [%s] house number", streetName), address.getHouseNumber(),
            is(expectedNr));
        // }
    }
}
