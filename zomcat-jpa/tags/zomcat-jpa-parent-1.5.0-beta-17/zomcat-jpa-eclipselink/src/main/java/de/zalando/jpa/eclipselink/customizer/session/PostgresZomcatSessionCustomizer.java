package de.zalando.jpa.eclipselink.customizer.session;

import de.zalando.jpa.eclipselink.customizer.classdescriptor.ChangePolicyClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.ClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.TableNameClassDescriptorCustomizer;
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

        final ColumnNameCustomizerCompositeBuilder builder = defaultZalandoColumnNameCustomizer().with(
                new DirectToFieldMappingEnumTypeConverterCustomizer());

        clazzDescriptorCustomizer = newComposite().with(builder.build())
                                                  .with(new ChangePolicyClassDescriptorCustomizer())
                                                  .with(new TableNameClassDescriptorCustomizer()).build();
    }

    @Override
    public ClassDescriptorCustomizer getClassDescriptorCustomizer() {
        return clazzDescriptorCustomizer;
    }

}
