package de.zalando.jpa.config;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.google.common.collect.Maps;

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

            return new EmbeddedDatabaseBuilder().setName("ONE").setType(EmbeddedDatabaseType.H2).build();
        }

        @Bean
        public EmbeddedDatabase embeddedDatabaseTwo() {

            return new EmbeddedDatabaseBuilder().setName("TWO").setType(EmbeddedDatabaseType.H2).build();
        }

        @Bean
        public EmbeddedDatabase embeddedDatabaseThree() {

            return new EmbeddedDatabaseBuilder().setName("THREE").setType(EmbeddedDatabaseType.H2).build();
        }

        @Bean
        public EmbeddedDatabase embeddedDatabaseFour() {

            return new EmbeddedDatabaseBuilder().setName("FOUR").setType(EmbeddedDatabaseType.H2).build();
        }

        @Bean(name = "defaultDataSource")
        public DataSource defaultDataSource() {
            return embeddedDatabaseOne();
        }

        @Bean
        public Map<String, DataSource> dataSourceLookup() {
            Map<String, DataSource> dataSourceMap = Maps.newHashMap();
            dataSourceMap.put("node1", embeddedDatabaseOne());
            dataSourceMap.put("node2", embeddedDatabaseTwo());
            dataSourceMap.put("node3", embeddedDatabaseThree());
            dataSourceMap.put("node4", embeddedDatabaseFour());

            //
            return dataSourceMap;
        }
    }

}
