package de.zalando.typemapper.core.fieldMapper;

import de.zalando.typemapper.core.result.DbResultNode;

/**
 * @author  danieldelhoyo
 */
public abstract class ObjectMapper<Value, Bound> {
    public abstract Bound unmarshalFromDbNode(DbResultNode dbResultNode);

    public abstract Value marshalToDb(Bound value);
}
