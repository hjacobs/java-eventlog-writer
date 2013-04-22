package de.zalando.jpa.eclipselink;

import org.postgresql.util.PGobject;

/**
 * The {@link ExtZomcatSessionCustomizer} registers an {@link DirectToFieldMappingEnumTypeConverterCustomizer} to handle
 * Enums with {@link PGobject}.
 *
 * @author  jbellmann
 */
public class ExtZomcatSessionCustomizer extends DefaultZomcatSessionCustomizer {

    public ExtZomcatSessionCustomizer() {
        super();

        // converter
        getClassDescriptorCustomizer().registerConverterCustomizer(
            new DirectToFieldMappingEnumTypeConverterCustomizer());
    }

}
