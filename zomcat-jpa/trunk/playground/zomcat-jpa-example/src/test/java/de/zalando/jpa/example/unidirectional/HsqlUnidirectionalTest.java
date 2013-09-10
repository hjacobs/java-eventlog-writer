package de.zalando.jpa.example.unidirectional;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import de.zalando.jpa.config.TestProfiles;

/**
 * Test agains HSQL-Database.
 *
 * @author  jbellmann
 */
@ActiveProfiles(TestProfiles.HSQL)
@DirtiesContext
public class HsqlUnidirectionalTest extends AbstractUnidirectionalTest { }
