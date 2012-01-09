package de.zalando.storedprocedurewrapper;

import de.zalando.storedprocedurewrapper.proxy.SprocProxyBuilder;

/**
 * @author  jmussler
 */
abstract class AbstractSprocService<I extends SprocProxyServiceInterface, P extends DataSourceProvider> {

    protected P ds;

    protected I service;

    protected Class<I> interfaceClass;

    protected AbstractSprocService(final P ps, final Class<I> clazz) {
        interfaceClass = clazz;
        ds = ps;
        service = SprocProxyBuilder.build(ds, interfaceClass);
    }

}
