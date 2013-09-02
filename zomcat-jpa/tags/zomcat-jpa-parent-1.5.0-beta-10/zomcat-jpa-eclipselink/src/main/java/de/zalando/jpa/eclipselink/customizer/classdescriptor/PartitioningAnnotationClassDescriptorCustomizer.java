package de.zalando.jpa.eclipselink.customizer.classdescriptor;

import org.eclipse.persistence.annotations.Partitioned;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Session;

import com.google.common.base.Strings;

import de.zalando.jpa.eclipselink.partitioning.DelegatingPartitioningPolicy;

/**
 * Inspects an {@link ClassDescriptor}s java-class for {@link Partitioned} annotation.
 *
 * @author  jbellmann
 */
public class PartitioningAnnotationClassDescriptorCustomizer implements ClassDescriptorCustomizer {

    @Override
    public void customize(final ClassDescriptor clazzDescriptor, final Session session) {
        final Class<?> javaClass = clazzDescriptor.getJavaClass();
        if (javaClass.isAnnotationPresent(Partitioned.class)) {
            final Partitioned partitioned = javaClass.getAnnotation(Partitioned.class);
            if (!Strings.isNullOrEmpty(partitioned.value())) {

                // add a new one if none exist yet
                if (session.getProject().getPartitioningPolicy(partitioned.value()) == null) {

                    // TODO, how will the delegate comes into this newly created object?
                    session.getProject().addPartitioningPolicy(new DelegatingPartitioningPolicy(partitioned.value()));
                }
            }
        }
    }

}
