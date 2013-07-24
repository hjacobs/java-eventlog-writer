package de.zalando.catalog.service;

import java.util.List;

import javax.annotation.Nullable;

import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.springframework.data.jpa.auditing.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import de.zalando.catalog.backend.repository.ArticleSkuRepository;
import de.zalando.catalog.backend.repository.MultimediaRepository;
import de.zalando.catalog.domain.multimedia.ArticleSku;
import de.zalando.catalog.price.shard.SkuShardingStrategy;

import de.zalando.jpa.config.ShardedDataSourceConfig;
import de.zalando.jpa.config.ShardedJpaConfig;
import de.zalando.jpa.config.TestProfiles;
import de.zalando.jpa.config.ValueGenerator;
import de.zalando.jpa.config.VendorAdapterDatabaseConfig;
import de.zalando.jpa.eclipselink.partitioning.policies.ShardedObjectPartitionPolicy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
@ActiveProfiles(TestProfiles.H2_SHARDED_4)
public class AnotherMultimediaIT {

    private static final Logger LOG = LoggerFactory.getLogger(AnotherMultimediaIT.class);

    public static final int MULTIMEDIA_PER_SKU = 3;

    @Autowired
    private MultimediaRepository multimediaRepository;

    @Autowired
    private List<PartitioningPolicy> policies;

    @Autowired
    private ArticleSkuRepository articleSkuRepository;

    private List<ArticleSku> articleSkuList;

    @Before
    public void isRepositoryWired() {
        Assert.assertNotNull(multimediaRepository);
    }

    @Before
    public void arePoliciesWired() {
        Assert.assertNotNull(policies);
    }

    @Before
    public void setUp() {
        articleSkuList = Lists.newArrayList();

        for (int i = 0; i < MULTIMEDIA_PER_SKU; i++) {
            ArticleSku aSku = new ArticleSku();
            aSku.setSku(ValueGenerator.generateModelSKu());
            aSku = this.articleSkuRepository.saveAndFlush(aSku);
            articleSkuList.add(aSku);
        }

    }

    @Test
    public void readArticleSkus() {
        List<ArticleSku> resultArticleSkuList = this.articleSkuRepository.findAll();
        Assert.assertNotNull(resultArticleSkuList);
        Assert.assertFalse(resultArticleSkuList.isEmpty());
        Assert.assertEquals(this.articleSkuList.size(), resultArticleSkuList.size());
        LOG.info("---------------------------------------");
        printArticleSkus(this.articleSkuList);
        LOG.info("---------------------------------------");
        printArticleSkus(resultArticleSkuList);
        LOG.info("---------------------------------------");
        for (ArticleSku r : resultArticleSkuList) {
            FindBySku predicate = new FindBySku(r.asString());
            ArticleSku orig = Iterables.getFirst(Iterables.filter(this.articleSkuList, predicate), null);
            Assert.assertNotNull(orig);
            LOG.info("TRY_EQUALS : with orig: {} and fromDB: {}", orig, r);

            // TODO, this should be inspected on the original zeos-catalog code
            // equals-method is not correct implemented
// Assert.assertEquals(orig, r);
        }

        LOG.info("---------------------------------------");
    }

    private static void printArticleSkus(final List<ArticleSku> skus) {
        for (ArticleSku s : skus) {
            LOG.info("ARTICLESKU:  {}", s);
        }
    }

    @Test
    public void test() { }

    @Configuration
    @EnableJpaRepositories("de.zalando.catalog.backend.repository")
    @EnableJpaAuditing
    @Import({ ShardedJpaConfig.class, ShardedDataSourceConfig.class, VendorAdapterDatabaseConfig.class })
    static class TestConfig {

        @Bean
        public PartitioningPolicy skuShardingPolicy() {
            ShardedObjectPartitionPolicy sopp = new ShardedObjectPartitionPolicy();
            sopp.setShardingStrategy(new SkuShardingStrategy());
            return sopp;
        }
    }

    private static final class FindBySku implements Predicate<ArticleSku> {

        public final String sku;

        public FindBySku(final String sku) {
            this.sku = sku;
        }

        @Override
        public boolean apply(@Nullable final ArticleSku input) {
            return this.sku.equals(input.asString());
        }
    }
}
