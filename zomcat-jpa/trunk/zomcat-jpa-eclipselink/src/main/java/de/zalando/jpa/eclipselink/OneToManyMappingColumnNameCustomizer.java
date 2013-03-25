package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.mappings.OneToManyMapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author  jbellmann
 */
public class OneToManyMappingColumnNameCustomizer extends AbstractColumnNameCustomizer<OneToManyMapping> {

    private static final Logger LOG = LoggerFactory.getLogger(OneToManyMappingColumnNameCustomizer.class);

    public OneToManyMappingColumnNameCustomizer() {
        super(OneToManyMapping.class);
    }

    @Override
    public void customizeColumnName(final String tableName, final OneToManyMapping databaseMapping) {
        LOG.debug("Do nothing on 'customizeColumnName'-method for tableName {}", tableName);
    }

}
