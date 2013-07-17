package de.zalando.typemapper.core.fieldMapper;

import de.zalando.typemapper.core.result.DbResultNode;
import de.zalando.typemapper.postgres.PgTypeHelper.PgTypeDataHolder;

/**
 * @author  danieldelhoyo
 */
public abstract class ObjectMapper<Bound> {
    public abstract Bound unmarshalFromDbNode(DbResultNode dbResultNode);

    public abstract PgTypeDataHolder marshalToDb(Bound value);
}
