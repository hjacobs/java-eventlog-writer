package de.zalando.sprocwrapper.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import java.sql.Connection;

import java.util.Collection;

import com.typemapper.annotations.DatabaseType;

import com.typemapper.postgres.PgArray;
import com.typemapper.postgres.PgTypeHelper;

/**
 * @author  jmussler
 */
class ArrayStoredProcedureParameter extends StoredProcedureParameter {

    protected String innerTypeName = null;

    public ArrayStoredProcedureParameter(final Class<?> clazz, final Method m, final String typeName, final int sqlType,
            final int javaPosition, final boolean sensitive) {
        super(clazz, m, typeName, sqlType, javaPosition, sensitive);

        if (typeName != null && typeName.endsWith("[]")) {
            innerTypeName = typeName.substring(0, typeName.length() - 2);
        } else if (typeName != null && !"".equals(typeName)) {
            throw new IllegalArgumentException("SprocService-Param: [" + clazz.getName() + ", " + m.getName()
                    + "] Provided typename must end with [] in case of list parameters: " + typeName);
        } else {
            final java.lang.reflect.Type parameterType = m.getGenericParameterTypes()[javaPosition];

            if (!(parameterType instanceof ParameterizedType)) {
                throw new IllegalArgumentException("SprocService-Param: [" + clazz.getName() + ", " + m.getName()
                        + "] Parameter must be of type Parametrized List<?> but is: " + parameterType.toString());
            } else {
                final ParameterizedType p = (ParameterizedType) parameterType;
                final Class<?> paramsClass = (Class<?>) p.getActualTypeArguments()[0];

                innerTypeName = PgTypeHelper.getSQLNameForClass(paramsClass);
                if (innerTypeName == null) {

                    final DatabaseType dbType = paramsClass.getAnnotation(DatabaseType.class);
                    if (dbType == null) {
                        innerTypeName = SProcProxyBuilder.camelCaseToUnderscore(paramsClass.getSimpleName());
                    } else {
                        innerTypeName = dbType.name();
                    }
                }
            }
        }
    }

    @Override
    public Object mapParam(final Object value, final Connection connection) {
        if (value == null) {
            return null;
        }

        Object result = value;
        result = PgArray.ARRAY((Collection<?>) value);
        if (innerTypeName != null) {
            result = ((PgArray<?>) result).asJdbcArray(innerTypeName, connection);
        }

        return result;
    }

}
