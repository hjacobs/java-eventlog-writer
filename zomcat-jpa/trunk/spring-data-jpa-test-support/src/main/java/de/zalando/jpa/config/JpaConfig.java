package de.zalando.jpa.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.zalando.jpa.springframework.ExtendedEclipseLinkJpaVendorAdapter;

/**
 * @author  jbellmann
 */
@Configuration
@EnableTransactionManagement
public class JpaConfig {

    private static final Logger LOG = LoggerFactory.getLogger(JpaConfig.class);

    @Autowired(required = false)
    private DataSource dataSource;

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
        ExtendedEclipseLinkJpaVendorAdapter vendorAdapter = new ExtendedEclipseLinkJpaVendorAdapter();
        vendorAdapter.setDatabase(database);
        // this will overwrite "create-or-extend-tables", so commented
// vendorAdapter.setGenerateDdl(true);
// vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setPersistenceUnitName(persistenceUnitNameProvider.getPersistenceUnitName());
        factory.setJpaVendorAdapter(vendorAdapter);
        if (this.dataSource != null) {
            factory.setDataSource(dataSource);
        } else {
            LOG.warn(
                "No DataSource was configured. So we expect you provide connection-pool configuration in the 'persistence.xml'?");
        }

        return factory;
    }

}
