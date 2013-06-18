package de.zalando.jpa.eclipselink.customizer.databasemapping;

import java.util.Vector;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.Association;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.sessions.Session;

import de.zalando.jpa.eclipselink.customizer.databasemapping.support.EntityFieldInspector;
import de.zalando.jpa.eclipselink.customizer.databasemapping.support.JoinColumnFieldInspector;
import de.zalando.jpa.eclipselink.customizer.databasemapping.support.NameUtils;

/**
 * @author  jbellmann
 */
public class ManyToOneMappingColumnNameCustomizer extends AbstractColumnNameCustomizer<ManyToOneMapping> {

    public ManyToOneMappingColumnNameCustomizer() {
        super(ManyToOneMapping.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void customizeColumnName(final String tableName, final ManyToOneMapping databaseMapping,
            final Session session) {

        logDatabaseMapping(databaseMapping, session);

        EntityFieldInspector<?> entityFieldInspector = new JoinColumnFieldInspector(super.getFieldInspector(
                    databaseMapping).getField());
        for (final DatabaseField foreignKeyField : databaseMapping.getForeignKeyFields()) {
            String prefix = NameUtils.iconizeTableName(tableName) + "_";
            if (!foreignKeyField.getName().startsWith(prefix) && !(entityFieldInspector.isNameValueSet())) {

                String newFieldName = NameUtils.buildFieldName(tableName, databaseMapping.getAttributeName()) + "_id";

                foreignKeyField.setName(newFieldName);
                logFine(session, "ForeignKeyField-Name was set to {0}", foreignKeyField.getName());

            } else if (!foreignKeyField.getName().startsWith(prefix) && entityFieldInspector.isNameValueSet()) {

                String newFieldName = NameUtils.buildFieldName(tableName, foreignKeyField.getName());
                foreignKeyField.setName(newFieldName);
                logFine(session, "ForeignKeyField-Name was set to {0}", foreignKeyField.getName());
            }
        }

        Vector<Association> associations = databaseMapping.getSourceToTargetKeyFieldAssociations();
        for (Association ass : associations) {
            logFine(session, "--" + tableName + "  --  " + ass.getKey().toString() + "----");
        }

    }

}
