package de.zalando.address.domain.util.builder;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.zalando.domain.address.Address;
import de.zalando.domain.globalization.ISOCountryCode;

@RunWith(value = Parameterized.class)
public class AddressWildcardTest {

    private final String fromStreet;
    private final String fromHouseNumber;
    private final String fromZip;
    private final String fromCity;
    private final String fromCountryCode;

    private final String expectedStreet;
    private final String expectedHouseNumber;
    private final String expectedZip;
    private final String expectedCity;
    private final ISOCountryCode expectedCountryCode;

    public AddressWildcardTest(final String fromStreet, final String fromHouseNumber, final String fromZip,
            final String fromCity, final String fromCountryCode, final String expectedStreet,
            final String expectedHouseNumber, final String expectedZip, final String expectedCity,
            final ISOCountryCode expectedCountryCode) {

        super();
        this.fromStreet = fromStreet;
        this.fromHouseNumber = fromHouseNumber;
        this.fromZip = fromZip;
        this.fromCity = fromCity;
        this.fromCountryCode = fromCountryCode;

        this.expectedStreet = expectedStreet;
        this.expectedHouseNumber = expectedHouseNumber;
        this.expectedZip = expectedZip;
        this.expectedCity = expectedCity;
        this.expectedCountryCode = expectedCountryCode;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {

        final Object[][] data = new Object[][] {

            // @formatter:off
            {
                "danziger str.", "177", "10407", "Berlin", "DE", "Danziger Str.", "177", "10407", "Berlin",
                ISOCountryCode.DE
            },
            {
                "Danziger             str.", "          177", "         10407", "             Berlin", "DE",
                "Danziger Str.", "177", "10407", "Berlin", ISOCountryCode.DE
            },
            {"", "177", "10407", "Berlin", "DE", "", "177", "10407", "Berlin", ISOCountryCode.DE},
            {
                "danziger str.", null, "10407", "Berlin", "DE", "Danziger Str.", null, "10407", "Berlin",
                ISOCountryCode.DE
            },
            {null, null, "10407", "Berlin", "DE", null, null, "10407", "Berlin", ISOCountryCode.DE},
            {null, null, "10407", null, "DE", null, null, "10407", null, ISOCountryCode.DE},
            {null, null, "10407", null, "FR", null, null, "10407", null, ISOCountryCode.FR},
            {null, null, "10407", null, "IT", null, null, "10407", null, ISOCountryCode.IT},
            {null, null, "4811PG", null, "NL", null, null, "4811 PG", null, ISOCountryCode.NL},
            // @formatter:on
        };
        return Arrays.asList(data);
    }

    @Test
    public void testTransformation() throws Exception {

        AddressBuilder ab = AddressBuilder.forCountry(fromCountryCode);
        if (fromStreet == null) {
            ab = ab.anyStreet();
        } else {
            ab = ab.streetName(fromStreet);
        }

        if (fromHouseNumber == null) {
            ab = ab.anyHouseNumber();
        } else {
            ab = ab.houseNumber(fromHouseNumber);
        }

        if (fromCity == null) {
            ab = ab.anyCity();
        } else {
            ab = ab.city(fromCity);
        }

        final Address address = ab.zip(fromZip).build();

        assertEquals(expectedCity, address.getCity());
        assertEquals(expectedCountryCode, address.getCountryCode());
        assertEquals(expectedZip, address.getZip());
        assertEquals(expectedStreet, address.getStreetName());
        assertEquals(expectedHouseNumber, address.getHouseNumber());
    }
}
