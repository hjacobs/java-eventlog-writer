package de.zalando.zomcat.jobs.management.persistence;

import javax.sql.DataSource;

import de.zalando.dbutils.sproc.SProc;

/**
 * Default Extension of {@link AbstractGetJobSchedulingConfigsProc}. Designed according to AppConfig loading mechanism
 *
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
@SProc(name = "get_job_scheduling_configs", resultName = "get_job_scheduling_configs_result")
public final class DefaultGetJobSchedulingConfigsProc extends AbstractGetJobSchedulingConfigsProc {

    public DefaultGetJobSchedulingConfigsProc(final DataSource dataSource) {
        super(dataSource);
    }
}
