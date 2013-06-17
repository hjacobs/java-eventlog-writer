package de.zalando.typemapper.core;

/**
 * This class is a copy of the class from the type mapper. we need to provide this base class for compatibility issues
 * with the unified sprocwrapper 1.0 (which uses this new namespace .. de.zalando.typemapper.core)
 */
public abstract class ValueTransformer<Value, Bound> {

    public abstract Bound unmarshalFromDb(String value);

    public abstract Value marshalToDb(Bound bound);
}
