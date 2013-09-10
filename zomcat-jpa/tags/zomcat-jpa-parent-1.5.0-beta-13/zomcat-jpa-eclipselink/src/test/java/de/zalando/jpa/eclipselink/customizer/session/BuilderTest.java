package de.zalando.jpa.eclipselink.customizer.session;

import org.junit.Assert;
import org.junit.Test;

import de.zalando.jpa.eclipselink.customizer.classdescriptor.ChangePolicyClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.ClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.DefaultClassDescriptorCustomizer;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.PartitioningAnnotationClassDescriptorCustomizer;

/**
 * @author  jbellmann
 */
public class BuilderTest {

    @Test
    public void withBuilder() {
        AbstractZomcatSessionCustomizer sessionCustomizer = new BuilderSessionCustomizer();
        Assert.assertNotNull(sessionCustomizer);

        ClassDescriptorCustomizer c = sessionCustomizer.getClassDescriptorCustomizer();
        Assert.assertNotNull(c);
        Assert.assertTrue(CompositeClassDescriptorCustomizer.class.isAssignableFrom(c.getClass()));
    }

    class BuilderSessionCustomizer extends AbstractZomcatSessionCustomizer {

        @Override
        public ClassDescriptorCustomizer getClassDescriptorCustomizer() {
            return newComposite().with(new DefaultClassDescriptorCustomizer())
                                 .with(new ChangePolicyClassDescriptorCustomizer())
                                 .with(new PartitioningAnnotationClassDescriptorCustomizer()).build();
        }

    }

}
