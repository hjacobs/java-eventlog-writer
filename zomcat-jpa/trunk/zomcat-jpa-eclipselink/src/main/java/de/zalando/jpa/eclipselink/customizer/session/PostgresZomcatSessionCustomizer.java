package de.zalando.jpa.eclipselink.customizer.session;

import de.zalando.jpa.eclipselink.customizer.databasemapping.CustomizerRegistry;
import de.zalando.jpa.eclipselink.customizer.databasemapping.DirectToFieldMappingEnumTypeConverterCustomizer;

/**
 * @author  jbellmann
 */
public class PostgresZomcatSessionCustomizer extends DefaultZomcatSessionCustomizer {

    public PostgresZomcatSessionCustomizer() {
        super();

        // converter for enums
        CustomizerRegistry.get().registerConverterCustomizer(new DirectToFieldMappingEnumTypeConverterCustomizer());
    }

}
