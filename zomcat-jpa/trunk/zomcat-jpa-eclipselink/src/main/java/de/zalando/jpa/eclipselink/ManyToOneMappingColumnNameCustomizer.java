package de.zalando.jpa.eclipselink;

import java.util.Vector;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.mappings.Association;
import org.eclipse.persistence.mappings.ManyToOneMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author  jbellmann
 */
public class ManyToOneMappingColumnNameCustomizer extends AbstractColumnNameCustomizer<ManyToOneMapping> {

    private static final Logger LOG = LoggerFactory.getLogger(ManyToOneMappingColumnNameCustomizer.class);

    public ManyToOneMappingColumnNameCustomizer() {
        super(ManyToOneMapping.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void customizeColumnName(final String tableName, final ManyToOneMapping databaseMapping) {
        for (final DatabaseField foreignKeyField : databaseMapping.getForeignKeyFields()) {
            String prefix = NameUtils.iconizeTableName(tableName) + "_";
            if (!foreignKeyField.getName().startsWith(prefix)) {
                String newFieldName = prefix + foreignKeyField.getName();
                foreignKeyField.setName(newFieldName);
                LOG.debug("ForeignKeyField-Name was set to {}", newFieldName);
            }

// foreignKeyField.setName(getIconizedTableName(tableName) + "_" + foreignKeyField.getName());
        }

        Vector<Association> associations = databaseMapping.getSourceToTargetKeyFieldAssociations();
        for (Association ass : associations) {
            LOG.debug("---------------------" + tableName + "      " + ass.getKey().toString()
                    + "---------------------------");
        }

    }

}
