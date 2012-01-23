package de.zalando.sprocwrapper.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;

import org.apache.log4j.Logger;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;
import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;
import de.zalando.sprocwrapper.sharding.ShardKey;
import de.zalando.sprocwrapper.sharding.VirtualShardKeyStrategy;

/**
 * @author  jmussler
 */
public class SProcProxyBuilder {

    private static final VirtualShardKeyStrategy VIRTUAL_SHARD_KEY_STRATEGY_DEFAULT = new VirtualShardKeyStrategy();

    private static final Logger LOG = Logger.getLogger(SProcProxyBuilder.class);

    public static String camelCaseToUnderscore(final String camelCase) {
        String[] camelCaseParts = StringUtils.splitByCharacterTypeCamelCase(camelCase);
        for (int i = 0; i < camelCaseParts.length; i++) {
            camelCaseParts[i] = camelCaseParts[i].toLowerCase();
        }

        return StringUtils.join(camelCaseParts, "_");
    }

    private static String getSqlNameForMethod(final String methodName) {
        return camelCaseToUnderscore(methodName);
    }

    public static <T> T build(final DataSourceProvider d, final Class<T> c) {
        Method[] methods = c.getMethods();

        SProcProxy proxy = new SProcProxy(d);

        SProcService serviceAnnotation = c.getAnnotation(SProcService.class);
        VirtualShardKeyStrategy keyStrategy = VIRTUAL_SHARD_KEY_STRATEGY_DEFAULT;
        if (serviceAnnotation != null) {
            try {
                keyStrategy = (VirtualShardKeyStrategy) serviceAnnotation.shardStrategy().newInstance();
            } catch (InstantiationException ex) {
                LOG.fatal("ShardKey strategy for service can not be instantiated", ex);
                return null;
            } catch (IllegalAccessException ex) {
                LOG.fatal("ShardKey strategy for service can not be instantiated", ex);
                return null;
            }
        }

        for (final Method method : methods) {
            SProcCall scA = method.getAnnotation(SProcCall.class);

            if (scA == null) {
                continue;
            }

            String name = scA.name();
            if ("".equals(name)) {
                name = getSqlNameForMethod(method.getName());
            }

            VirtualShardKeyStrategy sprocStrategy = keyStrategy;
            if (scA.shardStrategy() != Void.class) {
                try {
                    sprocStrategy = (VirtualShardKeyStrategy) scA.shardStrategy().newInstance();
                } catch (InstantiationException ex) {
                    LOG.fatal("Shard strategy for sproc can not be instantiated", ex);
                    return null;
                } catch (IllegalAccessException ex) {
                    LOG.fatal("Shard strategy for sproc can not be instantiated", ex);
                    return null;
                }
            }

            final StoredProcedure p = new StoredProcedure(name, method.getGenericReturnType(), sprocStrategy);

            if (!"".equals(scA.sql())) {
                p.setQuery(scA.sql());
            }

            int pos = 0;
            for (final Annotation[] as : method.getParameterAnnotations()) {

                for (final Annotation a : as) {
                    if (a instanceof ShardKey) {
                        int kp = ((ShardKey) a).pos();
                        if (kp == -1) {
                            kp = pos;
                        }

                        p.addShardKeyParamter(pos, kp);
                    }

                    if (a instanceof SProcParam) {
                        SProcParam sParam = (SProcParam) a;

                        int sqlPos = pos;
                        if (sParam.sqlPosition() != -1) {
                            sqlPos = sParam.sqlPosition();
                        }

                        int javaPos = pos;
                        if (sParam.javaPosition() != -1) {
                            javaPos = sParam.javaPosition();
                        }

                        String dbTypeName = sParam.type();
                        Class clazz = method.getParameterTypes()[pos];

                        p.addParam(new StoredProcedureParameter(clazz, dbTypeName, sqlPos, javaPos));
                    }
                }

                pos++;
            }

            LOG.debug("registering " + p);
            proxy.addStoredProcedure(method, p);
        }

        return (T) java.lang.reflect.Proxy.newProxyInstance(c.getClassLoader(), new Class[] {c}, proxy);
    }
}
