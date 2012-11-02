package de.zalando.zomcat.jobs.management;

import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(locations = {"classpath:singleQuartzSchedulerJobManagerBackendContextTest.xml"})
public class SingleQuartzSchedulerJobManagerIT extends AbstractJobManagerIT { }
