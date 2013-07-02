package de.zalando.jpa.config;

import javax.sql.DataSource;

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
public class JpaZwoConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private Database database;

    @Autowired
    private PersistenceUnitNameProvider persistenceUnitNameProvider;

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
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setPersistenceUnitName(persistenceUnitNameProvider.getPersistenceUnitName());
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setDataSource(dataSource);

        return factory;
    }

}
