package de.zalando.catalog.backend;

import java.util.Map;

import org.eclipse.persistence.annotations.Partitioned;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;

import com.google.common.base.Strings;

import de.zalando.jpa.eclipselink.customizer.session.DefaultZomcatSessionCustomizer;

public class PartitioningPoliciesSessionCustomizer extends DefaultZomcatSessionCustomizer {

    @Override
    public void customize(final Session session) throws Exception {
        super.customize(session);

        final Project project = session.getProject();
        for (final Map.Entry<Class, ClassDescriptor> classClassDescriptorEntry : session.getDescriptors().entrySet()) {
            final ClassDescriptor value = classClassDescriptorEntry.getValue();
            final Class<?> javaClass = value.getJavaClass();
            if (javaClass.isAnnotationPresent(Partitioned.class)) {
                final Partitioned partitioned = javaClass.getAnnotation(Partitioned.class);
                if (!Strings.isNullOrEmpty(partitioned.value())) {
                    if (project.getPartitioningPolicy(partitioned.value()) == null) {
                        project.addPartitioningPolicy(new DelegatingPartitioningPolicy(partitioned.value()));
                    }
                }
            }
        }
    }

}
