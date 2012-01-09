/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zalando.storedprocedurewrapper.proxy;

import java.lang.reflect.Method;

import java.util.HashMap;

import org.apache.log4j.Logger;

import de.zalando.storedprocedurewrapper.DataSourceProvider;

/**
 * @author  jmussler
 */
class SprocProxy implements java.lang.reflect.InvocationHandler {

    private final HashMap<String, StoredProcedure> sprocs = new HashMap<String, StoredProcedure>();
    private final DataSourceProvider dp;

    private static final Logger LOG = Logger.getLogger(SprocProxy.class);

    public boolean addStoredProcedure(final String methodName, final StoredProcedure p) {
        if (sprocs.containsKey(methodName)) {
            return false;
        }

        sprocs.put(methodName, p);
        return true;
    }

    public SprocProxy(final DataSourceProvider d) {
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
