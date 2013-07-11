package de.zalando.jpa.example.identity;

import org.springframework.test.context.ActiveProfiles;

import de.zalando.jpa.config.TestProfiles;

/**
 * @author  jbellmann
 */
@ActiveProfiles(TestProfiles.POSTGRES)
public class PostgresIdentityIT extends AbstractIdentityTest { }
