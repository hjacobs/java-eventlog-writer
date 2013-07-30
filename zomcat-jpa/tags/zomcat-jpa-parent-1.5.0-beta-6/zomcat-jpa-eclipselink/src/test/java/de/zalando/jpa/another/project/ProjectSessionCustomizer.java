package de.zalando.jpa.another.project;

import de.zalando.jpa.eclipselink.customizer.classdescriptor.ClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.session.AbstractZomcatSessionCustomizer;

/**
 * @author  jbellmann
 */
public class ProjectSessionCustomizer extends AbstractZomcatSessionCustomizer {

    private final ClassDescriptorCustomizer clazzDescriptorCustomizer;

    public ProjectSessionCustomizer() {

        final ClassDescriptorCustomizer zalandoDefaults = newBuilderWithDefaults().build();

        clazzDescriptorCustomizer = newComposite().with(zalandoDefaults).build();
    }

    @Override
    public ClassDescriptorCustomizer getClassDescriptorCustomizer() {
        return clazzDescriptorCustomizer;
    }

}
