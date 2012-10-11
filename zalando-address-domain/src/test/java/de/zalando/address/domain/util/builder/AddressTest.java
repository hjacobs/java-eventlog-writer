package de.zalando.address.domain.util.builder;

import static org.junit.Assert.assertEquals;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.zalando.domain.address.Address;
import de.zalando.domain.address.AddressWithDetails;
import de.zalando.domain.globalization.ISOCountryCode;

@RunWith(value = Parameterized.class)
public class AddressTest {

    private final String fromStreetWithNumber;

    private final String fromZip;

    private final String fromCity;

    private final String fromCountryCode;

    private final String expectedStreetWithNumber;

    private final String expectedZip;

    private final String expectedCity;

    private final ISOCountryCode expectedCountryCode;

    public AddressTest(final String fromStreetWithNumber, final String fromZip, final String fromCity,
            final String fromCountryCode, final String expectedStreetWithNumber, final String expectedZip,
            final String expectedCity, final ISOCountryCode expectedCountryCode) {
        super();
        this.fromStreetWithNumber = fromStreetWithNumber;
        this.fromZip = fromZip;
        this.fromCity = fromCity;
        this.fromCountryCode = fromCountryCode;
        this.expectedStreetWithNumber = expectedStreetWithNumber;
        this.expectedZip = expectedZip;
        this.expectedCity = expectedCity;
        this.expectedCountryCode = expectedCountryCode;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        final Object[][] data = new Object[][] {
            {" street  ", "  17'268    ", " City   ", " DE   ", "Street", "17268", "City", ISOCountryCode.DE},
            {
                " 52 RUE DES JONCQUILLES  ", "  99@$#%@$123  @#$  ", " VILLENOUVELLE   ", " FR   ",
                "52 Rue Des Joncquilles", "99123", "Villenouvelle", ISOCountryCode.FR
            },
        };
        return Arrays.asList(data);
    }

    @Test
    public void testAddressEqualsAddressWithSimilarity() throws Exception {
        final Address address = AddressBuilder.forCountry(fromCountryCode).streetWithHouseNumber(fromStreetWithNumber)
                                              .zip(fromZip).city(fromCity).build();

        final AddressWithDetails addressWithDetails = AddressBuilder.forCountry(fromCountryCode)
                                                                    .streetWithHouseNumber(fromStreetWithNumber)
                                                                    .zip(fromZip).city(fromCity).buildWithDetails();

        assertEquals(address, addressWithDetails);

    }

    @Test
    public void testTransformation() throws Exception {
        final Address address = AddressBuilder.forCountry(fromCountryCode).streetWithHouseNumber(fromStreetWithNumber)
                                              .zip(fromZip).city(fromCity).build();

        assertEquals(expectedCity, address.getCity());
        assertEquals(expectedCountryCode, address.getCountryCode());
        assertEquals(expectedZip, address.getZip());
        assertEquals(expectedStreetWithNumber, address.getStreetWithNumber());
    }

    @Test
    public void testEqualsHash() throws Exception {
        final Address address = AddressBuilder.forCountry(fromCountryCode).streetWithHouseNumber(fromStreetWithNumber)
                                              .zip(fromZip).city(fromCity).build();
        final Address address2 = AddressBuilder.forCountry(fromCountryCode).streetWithHouseNumber(fromStreetWithNumber)
                                               .zip(fromZip).city(fromCity).build();

        // check if the same address equals the other
        assertTrue(address.equals(address2));
        assertTrue(address2.equals(address));
        assertEquals(address.hashCode(), address2.hashCode());

        final Address address3 = AddressBuilder.forCountry(fromCountryCode).streetWithHouseNumber("other street")
                                               .zip(fromZip).city(fromCity).build();

        assertFalse(address.equals(address3));
        assertFalse(address3.equals(address));
        assertFalse(address.hashCode() == address3.hashCode());
    }

    @Test
    public void testEqualsHashSimilar() throws Exception {
        final Address address = AddressBuilder.forCountry(fromCountryCode).streetWithHouseNumber(fromStreetWithNumber)
                                              .zip(fromZip).city(fromCity).build();

        final AddressWithDetailsImpl addressWithDetailsImpl = new AddressWithDetailsImpl();
        addressWithDetailsImpl.setAdditional(address.getAdditional());
        addressWithDetailsImpl.setCity(address.getCity());
        addressWithDetailsImpl.setCountryCode(address.getCountryCode());
        addressWithDetailsImpl.setCustomerNumber(address.getCustomerNumber());
        addressWithDetailsImpl.setHouseNumber(address.getHouseNumber());
        addressWithDetailsImpl.setServicePoint(address.getServicePoint());
        addressWithDetailsImpl.setStreetName(address.getStreetName());
        addressWithDetailsImpl.setStreetWithNumber(address.getStreetWithNumber());
        addressWithDetailsImpl.setZip(address.getZip());

        // check if the same address equals the other
        assertTrue(address.equals(addressWithDetailsImpl));
        assertTrue(addressWithDetailsImpl.equals(address));
        assertTrue(address.hashCode() == addressWithDetailsImpl.hashCode());
    }

    @Test
    public void testNotEqualsHashSimilar() throws Exception {
        final Address address = AddressBuilder.forCountry(fromCountryCode).streetWithHouseNumber(fromStreetWithNumber)
                                              .zip(fromZip).city(fromCity).build();

        final AddressWithDetailsImpl addressWithDetailsImpl = new AddressWithDetailsImpl();
        addressWithDetailsImpl.setAdditional(address.getAdditional());
        addressWithDetailsImpl.setCity(address.getCity());
        addressWithDetailsImpl.setCountryCode(address.getCountryCode());
        addressWithDetailsImpl.setCustomerNumber(address.getCustomerNumber());
        addressWithDetailsImpl.setHouseNumber(address.getHouseNumber());
        addressWithDetailsImpl.setServicePoint(address.getServicePoint());
        addressWithDetailsImpl.setStreetName(address.getStreetName());
        addressWithDetailsImpl.setStreetWithNumber(address.getStreetWithNumber());
        addressWithDetailsImpl.setZip("other zip");

        // check if the same address equals the other
        assertFalse(address.equals(addressWithDetailsImpl));
        assertFalse(addressWithDetailsImpl.equals(address));
        assertFalse(address.hashCode() == addressWithDetailsImpl.hashCode());
    }
}
