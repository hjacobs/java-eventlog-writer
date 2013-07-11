package de.zalando.typemapper.core.fieldMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalObjectMapperRegistry {
    private static final Map<Class<?>, ObjectMapper> register = new ConcurrentHashMap<Class<?>, ObjectMapper>();

    public static void register(final Class<?> clazz, final ObjectMapper valueTransformer) {
        register.put(clazz, valueTransformer);
    }

    public static ObjectMapper getValueTransformerForClass(final Class<?> clazz) {
        return register.get(clazz);
    }
}
