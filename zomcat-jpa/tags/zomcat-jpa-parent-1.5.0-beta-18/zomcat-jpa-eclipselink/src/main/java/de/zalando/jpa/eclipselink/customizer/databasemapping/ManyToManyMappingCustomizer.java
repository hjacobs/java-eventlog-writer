package de.zalando.jpa.eclipselink.customizer.databasemapping;

import java.lang.reflect.Field;

import java.util.Vector;

import javax.persistence.JoinTable;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.mappings.ManyToManyMapping;
import org.eclipse.persistence.mappings.RelationTableMechanism;
import org.eclipse.persistence.sessions.Session;

import de.zalando.jpa.eclipselink.customizer.NameUtils;

/**
 * ASA-54.
 *
 * @author  jbellmann
 */
public class ManyToManyMappingCustomizer extends AbstractColumnNameCustomizer<ManyToManyMapping> {

    public ManyToManyMappingCustomizer() {
        super(ManyToManyMapping.class);
    }

    @Override
    public void customizeColumnName(final String tableName, final ManyToManyMapping databaseMapping,
            final Session session) {

        // wie heist dass attribute in der beinhaltenden Klasse
        final String attributeName = databaseMapping.getAttributeName(); // externalSystems

        // Typ der Collection zum Attribute
        Class<?> referenceClazz = databaseMapping.getReferenceClass();

        final RelationalDescriptor descriptor = (RelationalDescriptor) databaseMapping.getDescriptor();

        // die beinhaltende Klasse
        final Class<?> javaClazz = descriptor.getJavaClass(); // FunctionalGroup

        // wenn eine JoinTable via annotation deklariert mit wurde, und 'name' nicht leer ist machen wir nichts
        try {
            Field attributeField = javaClazz.getDeclaredField(attributeName);
            if (attributeField.isAnnotationPresent(JoinTable.class)) {
                JoinTable joinTableAnnotation = attributeField.getAnnotation(JoinTable.class);
                String name = joinTableAnnotation.name();
                if (!name.trim().isEmpty()) {

                    // skip processing here
                    return;
                }
            }
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }

        final RelationTableMechanism mechanism = databaseMapping.getRelationTableMechanism();

        // relationTable defines name, qualifiedname
        final DatabaseTable databaseTable = mechanism.getRelationTable();

        final String lhs = NameUtils.camelCaseToUnderscore(javaClazz.getSimpleName());

        final String rhs = NameUtils.camelCaseToUnderscore(referenceClazz.getSimpleName());
        final String newTableName = new StringBuilder().append(lhs).append("_").append(rhs).toString();

        databaseTable.setName(newTableName);

        // wenn deklariert in @JoinTable(name)
        final String databaseTableName = databaseTable.getName();

        // sourceKeyFields beinhaltet databasefield des SourceIdDatabasefield zum beispiel : deployment_set.ds_id
        Vector<DatabaseField> sourcekeyFields = mechanism.getSourceKeyFields();

        for (DatabaseField field : sourcekeyFields) {
            String fieldName = field.getName();
            field.setName(fieldName.toLowerCase());
            System.out.println(field.getName());
        }

        // sourceRelationKeyField zum beispiel : deploymentset_projects.DeploymentSet_ID
        Vector<DatabaseField> sourceRelationKeyFields = mechanism.getSourceRelationKeyFields();

        for (DatabaseField field : sourceRelationKeyFields) {
            String fieldName = field.getName();
            String iconized = NameUtils.iconizeTableName(lhs);
            String newFieldName = new StringBuilder().append(iconized).append("_").append("id").toString();
            field.setName(newFieldName);
// field.setName(fieldName.toLowerCase());
            System.out.println(field.getName());
        }

        // targetKeyField zum beispiel : project.p_id
        Vector<DatabaseField> targetKeyFields = mechanism.getTargetKeyFields();

        for (DatabaseField field : targetKeyFields) {
            String fieldName = field.getName();
            field.setName(fieldName.toLowerCase());
            System.out.println(field.getName());
        }

        // targetRelationKeyField zum Beispiel deploymentset_projects.projects_ID
        Vector<DatabaseField> targetRelationKeyField = mechanism.getTargetRelationKeyFields();

        for (DatabaseField field : targetRelationKeyField) {
            String fieldName = field.getName();
            String iconized = NameUtils.iconizeTableName(rhs);
            String newFieldName = new StringBuilder().append(iconized).append("_").append("id").toString();
            field.setName(newFieldName);
// field.setName(fieldName.toLowerCase());
            System.out.println(field.getName());
        }

    }

}
