package de.zalando.jpa.eclipselink.customizer.databasemapping.support;

import java.lang.reflect.Field;

import javax.persistence.JoinColumn;

/**
 * Inspector for JoinColumnFields.
 *
 * @author  jbellmann
 */
public class JoinColumnFieldInspector extends EntityFieldInspector<JoinColumn> {

    public JoinColumnFieldInspector(final Field field) {
        super(JoinColumn.class, field);
    }

    @Override
    protected String resolveNameValue(final JoinColumn annotation) {
        return annotation.name();
    }

}
