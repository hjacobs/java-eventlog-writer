package de.zalando.address.domain.util.builder;

import java.lang.reflect.Type;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.zalando.domain.address.Address;

/**
 * @author  fbrick
 */
public class GsonAddressAdapter implements InstanceCreator<Address>, JsonSerializer<Address>,
    JsonDeserializer<Address> {

    @Override
    public Address createInstance(final Type type) {
        return AddressBuilder.emptyAddress();
    }

    @Override
    public JsonElement serialize(final Address src, final Type typeOfSrc, final JsonSerializationContext context) {
        return context.serialize(src);
    }

    @Override
    public Address deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) {
        final JsonObject jsonObject = (JsonObject) json;

        final AddressBuilder builder = AddressBuilder.forCountry(getString(jsonObject, "countryCode"));

        // @formatter:off
        builder.noNormalization().city(getString(jsonObject, "city"))
               .streetAddition(getString(jsonObject, "additional"))
               .streetWithHouseNumber(getString(jsonObject, "streetWithNumber"))
               .streetName(getString(jsonObject, "streetName")).houseNumber(getString(jsonObject, "houseNumber")).zip(
                   getString(jsonObject, "zip"));
        // @formatter:on

        return builder.build();
    }

    private static String getString(final JsonObject jsonObject, final String key) {
        final JsonPrimitive jsonPrimitive = jsonObject.getAsJsonPrimitive(key);

        if (jsonPrimitive == null) {
            return null;
        }

        return jsonPrimitive.getAsString();
    }
}
