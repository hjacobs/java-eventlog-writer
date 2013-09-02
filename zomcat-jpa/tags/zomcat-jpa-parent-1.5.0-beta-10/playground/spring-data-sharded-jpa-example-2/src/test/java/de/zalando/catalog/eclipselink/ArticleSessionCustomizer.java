package de.zalando.catalog.eclipselink;

import de.zalando.jpa.eclipselink.customizer.classdescriptor.ClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.PartitioningAnnotationClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.session.AbstractZomcatSessionCustomizer;

/**
 * Example how to configure an project-specific-session-customizer.
 *
 * @author  jbellmann
 */
public class ArticleSessionCustomizer extends AbstractZomcatSessionCustomizer {

    private final ClassDescriptorCustomizer clazzDescriptorCustomizer;

    public ArticleSessionCustomizer() {
        super();

        final ClassDescriptorCustomizer defaultMappings = newBuilderWithDefaults().build();

        clazzDescriptorCustomizer = newComposite().with(defaultMappings)
                                                  .with(new PartitioningAnnotationClassDescriptorCustomizer()).build();
    }

    @Override
    public ClassDescriptorCustomizer getClassDescriptorCustomizer() {

        return this.clazzDescriptorCustomizer;
    }

}
