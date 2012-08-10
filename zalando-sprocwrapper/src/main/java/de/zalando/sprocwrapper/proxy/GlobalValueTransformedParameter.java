package de.zalando.sprocwrapper.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import java.sql.Connection;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import com.typemapper.core.ValueTransformer;

public class GlobalValueTransformedParameter extends StoredProcedureParameter {

    private StoredProcedureParameter forwardingStoredProcedureParameter;
    @SuppressWarnings("rawtypes")
    private ValueTransformer valueTransformerForClass;

    public GlobalValueTransformedParameter(final ValueTransformer<?, ?> valueTransformerForClass, final Class<?> clazz,
            final Type genericType, final Method m, final String typeName, final int sqlType, final int javaPosition,
            final boolean sensitive) throws InstantiationException, IllegalAccessException {
        super(getValueTransformedClazz(clazz), m, getValueTransformedTypeName(clazz), getValueTransformedTypeId(clazz),
            javaPosition, sensitive);

        this.valueTransformerForClass = valueTransformerForClass;
        forwardingStoredProcedureParameter = StoredProcedureParameter.createParameter(getValueTransformedClazz(clazz),
                String.class, m, getValueTransformedTypeName(clazz), getValueTransformedTypeId(clazz), javaPosition,
                sensitive);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object mapParam(final Object value, final Connection connection) {
        if (value == null) {
            return forwardingStoredProcedureParameter.mapParam(value, connection);
        }

        if (forwardingStoredProcedureParameter instanceof ArrayStoredProcedureParameter) {
            final List<String> transformedValues = Lists.newArrayList();
            if (value.getClass().isArray()) {
                for (final Object o : ((Object[]) value)) {
                    transformedValues.add(String.valueOf(valueTransformerForClass.marshalToDb(o)));
                }
            } else {
                for (final Object o : ((Collection<?>) value)) {
                    transformedValues.add(String.valueOf(valueTransformerForClass.marshalToDb(o)));
                }
            }

            return forwardingStoredProcedureParameter.mapParam(transformedValues, connection);
        } else {
            return forwardingStoredProcedureParameter.mapParam(valueTransformerForClass.marshalToDb(value), connection);
        }
    }

    @Override
    public int getJavaPos() {
        return forwardingStoredProcedureParameter.getJavaPos();
    }

    @Override
    public boolean isSensitive() {
        return forwardingStoredProcedureParameter.isSensitive();
    }

    @Override
    public int getType() {
        return forwardingStoredProcedureParameter.getType();
    }

    @Override
    public String getTypeName() {
        return forwardingStoredProcedureParameter.getTypeName();
    }

    private static int getValueTransformedTypeId(final Class<?> clazz) {
        if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
            return SQL_MAPPING.get(List.class);
        }

        // this is the only value that is currently available by a transformer
        return SQL_MAPPING.get(String.class);
    }

    private static String getValueTransformedTypeName(final Class<?> clazz) {
        if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
            return "text[]";
        }

        // this is the only value that is currently available by a transformer
        return "text";
    }

    private static Class<?> getValueTransformedClazz(final Class<?> clazz) {
        if (clazz.isArray() || Collection.class.isAssignableFrom(clazz)) {
            return List.class;
        }

        // this is the only value that is currently available by a transformer
        return String.class;
    }
}
