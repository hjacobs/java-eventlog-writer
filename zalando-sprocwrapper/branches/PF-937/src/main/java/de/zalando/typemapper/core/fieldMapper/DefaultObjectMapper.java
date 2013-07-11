package de.zalando.typemapper.core.fieldMapper;

import de.zalando.typemapper.core.result.DbResultNode;

/**
 * @author  danieldelhoyo
 */
public class DefaultObjectMapper extends ObjectMapper<Object, Object> {
    @Override
    public Object unmarshalFromDbNode(final DbResultNode object) {
        return null; // no mapper
    }

    @Override
    public Object marshalToDb(final Object value) {
        return String.valueOf(value);
    }
}
