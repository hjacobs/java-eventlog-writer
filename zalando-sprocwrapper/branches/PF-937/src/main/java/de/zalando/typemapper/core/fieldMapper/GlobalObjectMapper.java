package de.zalando.typemapper.core.fieldMapper;

import de.zalando.typemapper.core.result.DbResultNode;

/**
 * @author  danieldelhoyo
 */
public interface GlobalObjectMapper {
    Object unmarshalFromDbNode(DbResultNode dbResultNode);
}
