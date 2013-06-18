package de.zalando.zomcat.cxf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;

import de.zalando.zomcat.proxy.ProxyUtils;

@Provider
@Consumes({ MediaType.APPLICATION_JSON, "text/json" })
@Produces({ MediaType.APPLICATION_JSON, "text/json" })
public class GsonJsonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

    private Gson gson;
    private final Map<Class<?>, JsonSerializer<?>> serializers = Maps.newHashMap();

    @PostConstruct
    public void initialize() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        for (final Entry<Class<?>, JsonSerializer<?>> entry : serializers.entrySet()) {
            gsonBuilder = gsonBuilder.registerTypeAdapter(entry.getKey(), entry.getValue());
        }

        gsonBuilder.addSerializationExclusionStrategy(new XmlTransientExclusionStrategy());

        gson = gsonBuilder.create();
    }

    @Override
    public boolean isWriteable(final Class<?> type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType) {
        return true;
    }

    @Override
    public long getSize(final Object t, final Class<?> type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(final Object t, final Class<?> type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType, final MultivaluedMap<String, Object> httpHeaders,
            final OutputStream entityStream) throws IOException, WebApplicationException {
        Object obj = t;

        if (ProxyUtils.isProxy(obj)) {
            obj = ProxyUtils.getProxiedObject(obj);
        }

        final OutputStreamWriter writer = new OutputStreamWriter(entityStream);
        if (obj instanceof String) {
            writer.write(obj.toString());
        } else {
            gson.toJson(obj, type, writer);
        }

        writer.flush();
    }

    @Override
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType) {
        return true;
    }

    @Override
    public Object readFrom(final Class<Object> type, final Type genericType, final Annotation[] annotations,
            final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream)
        throws IOException, WebApplicationException {
        throw new RuntimeException("Operation is not supported by this provider. Implement this if needed.");
    }

    public void setSerializers(final Map<String, String> serializers) throws ClassNotFoundException,
        InstantiationException, IllegalAccessException {
        for (final Entry<String, String> entry : serializers.entrySet()) {
            final Class<?> type = Class.forName(entry.getKey());
            final Class<?> serializerType = Class.forName(entry.getValue());
            final JsonSerializer<?> serializer = (JsonSerializer<?>) serializerType.newInstance();
            this.serializers.put(type, serializer);
        }
    }

    private static class XmlTransientExclusionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(final FieldAttributes f) {
            boolean skip = f.getAnnotation(XmlTransient.class) != null;
            if (!skip) {
                String name = "get" + StringUtils.capitalize(f.getName());
                try {
                    skip = f.getDeclaringClass().getMethod(name).getAnnotation(XmlTransient.class) != null;
                } catch (NoSuchMethodException e) {
                    skip = false;
                }
            }

            return skip;
        }

        @Override
        public boolean shouldSkipClass(final Class<?> clazz) {
            return false;
        }
    }

}
