package de.zalando.jpa.example.identity;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import de.zalando.jpa.config.TestProfiles;

@ActiveProfiles(TestProfiles.H2)
@DirtiesContext
public class H2IdentityTest extends AbstractIdentityTest { }
