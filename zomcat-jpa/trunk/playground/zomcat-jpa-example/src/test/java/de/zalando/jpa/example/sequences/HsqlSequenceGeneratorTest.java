package de.zalando.jpa.example.sequences;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import de.zalando.jpa.config.TestProfiles;

/**
 * @author  jbellmann
 */
@ActiveProfiles(TestProfiles.HSQL)
@DirtiesContext
public class HsqlSequenceGeneratorTest extends AbstractSequenceGeneratorTest { }
