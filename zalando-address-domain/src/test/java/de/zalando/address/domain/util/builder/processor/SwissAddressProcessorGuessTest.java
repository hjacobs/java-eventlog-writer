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
public class SwissAddressProcessorGuessTest {

    private final String city;
    private final String zip;
    private final String streetName;
    private final String streetAddition;
    private final String expectedCity;
    private final String expectedZip;
    private final String expectedName;
    private final String expectedNr;
    private final String expectedAdditional;

    @Parameters
    public static Collection<Object[]> getParameters() {
        final Object[][] data = new Object[][] {

            // @formatter:off
            {
                "Zürich", "8004", "Badenerstrasse 230 53 1 2 3 4 5 6 Etage 53", null, "Zürich", "8004", "Badenerstr.",
                "230", "Etage 53"
            },
            {"Zürich", "8004", "Badenerstrasse 230a", null, "Zürich", "8004", "Badenerstr.", "230a", null},
            {
                "Zürich", "8004", "Badenerstrasse 230a Etage 53", null, "Zürich", "8004", "Badenerstr.", "230a",
                "Etage 53"
            },
            {
                "Zürich", "8004", "Badenerstrasse 230 53 Etage 53", null, "Zürich", "8004", "Badenerstr.", "230",
                "Etage 53"
            },
            {"Zürich", "8004", "Le Monde 10", null, "Zürich", "8004", "Le Monde", "10", null},
            {"Zürich", "8004", "rue de du le la homme 10", null, "Zürich", "8004", "Rue de du le la Homme", "10", null},
            {"Zürich", "8004", "rue de la burlesque 10", null, "Zürich", "8004", "Rue de la Burlesque", "10", null},
            {
                "Zürich", "8004", "rue de l'arquebuse l'homme 10", null, "Zürich", "8004", "Rue de l'Arquebuse l'Homme",
                "10", null
            },
            {"Zürich", "8004", "Badenerstrasse 230", null, "Zürich", "8004", "Badenerstr.", "230", null},
            {"Zürich", "8004", "rue de l'Arquebuse 10", null, "Zürich", "8004", "Rue de l'Arquebuse", "10", null},
            {"Zürich", "8004", "homme 10", null, "Zürich", "8004", "Homme", "10", null},
            {
                "Zürich", "8004", "Badenerstrasse 230\53 Etage 53", null, "Zürich", "8004", "Badenerstr.", "230",
                "Etage 53"
            },
            {
                "Zürich", "8004", "Badenerstrasse 230 53a Etage 53", null, "Zürich", "8004", "Badenerstr.", "230",
                "Etage 53"
            },
            {"Zürich", "8004", "Badenerstrasse 230 Etage 53", null, "Zürich", "8004", "Badenerstr.", "230", "Etage 53"},
            {
                "Zürich", "8004", "Badenerstrasse 230 53. Etage", null, "Zürich", "8004", "Badenerstr.", "230",
                "53. Etage"
            },
            {"Zürich", "8004", "Badenerstrasse 230 53 Etage", null, "Zürich", "8004", "Badenerstr.", "230", "53 Etage"},
            {"Zürich", "8004", "Badenerstrasse 230 Etage", null, "Zürich", "8004", "Badenerstr.", "230", null},
            {"Zürich", "8004", "Badenerstrasse Etage 53", null, "Zürich", "8004", "Badenerstr.", null, "Etage 53"},
            {"Hagendorn", "6332", "Rebstock 29", null, "Hagendorn", "6332", "Rebstock", "29", null},
            {"Hagendorn", "6332", "Rebstock 29 3. stock", null, "Hagendorn", "6332", "Rebstock", "29", "3. stock"},
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73., VorDerHaus", null, "Berlin", "10115",
                "Sonnenburgerstr.", "73", "VorDerHaus"
            },

            // @formatter:on
        };
        return Arrays.asList(data);
    }

    public SwissAddressProcessorGuessTest(final String city, final String zip, final String streetName,
            final String streetAddition, final String expectedCity, final String expectedZip, final String expectedName,
            final String expectedNr, final String expectedAdditional) {
        this.city = city;
        this.zip = zip;
        this.streetName = streetName;
        this.streetAddition = streetAddition;
        this.expectedCity = expectedCity;
        this.expectedZip = expectedZip;
        this.expectedName = expectedName;
        this.expectedNr = expectedNr;
        this.expectedAdditional = expectedAdditional;
    }

    @Test
    public void testGuessStreetNumber() throws Exception {

        // for (int i = 0; i < 2500; ++i) {
        final Address address = AddressBuilder.forCountry(ISOCountryCode.CH).city(city).zip(zip)
                                              .streetAddition(streetAddition).streetWithHouseNumber(streetName).build();
        assertThat(String.format("orig street [%s] street name", streetName), address.getStreetName(),
            is(expectedName));
        assertThat(String.format("orig street [%s] house number", streetName), address.getHouseNumber(),
            is(expectedNr));
        assertThat(String.format("orig street [%s] additional", streetName), address.getAdditional(),
            is(expectedAdditional));
        assertThat(String.format("orig street [%s] city", streetName), address.getCity(), is(expectedCity));
        assertThat(String.format("orig street [%s] zip", streetName), address.getZip(), is(expectedZip));
        // }
    }

}
