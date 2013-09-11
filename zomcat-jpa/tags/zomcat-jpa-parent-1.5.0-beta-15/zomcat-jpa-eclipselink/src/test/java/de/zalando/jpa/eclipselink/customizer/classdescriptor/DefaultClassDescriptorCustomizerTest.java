package de.zalando.jpa.eclipselink.customizer.classdescriptor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.Embeddable;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;

import org.junit.Before;
import org.junit.Test;

import de.zalando.jpa.eclipselink.Slf4jSessionLog;

/**
 * @author  jbellmann
 */
public class DefaultClassDescriptorCustomizerTest {

    private Session session = mock(Session.class);

    private ClassDescriptor clazzDescriptor;

    private DefaultClassDescriptorCustomizer customizer;

    private SessionLog sessionLog = new Slf4jSessionLog();

    @Before
    public void setUp() {

        clazzDescriptor = mock(ClassDescriptor.class);
        customizer = new DefaultClassDescriptorCustomizer();
        when(session.getSessionLog()).thenReturn(sessionLog);

    }

    /**
     * TODO. This is an open discussion yet. How to handle {@link Embeddable} annotated classes in the
     * {@link DefaultClassDescriptorCustomizer}? At the moment we are ignoring/skipping them from customization.
     */
    @Test
    public void testWithEmbeddableClasses() {

        when(clazzDescriptor.getJavaClass()).thenReturn(EmbeddableTestEntity.class);
        when(clazzDescriptor.getJavaClassName()).thenReturn(EmbeddableTestEntity.class.getSimpleName());

        customizer.customize(clazzDescriptor, session);

        verify(clazzDescriptor, never()).getMappings();
    }

    @Embeddable
    static class EmbeddableTestEntity {

        private String field;
    }
}
