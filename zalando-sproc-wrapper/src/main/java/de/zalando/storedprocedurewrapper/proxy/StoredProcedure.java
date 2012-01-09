package de.zalando.storedprocedurewrapper.proxy;

import java.lang.reflect.ParameterizedType;

import java.util.ArrayList;

import de.zalando.storedprocedurewrapper.DataSourceProvider;
import de.zalando.storedprocedurewrapper.VirtualShardKeyStrategy;
import de.zalando.storedprocedurewrapper.proxy.executestrategies.GenericSingleColumnSimpleType;
import de.zalando.storedprocedurewrapper.proxy.executestrategies.RowMapperStrategy;
import de.zalando.storedprocedurewrapper.proxy.executestrategies.RowMapperStrategySingleElement;

/**
 * @author  jmussler
 */
class StoredProcedure {

    private final ArrayList<StoredProcedureParameter> params = new ArrayList<StoredProcedureParameter>();

    private String name;
    private String query = null;
    private Class returnType = null;

    private static final ExecuteStrategy typeMapperExecutor = new RowMapperStrategy();

    private ExecuteStrategy executor = null;

    private VirtualShardKeyStrategy shardStrategy = new VirtualShardKeyStrategy();
    private ArrayList<ShardKeyParameter> shardKeyParameters = null;

    private static final ExecuteStrategy SINGLE_ROW_TYPEMAPPER = new RowMapperStrategySingleElement();
    private static final ExecuteStrategy TYPEMAPPER = new RowMapperStrategy();
    private static final ExecuteStrategy GENERIC_SIMPLE_TYPE_SINGLE_COLUMN = new GenericSingleColumnSimpleType();

    public StoredProcedure(final String n, final java.lang.reflect.Type genericType) {

        if (genericType instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) genericType;

            if (java.util.List.class.isAssignableFrom((Class) pType.getRawType())
                    && pType.getActualTypeArguments().length > 0) {
                returnType = (Class) pType.getActualTypeArguments()[0];
                executor = TYPEMAPPER;
            } else {
                executor = SINGLE_ROW_TYPEMAPPER;
                returnType = (Class) pType.getRawType();
            }

        } else {
            returnType = (Class) genericType;

            Class clazz = (Class) genericType;
            if (clazz == String.class || clazz == Integer.class || clazz == int.class) {
                executor = GENERIC_SIMPLE_TYPE_SINGLE_COLUMN;
            } else {
                executor = SINGLE_ROW_TYPEMAPPER;
            }
        }

        name = n;
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

    public Object[] getParams(final Object[] origParams) {
        Object[] ps = new Object[params.size()];

        for (StoredProcedureParameter p : params) {
            ps[p.sqlPos] = origParams[p.javaPos];
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
        return shardStrategy.getShardId(objs);
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
        return executor.executeSproc(dp.getDataSource(getShardId(args)), getQuery(), getParams(args), getTypes(),
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
