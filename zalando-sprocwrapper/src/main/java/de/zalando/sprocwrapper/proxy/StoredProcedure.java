package de.zalando.sprocwrapper.proxy;

import java.beans.PropertyDescriptor;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

import java.sql.Array;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import org.postgresql.util.PGobject;

import org.springframework.beans.BeanUtils;

import com.typemapper.annotations.DatabaseField;

import com.typemapper.serialization.postgres.PgArray;
import com.typemapper.serialization.postgres.PgRow;

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

    private static PGobject serializePGObject(final Object obj, final String typeHint) {
        List<Object> attributes = new ArrayList<Object>();

        for (PropertyDescriptor descr : BeanUtils.getPropertyDescriptors(obj.getClass())) {
            try {
                Field field = obj.getClass().getField(descr.getName());
                DatabaseField annotation = field.getAnnotation(DatabaseField.class);
                if (annotation != null) {
                    attributes.add(field.get(obj));
                }

            } catch (Exception e) {
                continue;
            }

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

    private static Object mapParam(final Object param, final int sqlType, final String typeName) {
        if (param == null) {
            return null;
        }

        switch (sqlType) {

            case Types.ARRAY :

                List<PGobject> pgobjects = new ArrayList<PGobject>();
                List list = (List) param;
                for (Object obj : list) {
                    pgobjects.add(serializePGObject(obj, null));
                }

                String innerTypeName = null;

                if (typeName != null && typeName.endsWith("[]")) {
                    innerTypeName = typeName.substring(0, typeName.length() - 2);
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
                return serializePGObject(param, typeName);
        }

        return param;
    }

    public Object[] getParams(final Object[] origParams) {
        Object[] ps = new Object[params.size()];

        for (StoredProcedureParameter p : params) {
            ps[p.sqlPos] = mapParam(origParams[p.javaPos], p.type, p.typeName);
        }

        return ps;
    }

    public int[] getTypes() {
        int[] types = new int[params.size()];

        int i = 0;
        for (StoredProcedureParameter p : params) {
            types[i++] = p.type;
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
        }

        s += ")";
        return s;
    }
}
