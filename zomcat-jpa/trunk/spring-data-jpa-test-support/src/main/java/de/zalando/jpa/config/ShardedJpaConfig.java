package de.zalando.jpa.config;

import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.context.annotation.Bean;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.vendor.Database;

import org.springframework.transaction.PlatformTransactionManager;

import de.zalando.jpa.eclipselink.partitioning.ShardedEclipseLinkJpaVendor;

/**
 * TODO maybe we can generalize this.
 *
 * @author  jbellmann
 */
public class ShardedJpaConfig {

    private static final Logger LOG = LoggerFactory.getLogger(ShardedJpaConfig.class);

    @Autowired
    @Qualifier("defaultDataSource")
    private DataSource defaultDataSource;

    @Autowired
    private Map<String, DataSource> dataSourceLookup;

    @Autowired(required = false)
    private Database database = Database.POSTGRESQL;

    @Autowired(required = false)
    private PersistenceUnitNameProvider persistenceUnitNameProvider = new StandardPersistenceUnitNameProvider();

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());

        return transactionManager;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

        DefaultPersistenceUnitManager persistenceUnitManager = new DefaultPersistenceUnitManager();
// persistenceUnitManager.setDefaultDataSource(defaultDataSource);
        persistenceUnitManager.setDataSources(dataSourceLookup);

        ShardedEclipseLinkJpaVendor vendorAdapter = new ShardedEclipseLinkJpaVendor(persistenceUnitManager);
        LOG.info("database : {}", database.toString());
        vendorAdapter.setDatabase(database);

        // this will overwrite "create-or-extend-tables", so commented
        // vendorAdapter.setGenerateDdl(true);
        // vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setPersistenceUnitName(persistenceUnitNameProvider.getPersistenceUnitName());
        factory.setJpaVendorAdapter(vendorAdapter);

        // this should always not null, because we set it to be required (default on @Autowired)
        if (this.defaultDataSource != null) {
            LOG.info("setting 'defaultDataSource' to factory");
            factory.setDataSource(defaultDataSource);
        } else {
            LOG.warn(
                "No defaultDataSource was configured. So we expect you provide connection-pool configuration in the 'persistence.xml'?");
        }

        return factory;
    }
}
