package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;

/**
 * Some common helper-methods for Customizers.
 *
 * @author  jbellmann
 */
public abstract class AbstractCustomizer {

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

    protected void logFine(final Session session, final String message, final Object... args) {
        session.getSessionLog().log(SessionLog.FINE, message, args, false);
    }

    protected void logFine(final Session session, final String message) {
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
