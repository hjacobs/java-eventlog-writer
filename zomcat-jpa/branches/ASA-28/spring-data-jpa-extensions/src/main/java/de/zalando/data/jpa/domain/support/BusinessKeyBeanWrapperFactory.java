package de.zalando.data.jpa.domain.support;

import java.lang.reflect.Field;

import org.springframework.core.annotation.AnnotationUtils;

import org.springframework.data.util.ReflectionUtils;

import de.zalando.data.annotation.BusinessKey;

/**
 * @author  jbellmann
 */
public class BusinessKeyBeanWrapperFactory {

    BusinessKeyBeanWrapper getBeanWrapperFor(final Object source) {

        if (source == null) {
            return null;
        }

        if (source instanceof BusinessKeyAware) {
            return new BusinessKeyAwareInterfaceBeanWrapper((BusinessKeyAware) source);
        }

        AnnotationBusinessKeyMetadata metadata = AnnotationBusinessKeyMetadata.getMetadata(source.getClass());

        if (metadata.isKeyable()) {
            return new ReflectionKeyableBeanWrapper(source);
        }

        return null;
    }

    static class BusinessKeyAwareInterfaceBeanWrapper implements BusinessKeyBeanWrapper {

        private final BusinessKeyAware keyable;

        BusinessKeyAwareInterfaceBeanWrapper(final BusinessKeyAware keyable) {
            this.keyable = keyable;
        }

        public String getBusinessKeySelector() {
            return this.keyable.getBusinessKeySelector();
        }

        public void setBusinessKey(final String key) {
            this.keyable.setBusinessKey(key);
        }

    }

    static class ReflectionKeyableBeanWrapper implements BusinessKeyBeanWrapper {

        private final Object source;
        private final AnnotationBusinessKeyMetadata metadata;

        ReflectionKeyableBeanWrapper(final Object source) {
            this.source = source;
            this.metadata = AnnotationBusinessKeyMetadata.getMetadata(source.getClass());
        }

        public String getBusinessKeySelector() {
            BusinessKey keyAnnotation = this.metadata.getKeyField().getAnnotation(BusinessKey.class);
            return (String) AnnotationUtils.getValue(keyAnnotation, "value");
        }

        public void setBusinessKey(final String key) {
            setField(metadata.getKeyField(), key);
        }

        private void setField(final Field keyField, final String key) {
            if (keyField != null) {
                ReflectionUtils.setField(keyField, this.source, key);
            }
        }

    }

}
