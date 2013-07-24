package de.zalando.jpa.eclipselink.customizer.session;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Session;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.zalando.jpa.eclipselink.customizer.classdescriptor.ClassDescriptorCustomizer;

/**
 * To combine multiple {@link ClassDescriptorCustomizer}s.
 *
 * @author  jbellmann
 */
final class CompositeClassDescriptorCustomizer implements ClassDescriptorCustomizer {

    protected List<ClassDescriptorCustomizer> customizers = new LinkedList<ClassDescriptorCustomizer>();

    /**
     * Hide constructor.
     */
    protected CompositeClassDescriptorCustomizer() { }

    /**
     * Runs every added {@link ClassDescriptorCustomizer} for an {@link ClassDescriptor} with provided {@link Session}.
     */
    @Override
    public void customize(final ClassDescriptor clazzDescriptor, final Session session) {
        for (ClassDescriptorCustomizer c : customizers) {
            c.customize(clazzDescriptor, session);
        }
    }

    /**
     * Creates an {@link CompositeClassDescriptorCustomizer} from the assigned array of
     * {@link ClassDescriptorCustomizer}.
     *
     * @param   classDescriptorCustomizers
     *
     * @return
     */
    static ClassDescriptorCustomizer build(final ClassDescriptorCustomizer... classDescriptorCustomizers) {
        CompositeClassDescriptorCustomizer composite = new CompositeClassDescriptorCustomizer();
        composite.customizers.addAll(Lists.newArrayList(
                Iterables.filter(Arrays.asList(classDescriptorCustomizers), Predicates.notNull())));
        return composite;
    }

    /**
     * Creates an {@link CompositeClassDescriptorCustomizer} from the assigned list of {@link ClassDescriptorCustomizer}.
     *
     * @param   classDescriptorCustomizers
     *
     * @return
     */
    static ClassDescriptorCustomizer build(final List<ClassDescriptorCustomizer> classDescriptorCustomizers) {
        return build(classDescriptorCustomizers.toArray(new ClassDescriptorCustomizer[0]));
    }

}
