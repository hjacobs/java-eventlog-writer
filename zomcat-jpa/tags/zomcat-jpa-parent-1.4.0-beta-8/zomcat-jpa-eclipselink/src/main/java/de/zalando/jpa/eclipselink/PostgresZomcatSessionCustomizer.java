package de.zalando.jpa.eclipselink;

/**
 * @author  jbellmann
 */
public class PostgresZomcatSessionCustomizer extends AbstractZomcatSessionCustomizer {

    private final ClassDescriptorCustomizer clazzDescriptorCustomizer;

    public PostgresZomcatSessionCustomizer() {
        clazzDescriptorCustomizer = new DefaultClassDescriptorCustomizer();

        // columnNames
        clazzDescriptorCustomizer.registerColumnNameCustomizer(new DirectToFieldMappingColumnNameCustomizer());
        clazzDescriptorCustomizer.registerColumnNameCustomizer(new ManyToOneMappingColumnNameCustomizer());
        clazzDescriptorCustomizer.registerColumnNameCustomizer(new OneToManyMappingColumnNameCustomizer());

        // converter
        clazzDescriptorCustomizer.registerConverterCustomizer(new DirectToFieldMappingEnumTypeConverterCustomizer());
    }

    @Override
    ClassDescriptorCustomizer getClassDescriptorCustomizer() {

        return this.clazzDescriptorCustomizer;
    }

}
