package de.zalando.jpa.eclipselink;

/**
 * @author  jbellmann
 */
public class ZomcatSessionCustomizer extends AbstractZomcatSessionCustomizer {

    private final ClassDescriptorCustomizer clazzDescriptorCustomizer;

    public ZomcatSessionCustomizer() {
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
