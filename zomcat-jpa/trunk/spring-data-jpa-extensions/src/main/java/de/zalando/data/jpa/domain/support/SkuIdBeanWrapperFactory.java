package de.zalando.data.jpa.domain.support;

import java.lang.reflect.Field;

import org.springframework.core.annotation.AnnotationUtils;

import org.springframework.data.util.ReflectionUtils;

import de.zalando.data.annotation.SkuId;

/**
 * @author  jbellmann
 */
public class SkuIdBeanWrapperFactory {

    SkuIdBeanWrapper getBeanWrapperFor(final Object source) {

        if (source == null) {
            return null;
        }

        AnnotationSkuIdMetadata metadata = AnnotationSkuIdMetadata.getMetadata(source.getClass());

        if (metadata.isIdable()) {
            return new ReflectionKeyableBeanWrapper(source);
        }

        return null;
    }

    static class ReflectionKeyableBeanWrapper implements SkuIdBeanWrapper {

        private final Object source;
        private final AnnotationSkuIdMetadata metadata;

        ReflectionKeyableBeanWrapper(final Object source) {
            this.source = source;
            this.metadata = AnnotationSkuIdMetadata.getMetadata(source.getClass());
        }

        @Override
        public String getSequenceName() {
            SkuId keyAnnotation = this.metadata.getSkuIdField().getAnnotation(SkuId.class);
            return (String) AnnotationUtils.getValue(keyAnnotation, "value");
        }

        @Override
        public boolean negateSku() {
            SkuId keyAnnotation = this.metadata.getSkuIdField().getAnnotation(SkuId.class);
            return (boolean) AnnotationUtils.getValue(keyAnnotation, "negate");
        }

        public void setSkuId(final Number key) {
            setField(metadata.getSkuIdField(), key);
        }

        private void setField(final Field keyField, final Number key) {
            if (keyField != null) {
                Object idValue = null;
                if (Integer.class.equals(keyField.getType())) {
                    idValue = key.intValue();
                } else {
                    idValue = key.longValue();
                }

                ReflectionUtils.setField(keyField, this.source, idValue);
            }
        }
    }

}
