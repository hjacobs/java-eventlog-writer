package de.zalando.sprocwrapper.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author  jmussler
 */
class StoredProcedureParameter {
    String typeName;
    int type;
    int sqlPos;
    int javaPos;
    Class clazz;

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
        SQL_MAPPING.put(List.class, java.sql.Types.ARRAY);
    }

    public StoredProcedureParameter(final Class clazz, final String typeName, final int sqlPosition,
            final int javaPosition) {
        this.typeName = typeName;
        this.clazz = clazz;

        Integer typeId = SQL_MAPPING.get(clazz);
        if (typeId == null) {
            typeId = java.sql.Types.OTHER;
        }

        type = typeId;
        sqlPos = sqlPosition;
        javaPos = javaPosition;
    }
}
