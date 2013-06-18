package de.zalando.zomcat.proxy;

import java.lang.reflect.Method;

public interface ProxyMethodCallback<ProxyType> {

    Object intercept(final ProxyType obj, final Method method, final Object[] args) throws Throwable;

}
