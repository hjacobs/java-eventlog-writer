package de.zalando.jpa.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.google.common.collect.Maps;

import com.jolbox.bonecp.BoneCPDataSource;

/**
 * Muliple DataSource-Configurations for Tests.
 *
 * @author  jbellmann
 */
@Configuration
public class DataSourceConfig {

    @Configuration
    @Profile(TestProfiles.HSQL)
    static class HSQLDataSource {

        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL);
            dataSource.addScript("schema_hsql.sql");
            dataSource.addScript("sequence_hsql.sql");
            return dataSource.build();
        }
    }

    @Configuration
    @Profile(TestProfiles.POSTGRES)
    static class PostgreSqlDataSource {

        @Bean
        public DataSource dataSource() {
            BoneCPDataSource dataSource = new BoneCPDataSource();

            dataSource.setUsername("postgres");
            dataSource.setPassword("postgres");
            dataSource.setJdbcUrl("jdbc:postgresql://localhost:5434/local_zomcat_jpa_db");
            dataSource.setDriverClass("org.postgresql.Driver");
            dataSource.setInitSQL("SET search_path to zzj_data,public;");

            return dataSource;
        }
    }

    @Configuration
    @Profile(TestProfiles.H2)
    static class H2DataSource {

        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2);

            dataSource.addScript("schema_h2.sql"); // .addScript("sequence_h2.sql");
            return dataSource.build();
        }
    }

    @Configuration
    @Profile(TestProfiles.H2_SHARDED)
    static class H2ShardedDataSource {

        @Bean
        public EmbeddedDatabase embeddedDatabaseOne() {

            return new EmbeddedDatabaseBuilder().setName("ONE").setType(EmbeddedDatabaseType.H2).build();
        }

        @Bean
        public EmbeddedDatabase embeddedDatabaseTwo() {

            return new EmbeddedDatabaseBuilder().setName("TWO").setType(EmbeddedDatabaseType.H2).build();
        }

        @Bean
        public DataSource dataSource() {
            AbstractRoutingDataSource routingDataSource = new RoundRobinRoutingDataSource();

            //
            Map<Object, Object> dataSourceMap = Maps.newHashMap();
            dataSourceMap.put(ShardKey.ONE, embeddedDatabaseOne());
            dataSourceMap.put(ShardKey.TWO, embeddedDatabaseTwo());

            //
            routingDataSource.setTargetDataSources(dataSourceMap);
            routingDataSource.setDefaultTargetDataSource(embeddedDatabaseOne());
            routingDataSource.afterPropertiesSet();
            return routingDataSource;
        }
    }

}
