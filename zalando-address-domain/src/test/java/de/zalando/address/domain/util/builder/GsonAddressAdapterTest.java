package de.zalando.address.domain.util.builder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.zalando.domain.address.Address;
import de.zalando.domain.globalization.ISOCountryCode;

public class GsonAddressAdapterTest {

    @Test
    public final void testSerializeDeserialize() {
        final Gson gson = new GsonBuilder().registerTypeAdapter(Address.class, new GsonAddressAdapter()).create();
        final AddressBuilder builder = new AddressBuilder(ISOCountryCode.DE);

        builder.city("city");
        builder.houseNumber("houseNumber");
        builder.streetAddition("streetAddition");
        builder.streetWithHouseNumber("streetWithHouseNumber");
        builder.streetName("streetName");
        builder.zip("12345");

        final Address src = builder.build();

        System.err.println(src);

        final String json = gson.toJson(src, Address.class);

        System.err.println(json);

        final Address dst = gson.fromJson(json, Address.class);

        System.err.println(dst);

        assertNotNull(dst);
        assertEquals(src.getCity(), dst.getCity());
        assertEquals(src.getHouseNumber(), dst.getHouseNumber());
        assertEquals(src.getStreetName(), dst.getStreetName());
        assertEquals(src.getStreetWithNumber(), dst.getStreetWithNumber());
        assertEquals(src.getAdditional(), dst.getAdditional());
        assertEquals(src.getZip(), dst.getZip());
        assertEquals(src.getCountryCode(), dst.getCountryCode());
    }
}
