package de.zalando.jpa.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.jolbox.bonecp.BoneCPDataSource;

@Configuration
public class DataSourceConfig {

    @Profile("HSQL")
    static class HSQLDataSource {

        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.HSQL);
            dataSource.addScript("schema_hsql.sql");
            dataSource.addScript("sequence_hsql.sql");
            return dataSource.build();
        }
    }

    @Profile("POSTGRES")
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

    @Profile("H2")
    static class H2DataSource {

        @Bean
        public DataSource dataSource() {
            EmbeddedDatabaseBuilder dataSource = new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2);
            dataSource.addScript("schema_h2.sql").addScript("sequence_h2.sql");
            return dataSource.build();
        }
    }

}
