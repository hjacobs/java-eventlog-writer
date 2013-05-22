package de.zalando.jpa.eclipselink;

/**
 * @author  jbellmann
 */
public class PostgresZomcatSessionCustomizer extends DefaultZomcatSessionCustomizer {

    public PostgresZomcatSessionCustomizer() {
        super();

        // converter for enums
        getClassDescriptorCustomizer().registerConverterCustomizer(
            new DirectToFieldMappingEnumTypeConverterCustomizer());
    }

}
