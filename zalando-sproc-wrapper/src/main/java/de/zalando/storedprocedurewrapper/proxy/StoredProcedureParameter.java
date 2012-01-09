package de.zalando.storedprocedurewrapper.proxy;

import java.util.HashMap;
import java.util.Map;

/**
 * @author  jmussler
 */
class StoredProcedureParameter {
    int type;
    int sqlPos;
    int javaPos;

    private static final Map<String, Integer> SQL_MAPPING = new HashMap<String, Integer>();

    static {
        SQL_MAPPING.put(int.class.getName(), java.sql.Types.INTEGER);
        SQL_MAPPING.put(Integer.class.getName(), java.sql.Types.INTEGER);
        SQL_MAPPING.put(long.class.getName(), java.sql.Types.BIGINT);
        SQL_MAPPING.put(Long.class.getName(), java.sql.Types.BIGINT);
        SQL_MAPPING.put(float.class.getName(), java.sql.Types.FLOAT);
        SQL_MAPPING.put(Float.class.getName(), java.sql.Types.FLOAT);
        SQL_MAPPING.put(double.class.getName(), java.sql.Types.DOUBLE);
        SQL_MAPPING.put(Double.class.getName(), java.sql.Types.DOUBLE);
        SQL_MAPPING.put(String.class.getName(), java.sql.Types.VARCHAR);
        SQL_MAPPING.put(java.sql.Date.class.getName(), java.sql.Types.TIMESTAMP);
        SQL_MAPPING.put(java.sql.Date.class.getName(), java.sql.Types.TIMESTAMP);
    }

    public StoredProcedureParameter(final String t, final int s, final int j) {
        Integer typeId = SQL_MAPPING.get(t);
        if (typeId == null) {

            // @TODO consider exception
            typeId = java.sql.Types.VARCHAR;
        }

        type = typeId;
        sqlPos = s;
        javaPos = j;
    }
}
