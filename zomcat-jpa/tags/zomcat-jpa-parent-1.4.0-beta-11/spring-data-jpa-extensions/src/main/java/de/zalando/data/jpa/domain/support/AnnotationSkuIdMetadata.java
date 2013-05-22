package de.zalando.data.jpa.domain.support;

import java.lang.reflect.Field;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.data.util.ReflectionUtils;
import org.springframework.data.util.ReflectionUtils.AnnotationFieldFilter;

import org.springframework.util.Assert;

import de.zalando.data.annotation.SkuId;

/**
 * Metadata for an entity class. Holds information to apply the skuid or not. Internally cached.
 *
 * @author  jbellmann
 */
final class AnnotationSkuIdMetadata {

    private static final AnnotationFieldFilter SKU_ID_FILTER = new AnnotationFieldFilter(SkuId.class);

    private static final Map<Class<?>, AnnotationSkuIdMetadata> METADATACACHE =
        new ConcurrentHashMap<Class<?>, AnnotationSkuIdMetadata>();

    private final Field skuIdField;

    private AnnotationSkuIdMetadata(final Class<?> type) {

        Assert.notNull(type, "Given Type must not be null");
        skuIdField = ReflectionUtils.findField(type, SKU_ID_FILTER);
    }

    public static AnnotationSkuIdMetadata getMetadata(final Class<?> type) {

        if (METADATACACHE.containsKey(type)) {
            return METADATACACHE.get(type);
        }

        AnnotationSkuIdMetadata metadata = new AnnotationSkuIdMetadata(type);
        METADATACACHE.put(type, metadata);
        return metadata;
    }

    public boolean isIdable() {
        if (skuIdField == null) {
            return false;
        }

        return true;
    }

    public Field getSkuIdField() {
        return skuIdField;
    }

}
