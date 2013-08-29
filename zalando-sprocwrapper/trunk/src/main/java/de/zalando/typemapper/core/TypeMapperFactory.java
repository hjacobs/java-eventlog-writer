package de.zalando.typemapper.core;

import java.sql.Connection;
import java.sql.SQLException;

import de.zalando.typemapper.core.db.DbFunctionRegister;
import de.zalando.typemapper.core.fieldMapper.GlobalValueTransformerRegistry;

public class TypeMapperFactory {

    private TypeMapperFactory() {
        // private constructor: Factory cannot be instantiated
    }

    public static <ITEM> TypeMapper<ITEM> createTypeMapper(final Class<ITEM> clazz) {
        return new TypeMapper<ITEM>(clazz);
    }

    public static void initTypeAndFunctionCaches(final Connection connection, final String name) throws SQLException {

        // TODO pribeiro analyze DbFunctionRegister and check if we are caching all functions from each database in a
        // single place.
        DbFunctionRegister.initRegistry(connection, name);
    }

    public static void registerGlobalValueTransformer(final Class<?> clazz,
            final ValueTransformer<?, ?> valueTransformer) {
        GlobalValueTransformerRegistry.register(clazz, valueTransformer);
    }
}
