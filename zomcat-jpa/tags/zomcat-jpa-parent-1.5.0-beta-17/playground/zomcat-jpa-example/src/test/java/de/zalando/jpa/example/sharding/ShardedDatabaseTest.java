package de.zalando.jpa.example.sharding;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.zalando.jpa.config.DataSourceConfig;
import de.zalando.jpa.config.TestProfiles;

/**
 * @author  jbellmann
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DataSourceConfig.class})
@ActiveProfiles(TestProfiles.H2_SHARDED)
public class ShardedDatabaseTest {

    @Autowired
    @Resource(name = "embeddedDatabaseOne")
    EmbeddedDatabase embeddedDatabaseOne;

    @Autowired
    @Resource(name = "embeddedDatabaseTwo")
    EmbeddedDatabase embeddedDatabaseTwo;

    @Autowired
    private DataSource dataSource;

    @Test
    public void testDataSourceNotNull() throws SQLException {
        Assert.assertNotNull(dataSource);

        Assert.assertNotNull(embeddedDatabaseOne);
        Assert.assertNotNull(embeddedDatabaseTwo);

        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            Assert.assertNotNull(connection);
        } finally {
            if (connection != null) {

                connection.close();
            }
        }
    }
}
