package de.zalando.jpa.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionCustomizer;

import com.google.common.base.CaseFormat;

public class ZalandoSessionCustomizer implements SessionCustomizer {

    @Override
    public void customize(final Session session) throws Exception {
        final Map<Class, ClassDescriptor> descs = session.getDescriptors();
        final Collection<ClassDescriptor> descriptors = descs.values();

        // This code assumes single table per descriptor!
        for (final ClassDescriptor desc : descriptors) {
            final String fullClassName = desc.getJavaClassName();
            final String className = Helper.getShortClassName(fullClassName);
            final String tableName = desc.getTableName();
            final Vector<String> tableNames = new Vector<String>();
            tableNames.add(tableName);
            updateMappings(desc, tableName);
        }
    }

    private void updateMappings(final ClassDescriptor desc, final String tableName) {
        for (final DatabaseMapping mapping : desc.getMappings()) {
            if (mapping.isDirectToFieldMapping()) {
                final DirectToFieldMapping directMapping = (DirectToFieldMapping) mapping;
                customizeColumnName(tableName, directMapping);
            }

            if (mapping.isOneToManyMapping()) {
                final OneToManyMapping oneToMany = (OneToManyMapping) mapping;
                customizeColumnName(tableName, oneToMany);
            }

            if (mapping.isManyToOneMapping()) {
                final ManyToOneMapping manyToOne = (ManyToOneMapping) mapping;
                customizeColumnName(tableName, manyToOne);
            }
        }
    }

    private void customizeColumnName(final String tableName, final ManyToOneMapping manyToOne) {
        for (final DatabaseField foreignKeyField : manyToOne.getForeignKeyFields()) {
            foreignKeyField.setName(getIconizedTableName(tableName) + "_" + foreignKeyField.getName());
        }
    }

    private void customizeColumnName(final String tableName, final OneToManyMapping oneToMany) { }

    private void customizeColumnName(final String tableName, final DirectToFieldMapping directMapping) {
        directMapping.getField().setName(getIconizedTableName(tableName) + "_"
                + getCamelCaseToUnderscore(directMapping.getAttributeName()));
    }

    private String getCamelCaseToUnderscore(final String name) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name);
    }

    private String getIconizedTableName(final String tableName) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(tableName.charAt(0));
        for (int i = 1; i < tableName.length(); ++i) {
            final char charAt = tableName.charAt(i);
            if (Character.isUpperCase(charAt)) {
                stringBuilder.append(charAt);
            }
        }

        return stringBuilder.toString().toLowerCase();
    }
}
