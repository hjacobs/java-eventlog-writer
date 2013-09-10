package de.zalando.data.jpa.domain.support;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.postgresql.ds.PGSimpleDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.util.StringUtils;

import de.zalando.data.jpa.domain.support.jdbc.SpringJdbcBusinessKeyGenerator;
import de.zalando.data.jpa.domain.support.jdbc.StoredProcedureBusinessKeyGenerator;

/**
 * @author  jbellmann
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class StoredProcedureBusinessKeyGeneratorIT {

    private static final Logger LOG = LoggerFactory.getLogger(StoredProcedureBusinessKeyGeneratorIT.class);

    @Autowired
    private DataSource dataSource;

    @Test
    public void testGetBusinessKey() {
        Assert.assertNotNull(dataSource);

        BusinessKeyGenerator generator = new StoredProcedureBusinessKeyGenerator(dataSource);

        String nextNumber = generator.getBusinessKeyForSelector("CONDITION_AGREEMENT");

        assertNextNumber(nextNumber);
    }

    @Ignore
    @Test
    public void testBusinessKeyGenerator() {
        SpringJdbcBusinessKeyGenerator generator = new SpringJdbcBusinessKeyGenerator();
        generator.setDataSource(dataSource);

        // important, in an application this will be done by spring itself
        generator.afterPropertiesSet();

        String businessKey = generator.getBusinessKeyForSelector("CONDITION_AGREEMENT");

        assertNextNumber(businessKey);
    }

    private void assertNextNumber(final String nextNumber) {
        Assert.assertNotNull(nextNumber);
        Assert.assertTrue(StringUtils.hasText(nextNumber));
        Assert.assertTrue(nextNumber.startsWith("CA"));
        LOG.info("GOT {}", nextNumber);
    }

    @Configuration
    static class TestConfig {

        @Bean
        public DataSource dataSource() {
            PGSimpleDataSource pgsd = new PGSimpleDataSource();
            pgsd.setUser("postgres");
            pgsd.setPassword("postgres");
            pgsd.setServerName("localhost");
            pgsd.setDatabaseName("local_purchase_db");
            pgsd.setPortNumber(5432);
            return pgsd;
        }
    }
}
