package de.zalando.jpa.example.id;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.data.jpa.auditing.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import de.zalando.jpa.config.DataSourceConfig;
import de.zalando.jpa.springframework.ExtendedEclipseLinkJpaVendorAdapter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
@ActiveProfiles("HSQL")
public class WorkerConfigTest {

    @Autowired
    private WorkerConfigRepository workerConfigRepository;

    @Autowired
    private WorkerRepository workerRepository;

    @Test
    public void persistWorkerConfig() {

        Assert.assertNotNull(workerConfigRepository);
        Assert.assertNotNull(workerRepository);

        //
        Worker worker = new Worker();
        worker = workerRepository.save(worker);

        //
        WorkerConfig config = new WorkerConfig(worker);
        config.setDescription("EINE DESCRIPTION");
        workerConfigRepository.save(config);

        // uses spring-data-default-jpa-repository
        WorkerConfig fromDb = workerConfigRepository.findOne(WorkerConfigPK.build(worker));

        // uses the custom-implementation
        WorkerConfig byWorkerId = workerConfigRepository.findByWorker(worker);

        //
        Assert.assertEquals(config, fromDb);
        Assert.assertEquals(fromDb, byWorkerId);
    }

    @Configuration
    @Import({ DataSourceConfig.class })
    @EnableJpaRepositories("de.zalando.jpa.example.id")
    @EnableJpaAuditing
    static class TestConfig {

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
            factory.setPersistenceUnitName("idExample");
            factory.setJpaVendorAdapter(vendorAdapter);
            factory.setDataSource(dataSource);

            return factory;
        }

    }
}
