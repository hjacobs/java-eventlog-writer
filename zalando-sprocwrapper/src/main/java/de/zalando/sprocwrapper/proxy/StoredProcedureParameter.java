package de.zalando.sprocwrapper.proxy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import org.postgresql.util.PGobject;

import com.typemapper.postgres.PgArray;
import com.typemapper.postgres.PgTypeHelper;

/**
 * @author  jmussler
 */
class StoredProcedureParameter {

    private static final Logger LOG = Logger.getLogger(StoredProcedureParameter.class);

    private static final Map<Class, Integer> SQL_MAPPING = new HashMap<Class, Integer>();

    static {
        SQL_MAPPING.put(int.class, java.sql.Types.INTEGER);
        SQL_MAPPING.put(Integer.class, java.sql.Types.INTEGER);
        SQL_MAPPING.put(long.class, java.sql.Types.BIGINT);
        SQL_MAPPING.put(Long.class, java.sql.Types.BIGINT);
        SQL_MAPPING.put(float.class, java.sql.Types.FLOAT);
        SQL_MAPPING.put(Float.class, java.sql.Types.FLOAT);
        SQL_MAPPING.put(double.class, java.sql.Types.DOUBLE);
        SQL_MAPPING.put(Double.class, java.sql.Types.DOUBLE);
        SQL_MAPPING.put(String.class, java.sql.Types.VARCHAR);
        SQL_MAPPING.put(java.sql.Date.class, java.sql.Types.TIMESTAMP);
        SQL_MAPPING.put(Date.class, java.sql.Types.TIMESTAMP);
        SQL_MAPPING.put(List.class, java.sql.Types.ARRAY);
        SQL_MAPPING.put(short.class, java.sql.Types.SMALLINT);
        SQL_MAPPING.put(Short.class, java.sql.Types.SMALLINT);
        SQL_MAPPING.put(boolean.class, java.sql.Types.BOOLEAN);
        SQL_MAPPING.put(Boolean.class, java.sql.Types.BOOLEAN);
        SQL_MAPPING.put(char.class, java.sql.Types.CHAR);
        SQL_MAPPING.put(Character.class, java.sql.Types.CHAR);
    }

    private String typeName;
    private int type;
    private int sqlPos;
    private int javaPos;
    private Class clazz;
    private boolean sensitive;

    public StoredProcedureParameter(final Class clazz, final String typeName, final int sqlType, final int sqlPosition,
            final int javaPosition, final boolean sensitive) {
        if (typeName == null || typeName.isEmpty()) {
            this.typeName = SProcProxyBuilder.camelCaseToUnderscore(clazz.getSimpleName());
        } else {
            this.typeName = typeName;
        }

        this.clazz = clazz;

        Integer typeId = sqlType;
        if (typeId == null || typeId == -1) {
            typeId = SQL_MAPPING.get(clazz);
        }

        if (typeId == null) {
            typeId = java.sql.Types.OTHER;
        }

        type = typeId;
        sqlPos = sqlPosition;
        javaPos = javaPosition;
        this.sensitive = sensitive;

    }

    public Object mapParam(final Object value, final Connection connection) {
        if (value == null) {
            return null;
        }

        Object result = value;
        switch (type) {

            case Types.ARRAY :

                String innerTypeName = null;

                if (typeName != null && typeName.endsWith("[]")) {
                    innerTypeName = typeName.substring(0, typeName.length() - 2);
                }

                result = PgArray.ARRAY((Collection) value);

                if (innerTypeName != null) {
                    result = ((PgArray) result).asJdbcArray(innerTypeName);
                }

                break;

            case Types.OTHER :

                if (clazz.isEnum()) {

                    // HACK: should be implemented in PgTypeHelper
                    PGobject pgobj = new PGobject();
                    pgobj.setType(typeName);
                    try {
                        pgobj.setValue(((Enum) value).name());
                    } catch (final SQLException ex) {
                        if (sensitive) {
                            LOG.error("Failed to set PG object value (sensitive parameter, stacktrace hidden)");
                        } else {
                            LOG.error("Failed to set PG object value", ex);
                        }
                    }

                    result = pgobj;

                } else {
                    try {
                        result = PgTypeHelper.asPGobject(value, typeName, connection);
                    } catch (final SQLException ex) {
                        if (sensitive) {
                            LOG.error("Failed to serialize PG object (sensitive parameter, stacktrace hidden)");
                        } else {
                            LOG.error("Failed to serialize PG object", ex);
                        }
                    }
                }

                break;

        }

        return result;
    }

    public int getJavaPos() {
        return javaPos;
    }

    public boolean isSensitive() {
        return sensitive;
    }

    public int getSqlPos() {
        return sqlPos;
    }

    public int getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

}
