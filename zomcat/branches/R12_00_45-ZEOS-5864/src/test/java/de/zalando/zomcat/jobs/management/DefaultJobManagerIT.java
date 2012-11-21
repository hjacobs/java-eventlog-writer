package de.zalando.zomcat.jobs.management;

import org.springframework.test.context.ContextConfiguration;

import de.zalando.zomcat.jobs.management.impl.DefaultJobManager;

/**
 * Integrationtests for {@link DefaultJobManager}.
 *
 * @author      Thomas Zirke (thomas.zirke@zalando.de)
 * @deprecated
 */
@Deprecated
@ContextConfiguration(locations = {"classpath:defaultJobManagerBackendContextTest.xml"})
public class DefaultJobManagerIT extends AbstractJobManagerIT { }
