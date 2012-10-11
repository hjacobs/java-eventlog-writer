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
public class SwedishAddressProcessorGuessTest {

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

            {"Helsingborg", "252 51", "Drottninggatan 62", "Helsingborg", "252 51", "Drottninggatan", "62", null},
            {"Helsingborg", "252 51", "Drottninggatan 62a", "Helsingborg", "252 51", "Drottninggatan", "62a", null},

            {"SKELLEFTEÅ", "931 92", "Gamla Falmark 21", "Skellefteå", "931 92", "Gamla Falmark", "21", null},

            {"STOCKHOLM", "113 49", "Sveavägen 121 A", "Stockholm", "113 49", "Sveavägen", "121 A", null},
            {"STOCKHOLM", "113 49", "Karl XII gata 17", "Stockholm", "113 49", "Karl XII Gata", "17", null},

            {"ÖSTERVÅLA", "740 46", "Nyvalla 118", "Östervåla", "740 46", "Nyvalla", "118", null},

            /*
             *          {
             *              "VINBERG", "311 05", "Box 5185",
             *
             *              "VINBERG", "311 05", null, null, "Box 5185"
             *          },
             *
             */
            {"Västerås", "725 96", "Björksta Valla", "Västerås", "725 96", "Björksta Valla", null, null},
            {
                "Solna", "171 58",

                "Polhemsgatan 16 2 TR", "Solna", "171 58",

                "Polhemsgatan", "16 2", "TR"
            },
            {
                "Solna", "171 58",

                "Polhemsgatan 16 2 ÖG", "Solna", "171 58",

                "Polhemsgatan", "16 2", "ÖG"
            },
            {"NORA", "713 31", "Rådstugugatan 25", "Nora", "713 31", "Rådstugugatan", "25", null},
            {"AXVALL", "532 93", "Norra Lundby Gärdet", "Axvall", "532 93", "Norra Lundby Gärdet", null, null},
            {"Norrköping", "61830", "Sigynvägen 15  5 TR ÖG", "Norrköping", "618 30", "Sigynvägen", "15 5", "TR ÖG"},
            {"GULLBRANDSTORP", "310 41", "Drakenstigen 4", "Gullbrandstorp", "310 41", "Drakenstigen", "4", null},
            {
                "GULLBRANDSTORP", "310--------------------------41", "Drakenstigen 4", "Gullbrandstorp", "310 41",
                "Drakenstigen", "4", null
            }
        };
        return Arrays.asList(data);

    }

    public SwedishAddressProcessorGuessTest(final String city, final String zip, final String streetName,
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

        final Address address = AddressBuilder.forCountry(ISOCountryCode.SE).city(city).zip(zip)
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
