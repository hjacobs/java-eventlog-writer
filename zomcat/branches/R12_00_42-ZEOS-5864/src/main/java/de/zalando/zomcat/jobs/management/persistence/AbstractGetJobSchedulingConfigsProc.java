package de.zalando.zomcat.jobs.management.persistence;

import java.sql.SQLException;

import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import de.zalando.dbutils.sproc.BaseStoredProcedure;

import de.zalando.zomcat.jobs.management.persistence.model.DatabaseJobSchedulingConfiguration;

/**
 * @author  Thomas Zirke (thomas.zirke@zalando.de)
 */
public abstract class AbstractGetJobSchedulingConfigsProc extends BaseStoredProcedure {

    public AbstractGetJobSchedulingConfigsProc(final DataSource dataSource) {
        setDataSource(dataSource);
        setResultMapper(new GetJobScheduingConfigsResultMapper());
        declareAndCompile();
    }

    @SuppressWarnings("unchecked")
    public List<DatabaseJobSchedulingConfiguration> getJobSchedulingConfigurations() throws SQLException {
        final List<DatabaseJobSchedulingConfiguration> result = (List<DatabaseJobSchedulingConfiguration>)
            executeWithMultipleResults();
        if (result == null) {
            return Collections.emptyList();
        } else {
            return result;
        }
    }

}
