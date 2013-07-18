package de.zalando.jpa.eclipselink.customizer.classdescriptor;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.sessions.Session;

import de.zalando.jpa.eclipselink.ZomcatPersistenceUnitProperties;

/**
 * Implementation of an {@link ClassDescriptorCustomizer} that inspects an {@link ClassDescriptor} and maybe creates an
 * {@link PartitioningPolicy}.
 *
 * @author  jbellmann
 */
public class PartitioningClassDescriptorCustomizer implements ClassDescriptorCustomizer {

    private PartitioningPolicyFactory factory = null;

    private final Object mutex = new Object();

    @Override
    public void customize(final ClassDescriptor clazzDescriptor, final Session session) {
        if (factory == null) {
            synchronized (mutex) {

                String clazzName = (String) session.getProperty(
                        ZomcatPersistenceUnitProperties.ZOMCAT_ECLIPSELINK_PARTITION_POLICY_FACTORY_NAME);
                if (clazzName != null && !(clazzName.trim().isEmpty())) {

                    this.factory = PartitioningPolicyFactoryBuilder.buildFromClassName(clazzName);
                } else {

                    this.factory = new AlwaysNullPartitionPolicyFactory();
                }
            }
        }

        PartitioningPolicy policy = factory.build(clazzDescriptor, session);

        // if factory returns an policy, add it to project
        if (policy != null && session.getProject() != null) {
            session.getProject().addPartitioningPolicy(policy);
        }
    }

}
