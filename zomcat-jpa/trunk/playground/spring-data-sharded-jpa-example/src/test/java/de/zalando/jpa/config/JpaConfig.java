package de.zalando.jpa.config;

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
        // this will overwrite "create-or-extend-tables", so commented
// vendorAdapter.setGenerateDdl(true);
// vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setPersistenceUnitName("default");
        factory.setJpaVendorAdapter(vendorAdapter);
        // for sharding we set no datasource
// factory.setDataSource(dataSource);

        return factory;
    }

}
