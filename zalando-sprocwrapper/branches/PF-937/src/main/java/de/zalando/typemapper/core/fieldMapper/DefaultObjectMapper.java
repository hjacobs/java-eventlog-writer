package de.zalando.typemapper.core.fieldMapper;

import de.zalando.typemapper.core.result.DbResultNode;

/**
 * @author  danieldelhoyo
 */
public class DefaultObjectMapper implements GlobalObjectMapper {
    @Override
    public Object unmarshalFromDbNode(final DbResultNode object) {
        return null; // no mapper
    }
}
