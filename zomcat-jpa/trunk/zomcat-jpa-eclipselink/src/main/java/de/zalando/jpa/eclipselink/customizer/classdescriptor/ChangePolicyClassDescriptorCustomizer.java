package de.zalando.jpa.eclipselink.customizer.classdescriptor;

import org.eclipse.persistence.annotations.ChangeTrackingType;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.descriptors.changetracking.DeferredChangeDetectionPolicy;
import org.eclipse.persistence.descriptors.changetracking.ObjectChangeTrackingPolicy;
import org.eclipse.persistence.sessions.Session;

import de.zalando.jpa.eclipselink.LogSupport;

/**
 * This {@link ClassDescriptorCustomizer} customizes {@link ChangeTrackingType} for an {@link ClassDescriptor}.
 *
 * @author  jbellmann
 */
public class ChangePolicyClassDescriptorCustomizer extends LogSupport implements ClassDescriptorCustomizer {

    @Override
    public void customize(final ClassDescriptor clazzDescriptor, final Session session) {
        customizeObjectChangePolicy(clazzDescriptor, session);
    }

    private void customizeObjectChangePolicy(final ClassDescriptor clazzDescriptor, final Session session) {
        final String propertyValue = (String) session.getProperty(ZOMCAT_JPA_CHANGE_TRACKER_TYPE);

        ChangeTrackingType changeTrackingType = ChangeTrackingType.AUTO;

        if (propertyValue != null && (!propertyValue.trim().isEmpty())) {
            try {
                changeTrackingType = ChangeTrackingType.valueOf(propertyValue);
            } catch (Exception e) {
                logWarning(session, COULD_NOT_DETERMINE_CHANGE_TRACKING_TYPE, propertyValue);
                changeTrackingType = ChangeTrackingType.AUTO;
            }
        }

        switch (changeTrackingType) {

            case DEFERRED :
                clazzDescriptor.setObjectChangePolicy(new DeferredChangeDetectionPolicy());
                logFine(session, SET_OBJECT_CHANGE_POLICY_TO, DEFERRED_CHANGE_DETECTION_POLICY);
                break;

            case OBJECT :
                clazzDescriptor.setObjectChangePolicy(new ObjectChangeTrackingPolicy());
                logFine(session, SET_OBJECT_CHANGE_POLICY_TO, OBJECT_CHANGE_TRACKING_POLICY);
                break;

            case ATTRIBUTE :
                clazzDescriptor.setObjectChangePolicy(new AttributeChangeTrackingPolicy());
                logFine(session, SET_OBJECT_CHANGE_POLICY_TO, ATTRIBUTE_CHANGE_TRACKING_POLICY);
                break;

            case AUTO :
            default :
                logFine(session, USE_DEFAULT_CHANGE_TRACKING_POLICY);
        }
    }

}
