package de.zalando.zomcat.proxy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * CGLib implementation of internal proxy creation and handling for WS purposes.
 */
public class ProxyUtils {

    private static final String CALLBACK_FIELD_NAME = "CGLIB$CALLBACK_0";

    @SuppressWarnings("unchecked")
    public static <ProxyType> ProxyType createProxy(final ProxyType obj, final ProxyMethodCallback<?> callback) {
        if (isProxy(obj)) {
            return obj;
        }

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(obj.getClass());
        enhancer.setInterfaces(obj.getClass().getInterfaces());
        enhancer.setCallback(new CGLibCallback(obj, callback));
        enhancer.setUseFactory(false);
        return (ProxyType) enhancer.create();
    }

    public static boolean isProxy(final Object obj) {
        return Enhancer.isEnhanced(obj.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <ProxyType> ProxyType getProxiedObject(final ProxyType obj) {
        if (!isProxy(obj)) {
            throw new IllegalArgumentException(String.format("Object: %s is probably not a proxy object.",
                    obj.getClass().getName()));
        }

        Field callbackField;
        try {
            callbackField = obj.getClass().getDeclaredField(CALLBACK_FIELD_NAME);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(e);
        }

        ReflectionUtils.makeAccessible(callbackField);
        return ((CGLibCallback<ProxyType>) ReflectionUtils.getField(callbackField, obj)).getTargetObject();
    }

    public static class CGLibCallback<ProxyType> implements MethodInterceptor {

        private ProxyType obj;
        private ProxyMethodCallback<ProxyType> proxyMethodCallback;

        public CGLibCallback(final ProxyType obj, final ProxyMethodCallback<ProxyType> proxyMethodCallback) {
            this.obj = obj;
            this.proxyMethodCallback = proxyMethodCallback;
        }

        @Override
        public Object intercept(final Object obj, final Method method, final Object[] args, final MethodProxy proxy)
            throws Throwable {
            return this.proxyMethodCallback.intercept(this.obj, method, args);
        }

        public ProxyType getTargetObject() {
            return this.obj;
        }

    }

}
