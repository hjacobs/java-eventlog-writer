package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;

import org.mockito.Mockito;

public class MockSessionCreator {

    public static Session create() {
        SessionLog log = Mockito.mock(SessionLog.class);
        Session session = Mockito.mock(Session.class);
        Mockito.when(session.getSessionLog()).thenReturn(log);
        return session;
    }

}
