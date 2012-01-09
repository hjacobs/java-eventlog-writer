package de.zalando.storedprocedurewrapper.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import de.zalando.storedprocedurewrapper.DataSourceProvider;
import de.zalando.storedprocedurewrapper.VirtualShardKeyStrategy;
import de.zalando.storedprocedurewrapper.annotations.ShardKey;
import de.zalando.storedprocedurewrapper.annotations.SprocCall;
import de.zalando.storedprocedurewrapper.annotations.SprocParam;

/**
 * @author  jmussler
 */
public class SprocProxyBuilder {

    private static final Logger LOG = Logger.getLogger(SprocProxyBuilder.class);

    private static String getSqlNameForMethod(final String s) {
        return s;
    }

    public static <T> T build(final DataSourceProvider d, final Class<T> c) {
        Method[] ms = c.getMethods();

        SprocProxy proxy = new SprocProxy(d);

        for (Method m : ms) {
            SprocCall scA = m.getAnnotation(SprocCall.class);

            if (scA == null) {
                continue;
            }

            String name = scA.name();
            if ("".equals(name)) {
                name = getSqlNameForMethod(m.getName());
            }

            StoredProcedure p = new StoredProcedure(name, m.getGenericReturnType());

            if (!"".equals(scA.sql())) {
                p.setQuery(scA.sql());
            }

            try {
                p.setVirtualShardKeyStrategy((VirtualShardKeyStrategy) scA.shardStrategy().newInstance());
            } catch (InstantiationException ex) {
                throw new IllegalArgumentException("Illegal VirtualShardKeyStrategy " + scA.shardStrategy().getName(),
                    ex);
            } catch (IllegalAccessException ex) {
                throw new IllegalArgumentException("Illegal VirtualShardKeyStrategy " + scA.shardStrategy().getName(),
                    ex);
            }

            int pos = 0;
            for (Annotation[] as : m.getParameterAnnotations()) {

                for (Annotation a : as) {
                    if (a instanceof ShardKey) {
                        int kp = ((ShardKey) a).pos();
                        if (kp == -1) {
                            kp = pos;
                        }

                        p.addShardKeyParamter(pos, kp);
                    }

                    if (a instanceof SprocParam) {
                        SprocParam sParam = (SprocParam) a;

                        int sqlPos = pos;
                        if (sParam.sqlPosPosition() != -1) {
                            sqlPos = sParam.sqlPosPosition();
                        }

                        int javaPos = pos;
                        if (sParam.javaPosition() != -1) {
                            javaPos = sParam.javaPosition();
                        }

                        String type = sParam.type();
                        if ("".equals(type)) {
                            type = m.getParameterTypes()[pos].getName();
                        }

                        p.addParam(new StoredProcedureParameter(type, sqlPos, javaPos));
                    }
                }

                pos++;
            }

            LOG.debug("registering stored procedure: " + p);
            proxy.addStoredProcedure(m.getName(), p);
        }

        return (T) java.lang.reflect.Proxy.newProxyInstance(c.getClassLoader(), new Class[] {c}, proxy);
    }
}
