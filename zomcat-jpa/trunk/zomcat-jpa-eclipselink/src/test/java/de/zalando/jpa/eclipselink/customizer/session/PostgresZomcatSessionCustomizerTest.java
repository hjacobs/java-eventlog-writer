package de.zalando.jpa.eclipselink.customizer.session;

import org.eclipse.persistence.config.SessionCustomizer;

import org.junit.Ignore;
import org.junit.Test;

/**
 * @author  jbellmann
 */
@Deprecated
@Ignore
public class PostgresZomcatSessionCustomizerTest extends AbstractSessionCutomizerTest {

    @Test
    public void testCustomizer() throws Exception {
        super.testCustomizer();
    }

    @Override
    SessionCustomizer getSessionCustomizer() {
        return new PostgresZomcatSessionCustomizer();
    }

}
