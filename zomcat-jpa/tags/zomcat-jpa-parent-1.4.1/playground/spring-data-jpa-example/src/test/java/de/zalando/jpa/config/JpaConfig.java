package de.zalando.jpa.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.support.ClasspathScanningPersistenceUnitPostProcessor;

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
@EnableJpaRepositories(basePackages = "de.zalando.production.repository")
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

            ClasspathScanningPersistenceUnitPostProcessor postProcessor =
                new ClasspathScanningPersistenceUnitPostProcessor("de.zalando.jpa.example.order");

            LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
            factory.setPersistenceUnitName("default");
            factory.setJpaVendorAdapter(vendorAdapter);
            factory.setPackagesToScan("de.zalando.jpa.example.order");
            factory.setDataSource(dataSource);
            factory.setPersistenceUnitPostProcessors(postProcessor);

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

            ClasspathScanningPersistenceUnitPostProcessor postProcessor =
                new ClasspathScanningPersistenceUnitPostProcessor("de.zalando.jpa.example.order");

            LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
            factory.setPersistenceUnitName("integrationTest");
            factory.setJpaVendorAdapter(vendorAdapter);
            factory.setPackagesToScan("de.zalando.jpa.example.order");
            factory.setDataSource(dataSource);
            factory.setPersistenceUnitPostProcessors(postProcessor);

            return factory;
        }
    }

    @Configuration
    @Profile("H2")
    static class H2 {
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
            vendorAdapter.setDatabase(Database.H2);
            vendorAdapter.setGenerateDdl(true);
            vendorAdapter.setShowSql(true);

            ClasspathScanningPersistenceUnitPostProcessor postProcessor =
                new ClasspathScanningPersistenceUnitPostProcessor("de.zalando.jpa.example.order");

            LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
            factory.setPersistenceUnitName("default");
            factory.setJpaVendorAdapter(vendorAdapter);
            factory.setPackagesToScan("de.zalando.jpa.example.order");
            factory.setDataSource(dataSource);
            factory.setPersistenceUnitPostProcessors(postProcessor);

            return factory;
        }
    }
}
