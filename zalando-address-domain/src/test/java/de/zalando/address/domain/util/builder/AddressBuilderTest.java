package de.zalando.address.domain.util.builder;

import static org.hamcrest.core.IsEqual.equalTo;

import static org.junit.Assert.assertThat;

import org.junit.Assert;
import org.junit.Test;

import de.zalando.domain.address.Address;
import de.zalando.domain.globalization.ISOCountryCode;

public class AddressBuilderTest {

    private static final String STREET = "zinnowitzer str !@#$%$%";
    private static final String HOUSE_NUMBER = "@#$! 1";
    private static final String ZIP = "123asdas45";
    private static final String CITY = "berlin";

    private static final String EXPECTED_STREET = "Zinnowitzer Str";
    private static final String EXPECTED_HOUSE_NUMBER = "1";
    private static final String EXPECTED_ZIP = "12345";
    private static final String EXPECTED_CITY = "Berlin";

    @Test
    public void testBuilder() throws Exception {
        final Address address = AddressBuilder.forCountry(ISOCountryCode.DE).streetName(STREET)
                                              .houseNumber(HOUSE_NUMBER).zip(ZIP).city(CITY).build();

        assertThat(address.getCity(), equalTo(EXPECTED_CITY));
        assertThat(address.getZip(), equalTo(EXPECTED_ZIP));
        assertThat(address.getStreetName(), equalTo(EXPECTED_STREET));
        assertThat(address.getHouseNumber(), equalTo(EXPECTED_HOUSE_NUMBER));
        assertThat(address.getCountryCode(), equalTo(ISOCountryCode.DE));
    }

    @Test
    public void testNoNormalization() throws Exception {
        final Address address = AddressBuilder.forCountry(ISOCountryCode.DE).noNormalization().streetName(STREET)
                                              .houseNumber(HOUSE_NUMBER).zip(ZIP).city(CITY).build();

        assertThat(address.getCity(), equalTo(CITY));
        assertThat(address.getZip(), equalTo(ZIP));
        assertThat(address.getStreetName(), equalTo(STREET));
        assertThat(address.getHouseNumber(), equalTo(HOUSE_NUMBER));
        assertThat(address.getCountryCode(), equalTo(ISOCountryCode.DE));
    }

    @Test
    public void testUK2GB() throws Exception {
        final Address address = AddressBuilder.forCountry("UK").streetWithHouseNumber("1 downing streeet").zip(ZIP)
                                              .city(CITY).build();
        assertThat(address.getCountryCode(), equalTo(ISOCountryCode.GB));
    }

    @Test
    public void testInvalidControlCharacter() throws Exception {
        final Address address = AddressBuilder.forCountry(ISOCountryCode.GB)
                                              .streetWithHouseNumber("\u0000\u0001\u0002\u0003\u0004\u0005"
                                                      + "\u0006\u0007\u0008\u0009\u000B\u000C\u000E\u000F"
                                                      + "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a"
                                                      + "\u001b\u001c\u001d\u001e\u001f\n\r\t" + "1 downing streeet")
                                              .zip(ZIP).city(CITY).build();
        assertThat(address.getCountryCode(), equalTo(ISOCountryCode.GB));
        assertThat(address.getCity(), equalTo("Berlin"));
        assertThat(address.getZip(), equalTo("123ASDA S45"));
        assertThat(address.getStreetName(), equalTo("1 Downing Streeet"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testForAddressThrowsExceptionWhenNull() {

        AddressBuilder.forAddress(null).build();

        Assert.fail("should have thrown exception");
    }

}
