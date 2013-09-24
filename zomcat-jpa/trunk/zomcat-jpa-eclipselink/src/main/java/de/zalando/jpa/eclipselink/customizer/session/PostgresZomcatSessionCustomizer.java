package de.zalando.jpa.eclipselink.customizer.session;

import java.util.List;

import de.zalando.jpa.eclipselink.customizer.classdescriptor.ClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.ConverterCustomizer;
import de.zalando.jpa.eclipselink.customizer.databasemapping.DirectToFieldMappingEnumTypeConverterCustomizer;

/**
 * @author      jbellmann
 * @deprecated  will be deleted next time
 */
@Deprecated
public class PostgresZomcatSessionCustomizer extends AbstractZomcatSessionCustomizer {

    private final ClassDescriptorCustomizer clazzDescriptorCustomizer;

    public PostgresZomcatSessionCustomizer() {
        super();

        final List<ConverterCustomizer> cc = defaultConverterCustomizer();
        cc.add(new DirectToFieldMappingEnumTypeConverterCustomizer());

        clazzDescriptorCustomizer = builder().with(newClassDescriptorCustomizer(defaultColumnNameCustomizers(), cc))
                                             .build();
    }

    @Override
    public ClassDescriptorCustomizer getClassDescriptorCustomizer() {
        return clazzDescriptorCustomizer;
    }

}
