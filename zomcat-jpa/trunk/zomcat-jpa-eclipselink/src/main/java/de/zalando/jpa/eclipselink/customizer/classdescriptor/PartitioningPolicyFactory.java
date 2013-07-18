package de.zalando.jpa.eclipselink.customizer.classdescriptor;

import java.lang.annotation.Annotation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.sessions.Session;

/**
 * Creates {@link PartitioningPolicy}s with informations supplied by {@link ClassDescriptor} and {@link Session}.
 *
 * @author  jbellmann
 */
public interface PartitioningPolicyFactory {

    /**
     * Builds up an {@link PartitioningPolicy} with the information supplied by the {@link ClassDescriptor} and
     * {@link Session}.<br/>
     * Returns null if no {@link PartitioningPolicy} can be created. It should be possible to use for example reflection
     * to get {@link Annotation}s or further information from {@link Session#getProperties()}.
     *
     * @param   clazzDescriptor
     * @param   session
     *
     * @return  null if no {@link PartitioningPolicy} can or should be created.
     */
    PartitioningPolicy build(final ClassDescriptor clazzDescriptor, final Session session);
}
