package com.typemapper.core;

@Deprecated // use de.zalando.typemapper.core.ValueTransformer instead
public abstract class ValueTransformer<Value, Bound> {

    public abstract Bound unmarshalFromDb(String value);

    public abstract Value marshalToDb(Bound bound);
}
