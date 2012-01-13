package de.zalando.sprocwrapper.proxy;

import java.lang.reflect.Method;

import java.util.HashMap;

import org.apache.log4j.Logger;

import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;

/**
 * @author  jmussler
 */
class SProcProxy implements java.lang.reflect.InvocationHandler {

    private final HashMap<Method, StoredProcedure> sprocs = new HashMap<Method, StoredProcedure>();
    private final DataSourceProvider dp;

    private static final Logger LOG = Logger.getLogger(SProcProxy.class);

    public boolean addStoredProcedure(final Method method, final StoredProcedure p) {
        if (sprocs.containsKey(method)) {
            return false;
        }

        sprocs.put(method, p);
        return true;
    }

    public SProcProxy(final DataSourceProvider d) {
        dp = d;
    }

    @Override
    public Object invoke(final Object proxy, final Method m, final Object[] args) {
        LOG.debug("invoke stored procedure for method " + m);

        StoredProcedure p = sprocs.get(m);

        if (p == null) {
            LOG.debug("no sproc found!");
            return null;
        }

        if (dp == null) {
            LOG.debug("no datasource set!");
            return null;
        }

        LOG.debug("executing " + p);

        return p.execute(dp, args);
    }
}
