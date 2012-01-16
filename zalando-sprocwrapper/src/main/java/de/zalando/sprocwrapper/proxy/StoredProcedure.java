package de.zalando.sprocwrapper.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import org.postgresql.util.PGobject;

import com.typemapper.annotations.DatabaseField;

import com.typemapper.serialization.postgres.PgArray;
import com.typemapper.serialization.postgres.PgRow;

import de.zalando.dbutils.ParseUtils;

import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;
import de.zalando.sprocwrapper.proxy.executors.Executor;
import de.zalando.sprocwrapper.proxy.executors.MultiRowTypeMapperExecutor;
import de.zalando.sprocwrapper.proxy.executors.SingleRowSimpleTypeExecutor;
import de.zalando.sprocwrapper.proxy.executors.SingleRowTypeMapperExecutor;
import de.zalando.sprocwrapper.sharding.VirtualShardKeyStrategy;

/**
 * @author  jmussler
 */
class StoredProcedure {

    private static final Logger LOG = Logger.getLogger(StoredProcedure.class);

    private final List<StoredProcedureParameter> params = new ArrayList<StoredProcedureParameter>();

    private String name;
    private String query = null;
    private Class returnType = null;

    private Executor executor = null;

    private VirtualShardKeyStrategy shardStrategy = new VirtualShardKeyStrategy();
    private List<ShardKeyParameter> shardKeyParameters = null;

    private static final Executor MULTI_ROW_TYPE_MAPPER_EXECUTOR = new MultiRowTypeMapperExecutor();
    private static final Executor SINGLE_ROW_SIMPLE_TYPE_EXECUTOR = new SingleRowSimpleTypeExecutor();
    private static final Executor SINGLE_ROW_TYPE_MAPPER_EXECUTOR = new SingleRowTypeMapperExecutor();

    public StoredProcedure(final String name, final java.lang.reflect.Type genericType) {
        this.name = name;

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) genericType;

            if (java.util.List.class.isAssignableFrom((Class) pType.getRawType())
                    && pType.getActualTypeArguments().length > 0) {
                returnType = (Class) pType.getActualTypeArguments()[0];
                executor = MULTI_ROW_TYPE_MAPPER_EXECUTOR;
            } else {
                executor = SINGLE_ROW_TYPE_MAPPER_EXECUTOR;
                returnType = (Class) pType.getRawType();
            }

        } else {
            returnType = (Class) genericType;

            if (SingleRowSimpleTypeExecutor.SIMPLE_TYPES.containsKey(returnType)) {
                executor = SINGLE_ROW_SIMPLE_TYPE_EXECUTOR;
            } else {
                executor = SINGLE_ROW_TYPE_MAPPER_EXECUTOR;
            }
        }
    }

    public void addParam(final StoredProcedureParameter p) {
        params.add(p);
    }

    public void setVirtualShardKeyStrategy(final VirtualShardKeyStrategy s) {
        shardStrategy = s;
    }

    public void addShardKeyParamter(final int jp, final int kp) {
        if (shardKeyParameters == null) {
            shardKeyParameters = new ArrayList<ShardKeyParameter>(1);
        }

        shardKeyParameters.add(new ShardKeyParameter(jp, kp));
    }

    public String getName() {
        return name;
    }

    private static boolean isPgSerializable(final Object o) {
        if (o == null) {
            return true;
        }

        Class clazz = o.getClass();
        if (o instanceof PGobject || o instanceof java.sql.Array || o instanceof CharSequence || o instanceof Character
                || clazz == Character.TYPE) {
            return true;
        } else if (clazz.isArray()) {
            return true;
        } else if (o instanceof Collection) {
            return true;
        } else if (clazz == Boolean.TYPE || clazz == Boolean.class) {
            return true;
        } else if (clazz.isPrimitive() || o instanceof Number) {
            return true;
        }

        return false;
    }

    private static PGobject serializePGObject(final Object obj, final String typeHint) {
        List<Object> attributes = new ArrayList<Object>();

        Field[] fields = obj.getClass().getDeclaredFields();

        // Hacky: sort fields alphabetically as class fields' order is undefined
        // http://stackoverflow.com/questions/1097807/java-reflection-is-the-order-of-class-fields-and-methods-standardized
        Arrays.sort(fields, new Comparator<Field>() {

                @Override
                public int compare(final Field a, final Field b) {
                    return a.getName().compareTo(b.getName());
                }

            });
        for (Field field : fields) {
            try {
                field.setAccessible(true);

                DatabaseField annotation = field.getAnnotation(DatabaseField.class);
                if (annotation != null) {
                    Object attr = field.get(obj);
                    if (attr instanceof Map) {

                        // TODO: move HSTORE serialization into PGSerializer in typemapper
                        Map<String, String> map = (Map<String, String>) attr;
                        int j = 0;
                        StringBuilder hstore = new StringBuilder();
                        for (Entry<String, String> entry : map.entrySet()) {
                            if (j > 0) {
                                hstore.append(",");
                            }

                            hstore.append("\"");
                            hstore.append(ParseUtils.quotePgArrayElement(entry.getKey()));
                            hstore.append("\"=>\"");
                            hstore.append(ParseUtils.quotePgArrayElement(entry.getValue()));
                            hstore.append("\"");
                            j++;
                        }

                        attr = hstore.toString();
                    } else if (attr instanceof List) {
                        List<PGobject> pgobjects = new ArrayList<PGobject>();
                        List list = (List) attr;
                        for (Object o : list) {
                            pgobjects.add(serializePGObject(o, null));
                        }

                        attr = pgobjects;
                    } else if (!isPgSerializable(attr)) {
                        attr = serializePGObject(attr, null);
                    }

                    attributes.add(attr);
                }

            } catch (Exception e) {
                continue;
            }
        }

        if (attributes.isEmpty()) {
            throw new IllegalArgumentException("Cannot serialize object of class " + obj.getClass().getName()
                    + " to PGObject: No attributes");
        }

        PGobject result = null;
        String typeName = typeHint;
        if (StringUtils.isEmpty(typeName)) {
            typeName = SProcProxyBuilder.camelCaseToUnderscore(obj.getClass().getSimpleName());
        }

        try {
            result = PgRow.ROW(attributes).asPGobject(typeName);
        } catch (final SQLException ex) {
            LOG.error("Failed to serialize PG object", ex);
        }

        return result;
    }

    private static Object mapParam(final Object param, final StoredProcedureParameter p) {
        if (param == null) {
            return null;
        }

        switch (p.type) {
// @TODO use method parameter to detect input array/list

            case Types.ARRAY :

                List<PGobject> pgobjects = new ArrayList<PGobject>();
                List list = (List) param;
                for (Object obj : list) {
                    pgobjects.add(serializePGObject(obj, null));
                }

                String innerTypeName = null;

                if (p.typeName != null && p.typeName.endsWith("[]")) {
                    innerTypeName = p.typeName.substring(0, p.typeName.length() - 2);
                }

                if (innerTypeName == null && !pgobjects.isEmpty()) {
                    innerTypeName = pgobjects.get(0).getType();
                }

                if (innerTypeName == null) {
                    throw new IllegalArgumentException(
                        "Could not determine PG array type: Empty list parameter without @SProcParam(type = \"..[]\")");
                }

                Array arr = null;
                arr = PgArray.ARRAY(pgobjects).asJdbcArray(innerTypeName);

                return arr;

            case Types.OTHER :
                return serializePGObject(param, p.typeName);
        }

        return param;
    }

    public Object[] getParams(final Object[] origParams) {
        Object[] ps = new Object[params.size()];

        for (StoredProcedureParameter p : params) {
            ps[p.sqlPos] = mapParam(origParams[p.javaPos], p);
        }

        return ps;
    }

    private int[] types = null;

    public int[] getTypes() {
        if (types == null) {
            types = new int[params.size()];

            int i = 0;
            for (StoredProcedureParameter p : params) {
                types[i++] = p.type;
            }
        }

        return types;
    }

    public int getShardId(final Object[] objs) {
        if (shardKeyParameters == null) {
            return shardStrategy.getShardId(null);
        }

        Object[] keys = new Object[shardKeyParameters.size()];
        for (ShardKeyParameter p : shardKeyParameters) {
            keys[p.keyPos] = objs[p.javaPos];
        }

        return shardStrategy.getShardId(keys);
    }

    public String getSqlParameterList() {
        String s = "";
        boolean first = true;
        for (int i = 1; i <= params.size(); ++i) {
            if (!first) {
                s += ",";
            }

            first = false;

            s += "?";
        }

        return s;
    }

    public void setQuery(final String sql) {
        query = sql;
    }

    public String getQuery() {
        if (query == null) {
            query = "SELECT * FROM " + name + " ( " + getSqlParameterList() + " )";
        }

        return query;
    }

    public Object execute(final DataSourceProvider dp, final Object[] args) {
        return executor.executeSProc(dp.getDataSource(getShardId(args)), getQuery(), getParams(args), getTypes(),
                returnType);
    }

    @Override
    public String toString() {
        String s = "";
        s += name;
        s += "(";

        StoredProcedureParameter[] ps = new StoredProcedureParameter[params.size()];
        for (StoredProcedureParameter p : params) {
            ps[p.sqlPos] = p;
        }

        boolean f = true;
        for (StoredProcedureParameter p : ps) {
            if (!f) {
                s += ",";
            }

            f = false;
            s += p.type;
            if (!"".equals(p.typeName)) {
                s += "=>" + p.typeName;
            }
        }

        s += ")";
        return s;
    }
}
