package de.zalando.sprocwrapper.proxy;

import java.lang.reflect.ParameterizedType;

import java.sql.Connection;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import org.springframework.jdbc.core.RowMapper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;
import de.zalando.sprocwrapper.proxy.executors.Executor;
import de.zalando.sprocwrapper.proxy.executors.MultiRowSimpleTypeExecutor;
import de.zalando.sprocwrapper.proxy.executors.MultiRowTypeMapperExecutor;
import de.zalando.sprocwrapper.proxy.executors.SingleRowCustomMapperExecutor;
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

    // whether the result type is a collection (List)
    private boolean collectionResult = false;
    private boolean runOnAllShards;
    private boolean autoPartition;

    private Executor executor = null;

    private VirtualShardKeyStrategy shardStrategy;
    private List<ShardKeyParameter> shardKeyParameters = null;
    private RowMapper<?> resultMapper;

    private int[] types = null;

    private static final Executor MULTI_ROW_SIMPLE_TYPE_EXECUTOR = new MultiRowSimpleTypeExecutor();
    private static final Executor MULTI_ROW_TYPE_MAPPER_EXECUTOR = new MultiRowTypeMapperExecutor();
    private static final Executor SINGLE_ROW_SIMPLE_TYPE_EXECUTOR = new SingleRowSimpleTypeExecutor();
    private static final Executor SINGLE_ROW_TYPE_MAPPER_EXECUTOR = new SingleRowTypeMapperExecutor();

    public StoredProcedure(final String name, final java.lang.reflect.Type genericType,
            final VirtualShardKeyStrategy sStrategy, final boolean runOnAllShards, final RowMapper<?> resultMapper) {
        this.name = name;
        this.runOnAllShards = runOnAllShards;
        this.resultMapper = resultMapper;

        shardStrategy = sStrategy;

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) genericType;

            if (java.util.List.class.isAssignableFrom((Class) pType.getRawType())
                    && pType.getActualTypeArguments().length > 0) {
                returnType = (Class) pType.getActualTypeArguments()[0];
                if (SingleRowSimpleTypeExecutor.SIMPLE_TYPES.containsKey(returnType)) {
                    executor = MULTI_ROW_SIMPLE_TYPE_EXECUTOR;
                } else {
                    executor = MULTI_ROW_TYPE_MAPPER_EXECUTOR;
                }

                collectionResult = true;
            } else {
                executor = SINGLE_ROW_TYPE_MAPPER_EXECUTOR;
                returnType = (Class) pType.getRawType();
            }

        } else {
            returnType = (Class) genericType;

            if (SingleRowSimpleTypeExecutor.SIMPLE_TYPES.containsKey(returnType)) {
                executor = SINGLE_ROW_SIMPLE_TYPE_EXECUTOR;
            } else {
                if (resultMapper != null) {
                    executor = new SingleRowCustomMapperExecutor(resultMapper);
                } else {
                    executor = SINGLE_ROW_TYPE_MAPPER_EXECUTOR;
                }
            }
        }
    }

    public void addParam(final StoredProcedureParameter p) {
        params.add(p);
    }

    public void setVirtualShardKeyStrategy(final VirtualShardKeyStrategy s) {
        shardStrategy = s;
    }

    public void addShardKeyParameter(final int jp, final Class clazz) {
        if (shardKeyParameters == null) {
            shardKeyParameters = new ArrayList<ShardKeyParameter>(1);
        }

        if (List.class.isAssignableFrom(clazz)) {
            autoPartition = true;
        }

        shardKeyParameters.add(new ShardKeyParameter(jp));
    }

    public String getName() {
        return name;
    }

    public Object[] getParams(final Object[] origParams, final Connection connection) {
        Object[] ps = new Object[params.size()];

        int i = 0;
        for (StoredProcedureParameter p : params) {
            try {
                ps[i] = p.mapParam(origParams[p.getJavaPos()], connection);
            } catch (Exception e) {
                final String errorMessage = "Could not map input parameter for stored procedure " + name + " of type "
                        + p.getType() + " at position " + p.getJavaPos() + ": "
                        + (p.isSensitive() ? "<SENSITIVE>" : origParams[p.getJavaPos()]);
                LOG.error(errorMessage, e);
                throw new IllegalArgumentException(errorMessage, e);
            }

            i++;
        }

        return ps;
    }

    public int[] getTypes() {
        if (types == null) {
            types = new int[params.size()];

            int i = 0;
            for (StoredProcedureParameter p : params) {
                types[i++] = p.getType();
            }
        }

        return types;
    }

    public int getShardId(final Object[] objs) {
        if (shardKeyParameters == null) {
            return shardStrategy.getShardId(null);
        }

        Object[] keys = new Object[shardKeyParameters.size()];
        int i = 0;
        for (ShardKeyParameter p : shardKeyParameters) {
            keys[i] = objs[p.javaPos];
            i++;
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
            } else if (params.get(i).isSensitive()) {
                sb.append("<SENSITIVE>");
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

    /**
     * split arguments by shard.
     *
     * @param   dataSourceProvider
     * @param   args                the original argument list
     *
     * @return  map of virtual shard ID to argument list (TreeMap with ordered keys: sorted by shard ID)
     */
    private Map<Integer, Object[]> partitionArguments(final DataSourceProvider dataSourceProvider,
            final Object[] args) {

        // use TreeMap here to maintain ordering by shard ID
        final Map<Integer, Object[]> argumentsByShardId = Maps.newTreeMap();

        // we need to partition by datasource instead of virtual shard ID (different virtual shard IDs are mapped to
        // the same datasource e.g. by VirtualShardMd5Strategy)
        final Map<DataSource, Integer> shardIdByDataSource = Maps.newHashMap();

        // TODO: currently only implemented for a single shardKey argument!
        final List<Object> originalArgument = (List) args[0];
        List<Object> partitionedArgument = null;
        Object[] partitionedArguments = null;
        int shardId;
        Integer existingShardId;
        DataSource dataSource;
        for (Object key : originalArgument) {
            shardId = getShardId(new Object[] {key});
            dataSource = dataSourceProvider.getDataSource(shardId);
            existingShardId = shardIdByDataSource.get(dataSource);
            if (existingShardId != null) {

                // we already saw the same datasource => use the virtual shard ID of the first argument with the same
                // datasource
                shardId = existingShardId;
            } else {
                shardIdByDataSource.put(dataSource, shardId);
            }

            partitionedArguments = argumentsByShardId.get(shardId);
            if (partitionedArguments == null) {

                partitionedArgument = Lists.newArrayList();
                partitionedArguments = new Object[] {partitionedArgument};
                argumentsByShardId.put(shardId, partitionedArguments);
            } else {
                partitionedArgument = (List<Object>) partitionedArguments[0];
            }

            partitionedArgument.add(key);

        }

        return argumentsByShardId;
    }

    public Object execute(final DataSourceProvider dp, final Object[] args) {

        List<Integer> shardIds = null;
        Map<Integer, Object[]> partitionedArguments = null;
        if (runOnAllShards) {
            shardIds = dp.getDistinctShardIds();
        } else {
            if (autoPartition) {
                partitionedArguments = partitionArguments(dp, args);
                shardIds = Lists.newArrayList(partitionedArguments.keySet());
            } else {
                shardIds = Lists.newArrayList(getShardId(args));
            }
        }

        if (partitionedArguments == null) {
            partitionedArguments = Maps.newHashMap();
            for (int shardId : shardIds) {
                partitionedArguments.put(shardId, args);
            }
        }

        final DataSource firstDs = dp.getDataSource(shardIds.get(0));
        Connection connection = null;
        try {
            connection = firstDs.getConnection();

        } catch (SQLException e) {
            throw new IllegalStateException("Failed to acquire connection for virtual shard " + shardIds.get(0)
                    + " for " + name, e);
        }

        final List<Object[]> paramValues = Lists.newArrayList();
        try {
            for (int shardId : shardIds) {
                paramValues.add(getParams(partitionedArguments.get(shardId), connection));
                if (LOG.isDebugEnabled()) {
                    LOG.debug(getDebugLog(paramValues.get(paramValues.size() - 1)));
                }
            }

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Throwable t) {
                    LOG.warn("Could not release connection", t);
                }
            }
        }

        if (shardIds.size() == 1 && !autoPartition) {

            // most common case: only one shard and no argument partitioning
            return executor.executeSProc(firstDs, getQuery(), paramValues.get(0), getTypes(), returnType);
        } else {
            List<?> results = Lists.newArrayList();
            DataSource shardDs;
            Object sprocResult = null;
            int i = 0;
            for (int shardId : shardIds) {
                shardDs = dp.getDataSource(shardId);
                sprocResult = executor.executeSProc(shardDs, getQuery(), paramValues.get(i), getTypes(), returnType);
                if (collectionResult) {
                    results.addAll((Collection) sprocResult);

                }

                i++;
            }

            if (collectionResult) {
                return results;
            } else {

                // return last result
                return sprocResult;
            }

        }

    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        sb.append('(');

        boolean f = true;
        for (StoredProcedureParameter p : params) {
            if (!f) {
                sb.append(',');
            }

            f = false;
            sb.append(p.getType());
            if (!"".equals(p.getTypeName())) {
                sb.append("=>").append(p.getTypeName());
            }
        }

        sb.append(')');
        return sb.toString();
    }
}
