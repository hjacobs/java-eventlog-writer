package de.zalando.jpa.eclipselink;

import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.mappings.Association;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.mappings.OneToManyMapping;
import org.eclipse.persistence.sessions.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.CaseFormat;

@Deprecated
public class NamingStrategySessionCustomizer implements SessionCustomizer {
    private static Logger LOG = LoggerFactory.getLogger(NamingStrategySessionCustomizer.class);

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
            LOG.debug("---------------------" + desc.getAlias() + "      " + tableName + "---------------------------");
        }
    }

    private void updateMappings(final ClassDescriptor desc, final String tableName) {
        for (final DatabaseMapping mapping : desc.getMappings()) {
            if (mapping.isDirectToFieldMapping()) {
                final DirectToFieldMapping directMapping = (DirectToFieldMapping) mapping;
                customizeColumnName(tableName, directMapping);
                LOG.debug("---------------------" + tableName + "      " + directMapping.getFieldName()
                        + "---------------------------");
            }

            if (mapping.isOneToManyMapping()) {
                final OneToManyMapping oneToMany = (OneToManyMapping) mapping;
                customizeColumnName(tableName, oneToMany);
                LOG.debug("---------------------" + tableName + "      " + oneToMany.getSetMethodName()
                        + "---------------------------");

            }

            if (mapping.isManyToOneMapping()) {
                final ManyToOneMapping manyToOne = (ManyToOneMapping) mapping;
                customizeColumnName(tableName, manyToOne);

                Vector<Association> assi = manyToOne.getSourceToTargetKeyFieldAssociations();
                for (Association ass : assi) {
                    LOG.debug("---------------------" + tableName + "      " + ass.getKey().toString()
                            + "---------------------------");
                }
            }
        }
    }

    private void customizeColumnName(final String tableName, final ManyToOneMapping manyToOne) {
        for (final DatabaseField foreignKeyField : manyToOne.getForeignKeyFields()) {
            String prefix = getIconizedTableName(tableName) + "_";
            if (!foreignKeyField.getName().startsWith(prefix)) {
                foreignKeyField.setName(prefix + foreignKeyField.getName());
            }

// foreignKeyField.setName(getIconizedTableName(tableName) + "_" + foreignKeyField.getName());
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
            if (charAt == '_') {
                stringBuilder.append(tableName.charAt(i + 1));
            }
        }

        return stringBuilder.toString().toLowerCase();
    }
}
