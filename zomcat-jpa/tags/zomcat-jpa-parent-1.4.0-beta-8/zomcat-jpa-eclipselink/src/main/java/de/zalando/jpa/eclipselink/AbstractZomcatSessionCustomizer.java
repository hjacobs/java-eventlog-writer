package de.zalando.jpa.eclipselink;

import java.util.Map;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Session;

/**
 * @author  jbellmann
 */
public abstract class AbstractZomcatSessionCustomizer extends AbstractCustomizer implements SessionCustomizer {

    @SuppressWarnings("rawtypes")
    @Override
    public void customize(final Session session) throws Exception {
        logInfo(session, "Customize Session ...");

        Map<Class, ClassDescriptor> clazzDescriptors = session.getDescriptors();
        for (Map.Entry<Class, ClassDescriptor> descriptorEntry : clazzDescriptors.entrySet()) {
            getClassDescriptorCustomizer().customize(descriptorEntry.getValue(), session);
        }

        logInfo(session, "Session customized");
    }

    abstract ClassDescriptorCustomizer getClassDescriptorCustomizer();
}
