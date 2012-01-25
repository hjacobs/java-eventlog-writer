package de.zalando.sprocwrapper.proxy;

import java.lang.reflect.ParameterizedType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import org.postgresql.util.PGobject;

import com.typemapper.postgres.PgArray;
import com.typemapper.postgres.PgTypeHelper;

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

    private static final int TRUNCATE_DEBUG_PARAMS_MAX_LENGTH = 1024;
    private static final String TRUNCATE_DEBUG_PARAMS_ELLIPSIS = " ...";

    private static final Logger LOG = Logger.getLogger(StoredProcedure.class);

    private final List<StoredProcedureParameter> params = new ArrayList<StoredProcedureParameter>();

    private String name;
    private String query = null;
    private Class returnType = null;

    private Executor executor = null;

    private VirtualShardKeyStrategy shardStrategy;
    private List<ShardKeyParameter> shardKeyParameters = null;

    private static final Executor MULTI_ROW_TYPE_MAPPER_EXECUTOR = new MultiRowTypeMapperExecutor();
    private static final Executor SINGLE_ROW_SIMPLE_TYPE_EXECUTOR = new SingleRowSimpleTypeExecutor();
    private static final Executor SINGLE_ROW_TYPE_MAPPER_EXECUTOR = new SingleRowTypeMapperExecutor();

    public StoredProcedure(final String name, final java.lang.reflect.Type genericType,
            final VirtualShardKeyStrategy sStrategy) {
        this.name = name;

        shardStrategy = sStrategy;

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

    private static Object mapParam(final Object param, final StoredProcedureParameter p, final Connection connection) {
        if (param == null) {
            return null;
        }

        Object result = param;
        switch (p.type) {

            case Types.ARRAY :

                String innerTypeName = null;

                if (p.typeName != null && p.typeName.endsWith("[]")) {
                    innerTypeName = p.typeName.substring(0, p.typeName.length() - 2);
                }

                result = PgArray.ARRAY((Collection) param);

                if (innerTypeName != null) {
                    result = ((PgArray) result).asJdbcArray(innerTypeName);
                }

                break;

            case Types.OTHER :

                if (p.clazz.isEnum()) {

                    // HACK: should be implemented in PgTypeHelper
                    PGobject pgobj = new PGobject();
                    pgobj.setType(p.typeName);
                    try {
                        pgobj.setValue(((Enum) param).name());
                    } catch (final SQLException ex) {
                        LOG.error("Failed to set PG object value", ex);
                    }

                    result = pgobj;

                } else {
                    try {
                        result = PgTypeHelper.asPGobject(param, p.typeName, connection);
                    } catch (final SQLException ex) {
                        LOG.error("Failed to serialize PG object", ex);
                    }
                }

                break;

        }

        return result;
    }

    public Object[] getParams(final Object[] origParams, final Connection connection) {
        Object[] ps = new Object[params.size()];

        for (StoredProcedureParameter p : params) {
            try {
                ps[p.sqlPos] = mapParam(origParams[p.javaPos], p, connection);
            } catch (Exception e) {
                final String errorMessage = "Could not map input parameter for stored procedure " + name + " of type "
                        + p.type + " at position " + p.javaPos + ": " + origParams[p.javaPos];
                LOG.error(errorMessage, e);
                throw new IllegalArgumentException(errorMessage, e);
            }
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

    /**
     * build execution string like create_or_update_multiple_objects({"(a,b)","(c,d)" }).
     *
     * @param   args
     *
     * @return
     */
    private String getDebugLog(final Object[] args) {
        final StringBuilder sb = new StringBuilder(name);
        sb.append('(');

        int i = 0;
        for (Object param : args) {
            if (i > 0) {
                sb.append(',');
            }

            if (param == null) {
                sb.append("NULL");
            } else {
                sb.append(param);
            }

            i++;
            if (sb.length() > TRUNCATE_DEBUG_PARAMS_MAX_LENGTH) {
                break;
            }
        }

        if (sb.length() > TRUNCATE_DEBUG_PARAMS_MAX_LENGTH) {

            // Truncate params for debug output
            return sb.substring(0, TRUNCATE_DEBUG_PARAMS_MAX_LENGTH) + TRUNCATE_DEBUG_PARAMS_ELLIPSIS + ")";
        } else {
            sb.append(')');
            return sb.toString();
        }
    }

    public Object execute(final DataSourceProvider dp, final Object[] args) {

        final int shardId = getShardId(args);
        final DataSource ds = dp.getDataSource(shardId);
        Connection connection = null;
        try {
            connection = ds.getConnection();

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to acquire connection for virtual shard " + shardId + " for "
                    + name, e);
        }

        final Object[] params = getParams(args, connection);
        if (LOG.isDebugEnabled()) {
            LOG.debug(getDebugLog(params));
        }

        return executor.executeSProc(ds, getQuery(), params, getTypes(), returnType);
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
