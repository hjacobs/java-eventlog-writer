package de.zalando.jpa.eclipselink.customizer.databasemapping.support;

import org.eclipse.persistence.mappings.ManyToOneMapping;
import org.eclipse.persistence.sessions.Session;

import de.zalando.jpa.eclipselink.LogSupport;

public class ManyToOneMappingLogger extends LogSupport implements DatabaseMappingLogger<ManyToOneMapping> {

    @Override
    public void logDatabaseMapping(final ManyToOneMapping databaseMapping, final Session session) {

        logFine(session, "\tmapping.attributeName : {0}", databaseMapping.getAttributeName());
        logFine(session, "\tmapping.attributeClassification: {0}", databaseMapping.getAttributeClassification());
    }

    @Override
    public Class<ManyToOneMapping> getMappingType() {
        return ManyToOneMapping.class;
    }
}
