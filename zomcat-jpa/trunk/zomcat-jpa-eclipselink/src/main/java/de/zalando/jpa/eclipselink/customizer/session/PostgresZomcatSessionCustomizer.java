package de.zalando.jpa.eclipselink.customizer.session;

import de.zalando.jpa.eclipselink.customizer.classdescriptor.ChangePolicyClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.ClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.DirectToFieldMappingEnumTypeConverterCustomizer;

/**
 * @author  jbellmann
 */
public class PostgresZomcatSessionCustomizer extends AbstractZomcatSessionCustomizer {

    private final ClassDescriptorCustomizer clazzDescriptorCustomizer;

    public PostgresZomcatSessionCustomizer() {
        super();

        final ClassDescriptorCustomizer defaults = newBuilderWithDefaults().with(
                new DirectToFieldMappingEnumTypeConverterCustomizer()).build();

        clazzDescriptorCustomizer = newComposite().with(defaults).with(new ChangePolicyClassDescriptorCustomizer())
                                                  .build();
    }

    @Override
    public ClassDescriptorCustomizer getClassDescriptorCustomizer() {
        return clazzDescriptorCustomizer;
    }

}
