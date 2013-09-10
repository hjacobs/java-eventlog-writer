package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;

/**
 * Some common helper-methods for Customizers.
 *
 * @author  jbellmann
 */
public abstract class LogSupport {

    public static final String SET_OBJECT_CHANGE_POLICY_TO = "Set ObjectChangePolicy to {0}";
    public static final String USE_DEFAULT_CHANGE_TRACKING_POLICY = "Use default change tracking policy";
    public static final String COULD_NOT_DETERMINE_CHANGE_TRACKING_TYPE =
        "Could not determine ChangeTrackingType for property value '{0}'. Use AUTO.";

    public static final String DEFERRED_CHANGE_DETECTION_POLICY = "DeferredChangeDetectionPolicy";
    public static final String OBJECT_CHANGE_TRACKING_POLICY = "ObjectChangeTrackingPolicy";
    public static final String ATTRIBUTE_CHANGE_TRACKING_POLICY = "AttributeChangeTrackingPolicy";

    public static final String NO_COL_CUSTOMIZER = "No ColumnNameCustomizer found for {0}";
    public static final String NO_CONV_CUSTOMIZER = "No ConverterCustomizer found for {0}";
    public static final String FIELD = "Field : {0}";
    public static final String START_CUS = "----  Customize for entity {0} ----\n";
    public static final String END_CUS = "----  Entity {0} customized  ----\n";

    public static final String CUS_SESSION_START = "Customize Session ...";
    public static final String CUS_SESSION_END = "Session customized";

    public static final String KEY_VALUE = "key : {0}, Value: {1}";
    public static final String SESSION_PROPS = "SessionProperties ...";

    protected void logFinest(final Session session, final String message, final Object... args) {
        session.getSessionLog().log(SessionLog.FINEST, message, args, false);
    }

    protected void logFinest(final Session session, final String message) {
        logFinest(session, message, new Object[] {});
    }

    protected void logFiner(final Session session, final String message, final Object... args) {
        session.getSessionLog().log(SessionLog.FINER, message, args, false);
    }

    protected void logFiner(final Session session, final String message) {
        logFiner(session, message, new Object[] {});
    }

    public static void logFine(final Session session, final String message, final Object... args) {
        session.getSessionLog().log(SessionLog.FINE, message, args, false);
    }

    public static void logFine(final Session session, final String message) {
        logFine(session, message, new Object[] {});
    }

    protected void logInfo(final Session session, final String message, final Object... args) {
        session.getSessionLog().log(SessionLog.INFO, message, args, false);
    }

    protected void logInfo(final Session session, final String message) {
        logInfo(session, message, new Object[] {});
    }

    protected void logAll(final Session session, final String message, final Object... args) {
        session.getSessionLog().log(SessionLog.ALL, message, args, false);
    }

    protected void logAll(final Session session, final String message) {
        logAll(session, message, new Object[] {});
    }

    protected void logWarning(final Session session, final String message, final Object... args) {
        session.getSessionLog().log(SessionLog.WARNING, message, args, false);
    }

    protected void logWarning(final Session session, final String message) {
        logWarning(session, message, new Object[] {});
    }

    protected void logServere(final Session session, final String message, final Object... args) {
        session.getSessionLog().log(SessionLog.SEVERE, message, args, false);
    }

    protected void logServere(final Session session, final String message) {
        logServere(session, message, new Object[] {});
    }
}
