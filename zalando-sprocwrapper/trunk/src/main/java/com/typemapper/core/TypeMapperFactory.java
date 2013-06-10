package com.typemapper.core;

import java.sql.Connection;
import java.sql.SQLException;

import de.zalando.typemapper.core.TypeMapper;
import de.zalando.typemapper.core.ValueTransformer;

@Deprecated // use de.zalando.typemapper.core.TypeMapperFactory instead
public class TypeMapperFactory {

    private TypeMapperFactory() {
        // private constructor: Factory cannot be instantiated
    }

    @Deprecated // use de.zalando.typemapper.core.TypeMapperFactory.createTypeMapper instead
    public static <ITEM> TypeMapper<ITEM> createTypeMapper(final Class<ITEM> clazz) {
        return de.zalando.typemapper.core.TypeMapperFactory.createTypeMapper(clazz);
    }

    @Deprecated // use de.zalando.typemapper.core.TypeMapperFactory.initTypeAndFunctionCaches instead
    public static void initTypeAndFunctionCaches(final Connection connection, final String name) throws SQLException {
        de.zalando.typemapper.core.TypeMapperFactory.initTypeAndFunctionCaches(connection, name);
    }

    @Deprecated // use de.zalando.typemapper.core.TypeMapperFactory.registerGlobalValueTransformer instead
    public static void registerGlobalValueTransformer(final Class<?> clazz,
            final ValueTransformer<?, ?> valueTransformer) {
        de.zalando.typemapper.core.TypeMapperFactory.registerGlobalValueTransformer(clazz, valueTransformer);
    }
}
