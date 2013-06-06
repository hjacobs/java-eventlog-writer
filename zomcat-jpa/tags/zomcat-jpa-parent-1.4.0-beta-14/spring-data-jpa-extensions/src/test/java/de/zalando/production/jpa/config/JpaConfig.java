package de.zalando.production.jpa.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import de.zalando.jpa.springframework.ExtendedEclipseLinkJpaVendorAdapter;

/**
 * JpaConfiguration for Zalando Production Tests.
 *
 * @author  jbellmann
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "de.zalando.production.jpa.support.domain")
@ImportResource("classpath:/enableAuditing.xml")
public class JpaConfig {

    @Configuration
    @Profile("HSQL")
    static class HSQL {

        @Autowired
        private DataSource dataSource;

        @Bean
        public PlatformTransactionManager transactionManager() {
            JpaTransactionManager transactionManager = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
            return transactionManager;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            ExtendedEclipseLinkJpaVendorAdapter vendorAdapter = new ExtendedEclipseLinkJpaVendorAdapter();
            vendorAdapter.setDatabase(Database.HSQL);
            vendorAdapter.setGenerateDdl(true);
            vendorAdapter.setShowSql(true);

            LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
            factory.setPersistenceUnitName("persistenceUnit");
            factory.setJpaVendorAdapter(vendorAdapter);
            factory.setPackagesToScan("de.zalando.production.jpa.support.domain");
            factory.setDataSource(dataSource);

            return factory;
        }
    }

    @Configuration
    @Profile("POSTGRES")
    static class Postgres {

        @Autowired
        private DataSource dataSource;

        @Bean
        public PlatformTransactionManager transactionManager() {
            JpaTransactionManager transactionManager = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
            return transactionManager;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            ExtendedEclipseLinkJpaVendorAdapter vendorAdapter = new ExtendedEclipseLinkJpaVendorAdapter();
            vendorAdapter.setDatabase(Database.POSTGRESQL);
            vendorAdapter.setGenerateDdl(true);
            vendorAdapter.setShowSql(true);

            LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
            factory.setPersistenceUnitName("persistenceUnit");
            factory.setJpaVendorAdapter(vendorAdapter);
            factory.setPackagesToScan("de.zalando.production.jpa.support.domain");
            factory.setDataSource(dataSource);

            return factory;
        }
    }
}
