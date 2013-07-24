package de.zalando.jpa.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.lookup.MapDataSourceLookup;

/**
 * @author  jbellmann
 */
@Configuration
public class ShardedDataSourceConfig {

    @Configuration
    @Profile(TestProfiles.H2_SHARDED_4)
    static class H2Sharded4DataSource {

        @Bean
        public EmbeddedDatabase embeddedDatabaseOne() {

            return new EmbeddedDatabaseBuilder().setName("ONE").setType(EmbeddedDatabaseType.H2)
                                                .addScript("schema_h2.sql").build();
        }

        @Bean
        public EmbeddedDatabase embeddedDatabaseTwo() {

            return new EmbeddedDatabaseBuilder().setName("TWO").setType(EmbeddedDatabaseType.H2)
                                                .addScript("schema_h2.sql").build();
        }

        @Bean
        public EmbeddedDatabase embeddedDatabaseThree() {

            return new EmbeddedDatabaseBuilder().setName("THREE").setType(EmbeddedDatabaseType.H2)
                                                .addScript("schema_h2.sql").build();
        }

        @Bean
        public EmbeddedDatabase embeddedDatabaseFour() {

            return new EmbeddedDatabaseBuilder().setName("FOUR").setType(EmbeddedDatabaseType.H2)
                                                .addScript("schema_h2.sql").build();
        }

        @Bean(name = "defaultDataSource")
        public DataSource defaultDataSource() {
            return embeddedDatabaseOne();
        }

        @Bean
        public MapDataSourceLookup mapDataSourceLookup() {
            MapDataSourceLookup dl = new MapDataSourceLookup();
            dl.addDataSource("node1", embeddedDatabaseOne());
            dl.addDataSource("node2", embeddedDatabaseTwo());
            dl.addDataSource("node3", embeddedDatabaseThree());
            dl.addDataSource("node4", embeddedDatabaseFour());

            //
            return dl;
        }
    }

}
