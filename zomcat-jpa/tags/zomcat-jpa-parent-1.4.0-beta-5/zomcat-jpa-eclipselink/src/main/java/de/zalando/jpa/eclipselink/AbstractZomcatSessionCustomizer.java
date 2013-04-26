package de.zalando.jpa.eclipselink;

import java.util.Map;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;

/**
 * @author  jbellmann
 */
public abstract class AbstractZomcatSessionCustomizer implements SessionCustomizer {

    @SuppressWarnings("rawtypes")
    @Override
    public void customize(final Session session) throws Exception {
        session.getSessionLog().log(SessionLog.INFO, "Customize Session ...");

        Map<Class, ClassDescriptor> clazzDescriptors = session.getDescriptors();
        for (Map.Entry<Class, ClassDescriptor> descriptorEntry : clazzDescriptors.entrySet()) {
            getClassDescriptorCustomizer().customize(descriptorEntry.getValue(), session);
        }

        session.getSessionLog().log(SessionLog.INFO, "Session customized");
    }

    abstract ClassDescriptorCustomizer getClassDescriptorCustomizer();
}
