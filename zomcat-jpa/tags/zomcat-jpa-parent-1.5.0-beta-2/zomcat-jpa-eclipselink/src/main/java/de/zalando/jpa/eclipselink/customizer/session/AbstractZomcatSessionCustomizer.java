package de.zalando.jpa.eclipselink.customizer.session;

import java.util.Map;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Session;

import de.zalando.jpa.eclipselink.LogSupport;
import de.zalando.jpa.eclipselink.customizer.classdescriptor.ClassDescriptorCustomizer;

/**
 * @author  jbellmann
 */
public abstract class AbstractZomcatSessionCustomizer extends LogSupport implements SessionCustomizer {

    @SuppressWarnings("rawtypes")
    @Override
    public void customize(final Session session) throws Exception {
        logInfo(session, "Customize Session ...");

        logSessionProperties(session);

        Map<Class, ClassDescriptor> clazzDescriptors = session.getDescriptors();
        for (Map.Entry<Class, ClassDescriptor> descriptorEntry : clazzDescriptors.entrySet()) {

            getClassDescriptorCustomizer().customize(descriptorEntry.getValue(), session);
        }

        logInfo(session, "Session customized");
    }

    protected void logSessionProperties(final Session session) {
        logFine(session, "SessionProperties ...");
        for (Map.Entry<Object, Object> entry : session.getProperties().entrySet()) {
            logFine(session, "key : {0}, Value: {1}", entry.getKey().toString(), entry.getValue().toString());
        }
    }

    abstract ClassDescriptorCustomizer getClassDescriptorCustomizer();
}
