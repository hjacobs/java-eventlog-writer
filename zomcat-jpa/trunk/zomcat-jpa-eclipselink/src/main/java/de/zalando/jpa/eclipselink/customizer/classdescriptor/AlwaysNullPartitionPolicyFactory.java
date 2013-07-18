package de.zalando.jpa.eclipselink.customizer.classdescriptor;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.sessions.Session;

/**
 * Returns always null to do nothing.
 *
 * @author  jbellmann
 */
public final class AlwaysNullPartitionPolicyFactory implements PartitioningPolicyFactory {

    @Override
    public PartitioningPolicy build(final ClassDescriptor clazzDescriptor, final Session session) {

        // TODO Auto-generated method stub
        return null;
    }

}
