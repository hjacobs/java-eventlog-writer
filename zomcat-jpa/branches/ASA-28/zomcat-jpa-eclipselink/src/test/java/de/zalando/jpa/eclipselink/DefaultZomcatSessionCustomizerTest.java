package de.zalando.jpa.eclipselink;

import org.eclipse.persistence.config.SessionCustomizer;

import org.junit.Test;

/**
 * @author  jbellmann
 */
public class DefaultZomcatSessionCustomizerTest extends AbstractSessionCutomizerTest {

    @Test
    public void testCustomizer() throws Exception {
        super.testCustomizer();
    }

    @Override
    SessionCustomizer getSessionCustomizer() {
        return new DefaultZomcatSessionCustomizer();
    }

}
