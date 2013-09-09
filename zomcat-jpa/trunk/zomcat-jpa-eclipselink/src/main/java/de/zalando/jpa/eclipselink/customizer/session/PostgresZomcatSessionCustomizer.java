package de.zalando.jpa.eclipselink.customizer.session;

import de.zalando.jpa.eclipselink.customizer.classdescriptor.ClassDescriptorCustomizer;

/**
 * @author  jbellmann
 */
@Deprecated
public class PostgresZomcatSessionCustomizer extends AbstractZomcatSessionCustomizer {

    private final ClassDescriptorCustomizer clazzDescriptorCustomizer;

    public PostgresZomcatSessionCustomizer() {
        super();
        clazzDescriptorCustomizer = null;
// final ClassDescriptorCustomizer defaults = defaultZalandoCustomizationBuilder().with(
// new DirectToFieldMappingEnumTypeConverterCustomizer()).build();
//
// clazzDescriptorCustomizer = newComposite().with(defaults).with(new ChangePolicyClassDescriptorCustomizer())
// .build();
    }

    @Override
    public ClassDescriptorCustomizer getClassDescriptorCustomizer() {
        return clazzDescriptorCustomizer;
    }

}
