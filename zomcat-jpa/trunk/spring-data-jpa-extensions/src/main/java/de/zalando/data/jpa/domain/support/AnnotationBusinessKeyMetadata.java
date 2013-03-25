package de.zalando.data.jpa.domain.support;

import java.lang.reflect.Field;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.util.ReflectionUtils;
import org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter;

import org.springframework.util.Assert;

import de.zalando.data.annotation.BusinessKey;

/**
 * @author  jbellmann
 */
class AnnotationBusinessKeyMetadata {

    private static final AnnotationFieldFilter BUSINESSKEY_FILTER = new AnnotationFieldFilter(BusinessKey.class);

    private static final Map<Class<?>, AnnotationBusinessKeyMetadata> METADATACACHE =
        new ConcurrentHashMap<Class<?>, AnnotationBusinessKeyMetadata>();

    private final Field businessKeyField;

    private AnnotationBusinessKeyMetadata(final Class<?> type) {

        Assert.notNull(type, "Given Type must not be null");
        businessKeyField = ReflectionUtils.findField(type, BUSINESSKEY_FILTER);
    }

    public static AnnotationBusinessKeyMetadata getMetadata(final Class<?> type) {

        if (METADATACACHE.containsKey(type)) {
            return METADATACACHE.get(type);
        }

        AnnotationBusinessKeyMetadata metadata = new AnnotationBusinessKeyMetadata(type);
        METADATACACHE.put(type, metadata);
        return metadata;
    }

    public boolean isKeyable() {
        if (businessKeyField == null) {
            return false;
        }

        return true;
    }

    public Field getKeyField() {
        return businessKeyField;
    }

}
