package de.zalando.jpa.example.unidirectional;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import de.zalando.jpa.config.TestProfiles;

/**
 * Test against H2-Database.
 *
 * @author  jbellmann
 */
@ActiveProfiles(TestProfiles.H2)
@DirtiesContext
public class H2UnidirectionalTest extends AbstractUnidirectionalTest { }
