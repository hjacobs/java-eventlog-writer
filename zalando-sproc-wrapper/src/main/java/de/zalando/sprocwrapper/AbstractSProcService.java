package de.zalando.sprocwrapper;

import de.zalando.sprocwrapper.dsprovider.DataSourceProvider;
import de.zalando.sprocwrapper.proxy.SProcProxyBuilder;

/**
 * @author  jmussler
 */
public abstract class AbstractSProcService<I extends SProcService, P extends DataSourceProvider> {

    protected P ds;

    protected I service;

    protected Class<I> interfaceClass;

    protected AbstractSProcService(final P ps, final Class<I> clazz) {
        interfaceClass = clazz;
        ds = ps;
        service = SProcProxyBuilder.build(ds, interfaceClass);
    }

}
