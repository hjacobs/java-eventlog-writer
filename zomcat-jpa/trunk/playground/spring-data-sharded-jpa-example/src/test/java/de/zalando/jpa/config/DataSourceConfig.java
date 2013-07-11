package de.zalando.jpa.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jolbox.bonecp.BoneCPDataSource;

@Configuration
public class DataSourceConfig {
    @Configuration
// @Profile(TestProfiles.POSTGRES)
    static class PostgreSqlDataSource {
        @Bean(name = "dataSource")
        public DataSource dataSource() {
            BoneCPDataSource dataSource = new BoneCPDataSource();

            dataSource.setUsername("postgres");
            dataSource.setPassword("postgres");
            dataSource.setJdbcUrl("jdbc:postgresql://192.168.58.133:5432/local_zomcat_jpa1_db");
            dataSource.setDriverClass("org.postgresql.Driver");
            dataSource.setInitSQL("SET search_path to zzj_data,public;");

            return dataSource;
        }

        @Bean(name = "dataSource2")
        public DataSource dataSource2() {
            BoneCPDataSource dataSource = new BoneCPDataSource();

            dataSource.setUsername("postgres");
            dataSource.setPassword("postgres");
            dataSource.setJdbcUrl("jdbc:postgresql://192.168.58.133:5432/local_zomcat_jpa2_db");
            dataSource.setDriverClass("org.postgresql.Driver");
            dataSource.setInitSQL("SET search_path to zzj_data,public;");

            return dataSource;
        }
    }
}
