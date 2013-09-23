package de.zalando.jpa.eclipselink.customizer.classdescriptor;

import javax.persistence.Table;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Session;

import de.zalando.jpa.eclipselink.LogSupport;
import de.zalando.jpa.eclipselink.customizer.NameUtils;

/**
 * Customize the name of the Table if not explicit set by the author.
 *
 * @author  maciej
 * @author  jbellmann
 */
public class TableNameClassDescriptorCustomizer implements ClassDescriptorCustomizer {

    @Override
    public void customize(final ClassDescriptor clazzDescriptor, final Session session) {

        final Class<?> clazz = clazzDescriptor.getJavaClass();

        // we want to respect Annotations, right?
        if (clazz.isAnnotationPresent(Table.class)) {

            final Table tableAnnotation = clazz.getAnnotation(Table.class);

            // is never null, defaults to empty string
            final String nameValue = tableAnnotation.name();

            if (nameValue.trim().isEmpty()) {

                iconizeTableName(clazzDescriptor, session);
            }

        } else {

            iconizeTableName(clazzDescriptor, session);
        }
    }

    /**
     * @param  clazzDescriptor
     */
    protected void iconizeTableName(final ClassDescriptor clazzDescriptor, final Session session) {
        final String alias = clazzDescriptor.getAlias();

        final String tableName = NameUtils.camelCaseToUnderscore(alias);

        LogSupport.logFine(session, "Set Tablename to {0}", tableName);
        clazzDescriptor.setTableName(tableName);
    }

}
