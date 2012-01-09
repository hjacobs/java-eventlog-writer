package de.zalando.sprocwrapper.proxy;

import java.lang.reflect.Method;

import java.util.HashMap;

import org.apache.log4j.Logger;

import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;

/**
 * @author  jmussler
 */
class SProcProxy implements java.lang.reflect.InvocationHandler {

    private final HashMap<String, StoredProcedure> sprocs = new HashMap<String, StoredProcedure>();
    private final DataSourceProvider dp;

    private static final Logger LOG = Logger.getLogger(SProcProxy.class);

    public boolean addStoredProcedure(final String methodName, final StoredProcedure p) {
        if (sprocs.containsKey(methodName)) {
            return false;
        }

        sprocs.put(methodName, p);
        return true;
    }

    public SProcProxy(final DataSourceProvider d) {
        dp = d;
    }

    @Override
    public Object invoke(final Object proxy, final Method m, final Object[] args) {
        LOG.debug("try to invoke sproc for " + m.getName());

        StoredProcedure p = sprocs.get(m.getName());

        if (p == null) {
            LOG.debug("no sproc found!");
            return null;
        }

        if (dp == null) {
            LOG.debug("no datasource set!");
            return null;
        }

        return p.execute(dp, args);
    }
}
