package de.zalando.production.jpa.support.jdbc;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.zalando.data.jpa.domain.support.jdbc.SequenceIdGenerator;

/**
 * Author: clohmann Date: 06.05.13 Time: 17:50
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class SequenceSkuIdGeneratorTest {

    @Autowired
    private DataSource dataSource;

    @Before
    public void setUp() {
        Assert.assertNotNull(dataSource);
    }

    @Test
    public void testGetSkuId() throws Exception {
        SequenceIdGenerator generator = new SequenceIdGenerator(dataSource);
        generator.getSeqId("zprod_data.article_simple_id_seq", false);
    }

    @Configuration
    static class TestConfig {

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL).addScript("schema_hsql.sql")
                                                .addScript("sequence_hsql.sql").build();
        }
    }
}
