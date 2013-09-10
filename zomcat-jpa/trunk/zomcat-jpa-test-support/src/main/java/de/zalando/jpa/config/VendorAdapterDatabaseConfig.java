package de.zalando.jpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.orm.jpa.vendor.Database;

/**
 * @author  jbellmann
 */
@Configuration
public class VendorAdapterDatabaseConfig {

    @Configuration
    @Profile({ TestProfiles.H2, TestProfiles.H2_SHARDED_4, TestProfiles.H2_SHARDED })
    static class H2VendorAdapter {

        @Bean
        public Database database() {
            return Database.H2;
        }
    }

    @Configuration
    @Profile(TestProfiles.POSTGRES)
    static class PostgresVendorAdapter {

        @Bean
        public Database database() {
            return Database.POSTGRESQL;
        }
    }

    @Configuration
    @Profile(TestProfiles.HSQL)
    static class HsqlVendorAdapter {

        @Bean
        public Database database() {
            return Database.HSQL;
        }
    }
}
