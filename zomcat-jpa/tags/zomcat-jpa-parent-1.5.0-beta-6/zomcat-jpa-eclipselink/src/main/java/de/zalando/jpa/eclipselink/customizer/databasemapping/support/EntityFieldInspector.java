package de.zalando.jpa.eclipselink.customizer.databasemapping.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * @author  jbellmann
 */
public abstract class EntityFieldInspector<T extends Annotation> {

    protected Field field;
    protected T annotation = null;
    protected Class<T> annotationClass;
    protected String nameValue = null;

    protected EntityFieldInspector(final Class<T> annotationClass, final Field field) {
        this.annotationClass = annotationClass;
        this.field = field;
        inspect();
    }

    protected void inspect() {
        if (field != null) {
            annotation = field.getAnnotation(annotationClass);
            if (annotation != null) {
                nameValue = resolveNameValue(annotation);
            }
        }
    }

    protected abstract String resolveNameValue(T annotation);

    public boolean isNameValueSet() {
        return !"".equals(getNameValue());
    }

    public String getNameValue() {
        return nameValue != null ? nameValue : "";
    }

    public Class<?> getFieldType() {
        return field != null ? field.getType() : null;
    }

    public Field getField() {
        return field;
    }
}
