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
    @Profile(TestProfiles.POSTGRES)
    static class PostgresVendorAdapter {

        @Bean
        public Database database() {
            return Database.POSTGRESQL;
        }
    }

}
