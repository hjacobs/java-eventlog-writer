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
public class PolishAddressProcessorGuessTest {

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

        //J-
        final Object[][] data = new Object[][] {
            {"Kraków", "31-516", "ul. Zamkowa 1 55", "Kraków", "31-516", "ul. Zamkowa 1", "55", null},
            {"Kraków", "31-516", "ul. Zamkowa 1 55 m. 3", "Kraków", "31-516", "ul. Zamkowa 1", "55/3", null},
            {"Kraków", "31-516", "ul. Zamkowa 1 55/3", "Kraków", "31-516", "ul. Zamkowa 1", "55/3", null},
            {"Kraków", "31-516", "ul. Zamkowa 1", "Kraków", "31-516", "ul. Zamkowa", "1", null},
            {"Kraków", "31-516", "ulica Zamkowa 1", "Kraków", "31-516", "ul. Zamkowa", "1", null},
            {"Kraków", "31-516", "Ulica Zamkowa 1", "Kraków", "31-516", "ul. Zamkowa", "1", null},
            {"Kraków", "31-516", "plac Zamkowa 1", "Kraków", "31-516", "pl. Zamkowa", "1", null},
            {"Kraków", "31-516", "aleja Zamkowa 1", "Kraków", "31-516", "al. Zamkowa", "1", null},
            {
                "Wrocław", "50-220", "pl. Tadeusza Kościuszki", "Wrocław", "50-220", "pl. Tadeusza Kościuszki", null,
                null
            },
            {"Warszawa", "02-219", "al. Krakowska 218", "Warszawa", "02-219", "al. Krakowska", "218", null},
            {
                "Warszawa", "03-720", "ul. ks. Kłopotowskiego 33", "Warszawa", "03-720", "ul. Ksiądz Kłopotowskiego",
                "33", null,
            },
            {"Gniezno", "81-354", "pl. Konstytucji 1", "Gniezno", "81-354", "pl. Konstytucji", "1", null},
            {
                "{{{{{{{{{{{{{{*****************************&$#@!#$%Gniezno", "81-354", "pl. Konstytucji 1", "Gniezno",
                "81-354", "pl. Konstytucji", "1", null
            },
            {"Gniezno", "81354", "pl. Konstytucji 1", "Gniezno", "81-354", "pl. Konstytucji", "1", null},
            {
                "Gniezno]]]]]]]][[[[[[[[{}}}}}}}}}}}", "/////////////////81-354", "pl. Konstytucji 1", "Gniezno",
                "81-354", "pl. Konstytucji", "1", null
            },
            {"Kraków", "31-832", "ul. os.* Jagielońskie 7", "Kraków", "31-832", "ul. Osiedle Jagielońskie", "7", null},
            {"Wrocław", "51-013", "pl. bpa. Nankiera 15", "Wrocław", "51-013", "pl. Biskupa Nankiera", "15", null},
            {"Warszawa", "03-113", "ul. Modlińska 353a", "Warszawa", "03-113", "ul. Modlińska", "353a", null},
            {"Warszawa", "00-265", "ul. Piwna 44 m. 3", "Warszawa", "00-265", "ul. Piwna", "44/3", null},
            {"Poznań", "61-312", "ul. Glebowa 30a m. 1", "Poznań", "61-312", "ul. Glebowa", "30a/1", null},
            {"Gniezno", "62-200", "ul. Cicha 132 m. 16", "Gniezno", "62-200", "ul. Cicha", "132/16", null},
            {"Gniezno", "62-200", "ul. Cicha 132 mieszkanie 16", "Gniezno", "62-200", "ul. Cicha", "132/16", null},
            {"Gniezno", "62-200", "ul. Cicha 132/16", "Gniezno", "62-200", "ul. Cicha", "132/16", null},
            {"Gniezno", "62-200", "ul. Cicha 132/16", "Gniezno", "62-200", "ul. Cicha", "132/16", null},
            {
                "Kraków", "31-867", "ul. os. 2 Pułku Lotniczego 47 A",
                "Kraków", "31-867", "ul. Osiedle 2 Pułku Lotniczego", "47 A", null
            },
            {
                "Warszawa", "02-148", "ul. 17 Stycznia 32",
                "Warszawa", "02-148", "ul. 17 Stycznia", "32", null
            },
            {
                "Warszawa", "02-148", "17 Stycznia 32",
                "Warszawa", "02-148", "17 Stycznia", "32", null
            }
        };
        //J+
        return Arrays.asList(data);

    }

    public PolishAddressProcessorGuessTest(final String city, final String zip, final String streetName,
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

        final Address address = AddressBuilder.forCountry(ISOCountryCode.PL).city(city).zip(zip)
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
