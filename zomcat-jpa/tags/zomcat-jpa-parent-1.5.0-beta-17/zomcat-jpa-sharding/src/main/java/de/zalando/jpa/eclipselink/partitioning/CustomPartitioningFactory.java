package de.zalando.jpa.eclipselink.partitioning;

import org.eclipse.persistence.annotations.Partitioning;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.sessions.Session;

import de.zalando.jpa.eclipselink.partitioning.annotations.CustomPartitioning;

/**
 * Inspects the {@link ClassDescriptor} to an annotation {@link Partitioning}.
 *
 * @author  jbellmann
 */
public class CustomPartitioningFactory implements PartitioningPolicyFactory {

    @Override
    public PartitioningPolicy build(final ClassDescriptor clazzDescriptor, final Session session) {

        final Class<?> descriptorJavaClass = clazzDescriptor.getJavaClass();
        CustomPartitioning customPartitioning = descriptorJavaClass.getAnnotation(CustomPartitioning.class);
        if (customPartitioning != null) {
            final String name = customPartitioning.name();
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalStateException("Name should never be null or empty for Partitioning.");
            }

            // TODO think about privileged access
            final Class<?> partitioningClass = customPartitioning.partitioningClass();
            if (partitioningClass == null) {
                throw new IllegalStateException("PartitioningClass should never be null");
            }

            try {

                // TODO, think about privileged access
                PartitioningPolicy policy = (PartitioningPolicy) partitioningClass.newInstance();
                policy.setName(name);

                // TODO, uniqueQuery ?
// policy.
                return policy;
            } catch (InstantiationException e) {

                throw new RuntimeException(e.getMessage(), e);
            } catch (IllegalAccessException e) {

                throw new RuntimeException(e.getMessage(), e);
            }
        }

        return null;
    }

}
