package de.zalando.catalog.eclipselink;

import de.zalando.jpa.eclipselink.customizer.classdescriptor.ClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.TableNameClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.session.AbstractZomcatSessionCustomizer;
import de.zalando.jpa.eclipselink.partitioning.customizer.PartitioningAnnotationClassDescriptorCustomizer;

/**
 * Example how to configure an project-specific-session-customizer.
 *
 * @author  jbellmann
 */
public class ArticleSessionCustomizer extends AbstractZomcatSessionCustomizer {

    private final ClassDescriptorCustomizer clazzDescriptorCustomizer;

    public ArticleSessionCustomizer() {
        super();

        final ClassDescriptorCustomizer defaultMappings = defaultColumnNameClassDescriptorCustomizer();

        clazzDescriptorCustomizer = builder().with(new TableNameClassDescriptorCustomizer()).with(defaultMappings)
                                             .with(new PartitioningAnnotationClassDescriptorCustomizer()).build();
    }

    @Override
    public ClassDescriptorCustomizer getClassDescriptorCustomizer() {

        return this.clazzDescriptorCustomizer;
    }

}
