package de.zalando.sprocwrapper.example.transformer;

import com.typemapper.core.ValueTransformer;

import de.zalando.sprocwrapper.example.GlobalTransformedObject;

import de.zalando.zomcat.valuetransformer.annotation.GlobalValueTransformer;

@GlobalValueTransformer
public class GlobalTransformedObjectTransformer extends ValueTransformer<String, GlobalTransformedObject> {

    @Override
    public GlobalTransformedObject unmarshalFromDb(final String value) {
        return new GlobalTransformedObject(value);
    }

    @Override
    public String marshalToDb(final GlobalTransformedObject bound) {
        return String.valueOf(bound.getValue());
    }
}
