package de.zalando.address.domain.util.builder.processor;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertThat;

import static org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;

import de.zalando.address.domain.util.builder.AddressBuilder;

import de.zalando.domain.address.Address;
import de.zalando.domain.globalization.ISOCountryCode;

@RunWith(value = Parameterized.class)
public class NorwegianAddressProcessorGuessTest {

    private final String city;
    private final String zip;
    private final String street;
    private final String additional;
    private final String expectedCity;
    private final String expectedZip;
    private final String expectedStreet;
    private final String expectedNr;
    private final String expectedAdditional;

    @Parameters
    public static Collection<String[]> getParameters() {
        //J-
        final String[][] args = {
            {
                "Tolder Holmers vei 6", "8003", "Bodø", "",
                "Tolder Holmers Vei", "8003", "6", "Bodø", null
            },
            {
                "Jomarveien 43", "1340", "Skui", "",
                "Jomarveien", "1340", "43", "Skui", null
            },
            {
                "Jomarvn. 43", "1340", "Skui", "",
                "Jomarveien", "1340", "43", "Skui", null
            },
            {
                "Essendrops Gate 3", "0368", "OSLO", "",
                "Essendrops Gate", "0368", "3", "Oslo", null
            },
            {
                "Essendrops gt. 3", "0368", "OSLO", "",
                "Essendrops Gate", "0368", "3", "Oslo", null
            },
            {
                "Stortingsgaten 22", "0161", "Oslo", "",
                "Stortingsgaten", "0161", "22", "Oslo", null
            },
            {
                "Rolf E Stenersens Allé 2", "0858", "Oslo", "",
                "Rolf E Stenersens Allé", "0858", "2", "Oslo", null
            },
            {
                "St. Olavs plass 5", "0130", "OSLO", "",
                "St. Olavs Plass", "0130", "5", "Oslo", null
            },
            {
                "Youngstorget 3", "0181", "OSLO", "",
                "Youngstorget", "0181", "3", "Oslo", null
            },
            {
                "PB 2013 Vika", "0125", "Oslo", "", // Abkürzung  PB= Postbox
                "Vika", "0125", null, "Oslo", "PB 2013"
            },
            {
                "Hørte", "4886", "Grimstad", "", // Hofname Hørte anstatt Strassenangabe
                "Hørte", "4886", null, "Grimstad", null
            },
            {
                "Solheimveien 85 B", "1473", "Lørenskog", "", // Hausnummer mit Ziffer und Buchstabenangabe
                "Solheimveien", "1473", "85 B", "Lørenskog", null
            },
            {
                "Solheimvn. 85B", "1473", "Lørenskog", "", // Hausnummer mit Ziffer und Buchstabenangabe
                "Solheimveien", "1473", "85 B", "Lørenskog", null
            },
            {
                "H0101 Christian Krohgs Gate 58", "0186", "Oslo", "", // Adresse mit formaler Wohneinheitsangabe
                "Christian Krohgs Gate", "0186", "58", "Oslo", "H0101"
            },
            {
                "U0101 Christian Krohgs Gate 58", "0186", "Oslo", "", // Adresse mit formaler Wohneinheitsangabe
                "Christian Krohgs Gate", "0186", "58", "Oslo", "U0101"
            },
            {
                "K 0101 Christian Krohgs Gate 58", "0186", "Oslo", "", // Adresse mit formaler Wohneinheitsangabe
                "Christian Krohgs Gate", "0186", "58", "Oslo", "K 0101"
            },
            {
                "L 0101 Christian Krohgs Gate 58", "0186", "Oslo", "", // Adresse mit formaler Wohneinheitsangabe
                "Christian Krohgs Gate", "0186", "58", "Oslo", "L 0101"
            },
            {
                "Ringgata 4 B Etg. 2 Leil. H 02", "0577", "Oslo", "", // Unformale, aber gebräuchliche Haus, -Wohnungs- und Etagenangabe
                "Ringgata", "0577", "4 B", "Oslo", "Et. 2 Leil. H 02"
            },
            {
                "Fetvn. 171 oppgang B", "2007", "Kjeller", "", // oppgang B bdeutet Eingang B (manchmal sind Postkästen auch an verscheidenen Eingängen platziert)
                "Fetveien", "2007", "171", "Kjeller", "oppgang B"
            },
            {
                "Fetvn. 171, blah blah bloop", "2007", "Kjeller", "",
                "Fetveien", "2007", "171", "Kjeller", "blah blah bloop"
            },
            {
                "Fetvn. 171 Etg. 2 L. 23", "2007", "Kjeller", "",
                "Fetveien", "2007", "171", "Kjeller", "Et. 2 Leil. 23"
            },
            {
                "Fetvn. 171 L. 23 Etg. 2", "2007", "Kjeller", "",
                "Fetveien", "2007", "171", "Kjeller", "Leil. 23 Et. 2"
            },
            {
                "Fetvn. 171 Etage. 2 Lei. U 02", "2007", "Kjeller", "",
                "Fetveien", "2007", "171", "Kjeller", "Et. 2 Leil. U 02"
            },
            {
                "Ringgata 4 B Etg. 2 Leilg. H 02", "0557", "Oslo", "",
                "Ringgata", "0557", "4 B", "Oslo", "Et. 2 Leil. H 02"
            },
            {
                "Ringgata 4 B Hus ZZZ Etg. 2 Leilg. H 02", "0557", "Oslo", "",
                "Ringgata", "0557", "4 B", "Oslo", "H. ZZZ Et. 2 Leil. H 02"
            },
        };
        return Arrays.asList(args);
        //J+
    }

    public NorwegianAddressProcessorGuessTest(final String street, final String zip, final String city,
            final String additional, final String expectedStreet, final String expectedZip, final String expectedNr,
            final String expectedCity, final String expectedAdditional) {
        this.city = city;
        this.zip = zip;
        this.street = street;
        this.additional = additional;
        this.expectedCity = expectedCity;
        this.expectedZip = expectedZip;
        this.expectedStreet = expectedStreet;
        this.expectedNr = expectedNr;
        this.expectedAdditional = expectedAdditional;
    }

    @Test
    public void testGuess() throws Exception {
        final Address address = AddressBuilder.forCountry(ISOCountryCode.NO).city(city).zip(zip)
                                              .streetWithHouseNumber(street).streetAddition(additional).build();

        assertThat(String.format("orig street [%s] street name", street), address.getStreetName(), is(expectedStreet));
        assertThat(String.format("orig street [%s] additional", street), address.getAdditional(),
            is(expectedAdditional));
        assertThat(String.format("orig street [%s] city", street), address.getCity(), is(expectedCity));
        assertThat(String.format("orig street [%s] zip", street), address.getZip(), is(expectedZip));
        assertThat(String.format("orig street [%s] house number", street), address.getHouseNumber(), is(expectedNr));
        assertThat(String.format("orig street [%s] additional", street), address.getAdditional(),
            is(expectedAdditional));
    }
}
