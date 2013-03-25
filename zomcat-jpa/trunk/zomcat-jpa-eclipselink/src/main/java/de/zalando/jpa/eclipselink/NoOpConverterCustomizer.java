package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author  jbellmann
 */
public class NoOpConverterCustomizer implements ConverterCustomizer<DatabaseMapping> {

    private static final Logger LOG = LoggerFactory.getLogger(NoOpConverterCustomizer.class);

    @Override
    public void customizeConverter(final DatabaseMapping databaseMapping, final Session session) {
        LOG.debug("Do nothing on databaseMapping for attribute {} ", databaseMapping.getAttributeName());
    }

    @Override
    public Class<DatabaseMapping> supportedDatabaseMapping() {
        return DatabaseMapping.class;
    }

}
