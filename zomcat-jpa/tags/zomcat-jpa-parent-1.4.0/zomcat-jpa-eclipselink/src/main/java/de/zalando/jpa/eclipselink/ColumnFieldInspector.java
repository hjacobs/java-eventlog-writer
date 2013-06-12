package de.zalando.jpa.eclipselink;

import java.lang.reflect.Field;

import javax.persistence.Column;

/**
 * Inspect fields for @{link Column} annotation.
 *
 * @author  jbellmann
 */
public class ColumnFieldInspector extends EntityFieldInspector<Column> {

    public ColumnFieldInspector(final Field field) {
        super(Column.class, field);
    }

    @Override
    protected String resolveNameValue(final Column annotation) {
        return annotation.name();
    }

}
