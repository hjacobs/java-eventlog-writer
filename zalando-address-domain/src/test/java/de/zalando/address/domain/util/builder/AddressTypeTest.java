package de.zalando.address.domain.util.builder;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.zalando.domain.address.AddressType;
import de.zalando.domain.globalization.ISOCountryCode;

@RunWith(value = Parameterized.class)
public class AddressTypeTest {

    private final AddressType expected;
    private final ISOCountryCode countryCode;
    private final String address;
    private final String additional;

    @Parameters
    public static Collection<Object[]> getParameters() {

        Object[][] data = new Object[][] {

            // @formatter:off
            {AddressType.DEFAULT, ISOCountryCode.DE, "nothing special address", ""},
            {AddressType.DEFAULT, ISOCountryCode.DE, "nothing special", "address"},
            {AddressType.DEFAULT, ISOCountryCode.DE, "das ist keine packstation", ""},
            {AddressType.DEFAULT, ISOCountryCode.DE, "das ist keine ", " packstation"},
            {AddressType.DEFAULT, ISOCountryCode.IT, "das ist keine ", " packstation"},
            {AddressType.DEFAULT, ISOCountryCode.DE, "invalid Kiala: 13245", ""},
            {AddressType.DEFAULT, ISOCountryCode.DE, "invalid ", "Kiala: 13245"},
            {AddressType.DEFAULT, ISOCountryCode.FR, "das ist keine Packstation", ""},
            {AddressType.DEFAULT, ISOCountryCode.DE, "KIALA: 12345", ""},
            {AddressType.DEFAULT, ISOCountryCode.FR, "KIALA: 12345", " only in additional!"},
            {AddressType.KIALA, ISOCountryCode.FR, "this is a kiala!", "KIALA: 12345"},
            {AddressType.PACKSTATION, ISOCountryCode.DE, "das ist eine Packstation", ""}
            // @formatter:on
        };

        return Arrays.asList(data);
    }

    public AddressTypeTest(final AddressType expected, final ISOCountryCode countryCode, final String address,
            final String additional) {

        this.expected = expected;
        this.countryCode = countryCode;
        this.address = address;
        this.additional = additional;
    }

    @Test
    public void testAddressType() {

        Assert.assertEquals(expected, AddressType.getByAddress(countryCode, address, additional));
    }
}
