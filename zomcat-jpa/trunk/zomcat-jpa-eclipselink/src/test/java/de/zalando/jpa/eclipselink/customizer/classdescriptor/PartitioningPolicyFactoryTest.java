package de.zalando.jpa.eclipselink.customizer.classdescriptor;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.sessions.Session;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author  jbellmann
 */
public class PartitioningPolicyFactoryTest {

    @Test(expected = IllegalArgumentException.class)
    public void getNotInitializedPartitioningPolicyFactory() {

        //
        PartitioningPolicyFactory f = PartitioningPolicyFactoryBuilder.buildFromClassName(null);
    }

    @Test
    public void initPartitioningPolicyFactory() {

        String clazzName = AlwaysNullPartitionPolicyFactory.class.getName();
        clazzName = clazzName.replace("/", ".");

        //
        PartitioningPolicyFactory f = PartitioningPolicyFactoryBuilder.buildFromClassName(clazzName);

        ClassDescriptor descriptor = new ClassDescriptor();
        Session session = Mockito.mock(Session.class);
        PartitioningPolicy policy = f.build(descriptor, session);
        Assert.assertNull(policy);
    }

}
