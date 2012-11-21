package de.zalando.zomcat.jobs.management;

import org.springframework.test.context.ContextConfiguration;

import de.zalando.zomcat.jobs.management.impl.SingleQuartzSchedulerJobManager;

/**
 * Integrationtests for {@link SingleQuartzSchedulerJobManager}.
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
@ContextConfiguration(locations = {"classpath:singleQuartzSchedulerJobManagerBackendContextTest.xml"})
public class SingleQuartzSchedulerJobManagerIT extends AbstractJobManagerIT { }
