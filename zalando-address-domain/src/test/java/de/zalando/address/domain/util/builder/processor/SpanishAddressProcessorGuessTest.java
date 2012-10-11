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
public class SpanishAddressProcessorGuessTest {

    private final String city;
    private final String zip;
    private final String streetName;
    private final String streetAddition;
    private final String expectedCity;
    private final String expectedZip;
    private final String expectedName;
    private final String expectedNr;
    private final String expectedAdditional;
    private final String expectedStreetWithNumber;

    @Parameters
    public static Collection<Object[]> getParameters() {
        final Object[][] data = new Object[][] {

            // formatter:off
            {
                "Bormujos, Sevilla", "41930", "C/ Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Calle Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Plz. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Plaza Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Avd. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Avenida Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Pasaje Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Pasaje Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Pasaje Cerro Colarte, 13B", null, "Bormujos, Sevilla", "41930",
                "Pasaje Cerro Colarte", "13b", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Pasaje Cerro Colarte, 13-B", null, "Bormujos, Sevilla", "41930",
                "Pasaje Cerro Colarte", "13-B", null, null
            },
            {
                "          Bormujos              ,            Sevilla",
                "/////////------------((((((((( 41930            )))))))aha-==============.........",
                "          C     /        Cerro Colarte           , 13", null, "Bormujos, Sevilla", "41930",
                "Calle Cerro Colarte", "13", null, null
            },
            {
                "Bormujos,/// \\\\ Sevilla", "41930", "C/ Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Calle Cerro Colarte", "13", null, null
            },
            {
                "Viladecans, Barcelona", "08880", "Avd. Roureda, 27, 7° A", null, "Viladecans, Barcelona", "08880",
                "Avenida Roureda", "27", "7º A", null
            },
            {
                "Granada", "18002", "C/ Molinos, 66, 3° Izq.", null, "Granada", "18002", "Calle Molinos", "66",
                "3º Izq.", null
            },
            {
                "Sta. Eulalia del Río, Ibiza", "07004", "C/ Pare Vicent Costa, 12, 7° dcha", null,
                "Sta. Eulalia del Río, Ibiza", "07004", "Calle Pare Vicent Costa", "12", "7º dcha", null
            },

            {
                "A Coruña", "01998", "C/ Chueca, n° 9, 3° 4ª", null, "A Coruña", "01998", "Calle Chueca", "Nº 9",
                "3º 4ª", null
            },

            {
                "Elche", "09076", "Avd. De Ausiàs March, blq. 178, esc. A, 5° C", null, "Elche", "09076",
                "Avenida de Ausiàs March", "Bloque 178", "esc. A, 5º C", null
            },
            {
                "Elche", "09076", "Avd. De Ausiàs March, bloque 178, esc. A, 5° C", null, "Elche", "09076",
                "Avenida de Ausiàs March", "Bloque 178", "esc. A, 5º C", null
            },
            {
                "Hospitalet de Llobregat, Barcelona", "08085", "Pl. Europa, 29, 1° izq.", null,
                "Hospitalet de Llobregat, Barcelona", "08085", "Plaza Europa", "29", "1º izq.", null
            },
            {
                "Barcelona", "08765", "Paseo de Gracia, 249, esc. 7, 2° dcha.", null, "Barcelona", "08765",
                "Paseo de Gracia", "249", "esc. 7, 2º dcha.", null
            },

            {
                "Granada", "18002", "Paseo de las Angustias, 20-2", null, "Granada", "18002", "Paseo de las Angustias",
                "20-2", null, null
            },
            {
                "Mairena del Aljarafe, Sevilla", "41004", "Pasaje de Palomares, 12, 5º 1ª", null,
                "Mairena del Aljarafe, Sevilla", "41004", "Pasaje de Palomares", "12", "5º 1ª", null
            },
            {
                "Madrid", "28002", "Paseo de Colón, Edif. 15, plta 5ª, pta. 3ª, C.P", null, "Madrid", "28002",
                "Paseo de Colón", "Edificio 15", "plta 5ª, pta. 3ª, C.P", null
            },
            {
                "Sevilla", "41000", "Plaza de España, n° 24-B, piso 4°, pta. Izq.", null, "Sevilla", "41000",
                "Plaza de España", "Nº 24-B", "piso 4º, pta. Izq.", null
            },

            {"Madrid", "28003", "C/ Ferraz, 38, 1º 1ª", null, "Madrid", "28003", "Calle Ferraz", "38", "1º 1ª", null},
            {
                "Manresa", "27008", "C/ Pi i Maragall, 24, 1r 1a", null, "Manresa", "27008", "Calle Pi i Maragall",
                "24", "1r 1a", null
            },
            {
                "Manresa", "27008", "Pl. Reina Victoria, 2 ent.º", null, "Manresa", "27008", "Plaza Reina Victoria",
                "2 Ent.º", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "C. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Calle Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Cl. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Calle Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Pl. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Plaza Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Plza. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Plaza Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Pza. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Plaza Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Av. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Avenida Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Avd. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Avenida Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Avda. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Avenida Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "P.º Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Paseo Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Edif. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Edificio Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Pta. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Puerta Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Blq. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Bloque Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Ctra. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Carretera Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Plta. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Planta Cerro Colarte", "13", null, null
            },
            {
                "Bormujos, Sevilla", "41930", "Travessera de les Corts, núm. 345, esc. 7, plta. 3, pta. B", null,
                "Bormujos, Sevilla", "41930", "Travessera de les Corts", "Núm. 345", "esc. 7, plta. 3, pta. B", null
            },
            {
                "Bormujos, Sevilla", "41930", "Av. Democracia, núm. 72, esc. 12, p. 5, pta. B", null,
                "Bormujos, Sevilla", "41930", "Avenida Democracia", "Núm. 72", "esc. 12, p. 5, pta. B", null
            },
            {
                "Bormujos, Sevilla", "41930", "Av. Democracia, núm. 72, esc. 12, p. 5, pta. B", null,
                "Bormujos, Sevilla", "41930", "Avenida Democracia", "Núm. 72", "esc. 12, p. 5, pta. B",
                "Avenida Democracia, Núm. 72"
            },
            {
                "Bormujos, Sevilla", "41930", "Plta. Cerro Colarte, 13", null, "Bormujos, Sevilla", "41930",
                "Planta Cerro Colarte", "13", null, "Planta Cerro Colarte, 13"
            },
            // formatter:on
        };
        return Arrays.asList(data);
    }

    public SpanishAddressProcessorGuessTest(final String city, final String zip, final String streetName,
            final String streetAddition, final String expectedCity, final String expectedZip, final String expectedName,
            final String expectedNr, final String expectedAdditional, final String streetWithNumber) {
        this.city = city;
        this.zip = zip;
        this.streetName = streetName;
        this.streetAddition = streetAddition;
        this.expectedCity = expectedCity;
        this.expectedZip = expectedZip;
        this.expectedName = expectedName;
        this.expectedNr = expectedNr;
        this.expectedAdditional = expectedAdditional;
        this.expectedStreetWithNumber = streetWithNumber;
    }

    @Test
    public void testGuessStreetNumber() throws Exception {

        // for (int i = 0; i < 2500; ++i) {
        final Address address = AddressBuilder.forCountry(ISOCountryCode.ES).city(city).zip(zip)
                                              .streetAddition(streetAddition).streetWithHouseNumber(streetName).build();
        assertThat(String.format("orig street [%s] street name", streetName), address.getStreetName(),
            is(expectedName));
        assertThat(String.format("orig street [%s] house number", streetName), address.getHouseNumber(),
            is(expectedNr));
        assertThat(String.format("orig street [%s] additional", streetName), address.getAdditional(),
            is(expectedAdditional));
        assertThat(String.format("orig street [%s] city", streetName), address.getCity(), is(expectedCity));
        assertThat(String.format("orig street [%s] zip", streetName), address.getZip(), is(expectedZip));

        if (expectedStreetWithNumber != null) {
            assertThat(String.format("street with number [%s] zip", expectedStreetWithNumber),
                address.getStreetWithNumber(), is(expectedStreetWithNumber));
        }
    }

}
