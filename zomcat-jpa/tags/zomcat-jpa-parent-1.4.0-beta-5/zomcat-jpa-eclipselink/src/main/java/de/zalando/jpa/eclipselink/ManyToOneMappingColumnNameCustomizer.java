package de.zalando.jpa.eclipselink;

import java.util.Vector;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.mappings.Association;
import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.sessions.Session;

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
        for (final DatabaseField foreignKeyField : databaseMapping.getForeignKeyFields()) {
            String prefix = NameUtils.iconizeTableName(tableName) + "_";
            if (!foreignKeyField.getName().startsWith(prefix)) {
                String newFieldName = prefix + foreignKeyField.getName();
                foreignKeyField.setName(newFieldName);
                session.getSessionLog().log(SessionLog.FINE, "ForeignKeyField-Name was set to {0}",
                    new Object[] {newFieldName}, false);
            }

// foreignKeyField.setName(getIconizedTableName(tableName) + "_" + foreignKeyField.getName());
        }

        Vector<Association> associations = databaseMapping.getSourceToTargetKeyFieldAssociations();
        for (Association ass : associations) {
            session.getSessionLog().log(SessionLog.FINE,
                "---------------------" + tableName + "      " + ass.getKey().toString()
                    + "---------------------------", false);
        }

    }

}
