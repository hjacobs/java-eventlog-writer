package de.zalando.catalog.service;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getFirst;

import static de.zalando.catalog.backend.repository.MultimediaPredicates.idIn;
import static de.zalando.catalog.backend.repository.MultimediaPredicates.transform;

import java.util.List;

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

import com.google.common.collect.Lists;

import de.zalando.catalog.backend.repository.ArticleSkuRepository;
import de.zalando.catalog.backend.repository.MultimediaRepository;
import de.zalando.catalog.domain.ShardedId;
import de.zalando.catalog.domain.multimedia.ArticleSku;
import de.zalando.catalog.domain.multimedia.MediaCharacterCode;
import de.zalando.catalog.domain.multimedia.Multimedia;
import de.zalando.catalog.domain.multimedia.MultimediaTypeCode;
import de.zalando.catalog.price.shard.SkuShardingStrategy;

import de.zalando.jpa.config.ShardedDataSourceConfig;
import de.zalando.jpa.config.ShardedJpaConfig;
import de.zalando.jpa.config.TestProfiles;
import de.zalando.jpa.config.ValueGenerator;
import de.zalando.jpa.config.VendorAdapterDatabaseConfig;
import de.zalando.jpa.eclipselink.partitioning.policies.ShardedObjectPartitionPolicy;

/**
 * An Test to show some examples on how to go with Spring-Data, QueryDSL and Custom-Repository-Implementation.
 *
 * @author  jbellmann
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
@ActiveProfiles(TestProfiles.H2_SHARDED_4)
public class AnotherMultimediaTest {

    private static final Logger LOG = LoggerFactory.getLogger(AnotherMultimediaTest.class);

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
// @Ignore
    public void testProblemWithSavingSku() {

        // this is coming from webservice
        // final String sku = "test1234";
        // it has to be somewhere in the db, right
        ArticleSku articleSku = this.articleSkuList.get(0);
//
        final String sku = articleSku.asString();
        final long id = articleSku.getId();

        // do not say I use the ref above
        articleSku = null;

        // this should be done automatically!
        // final ArticleSku articleSku = articleSkuRepository.findOneBySku(sku);

        final Multimedia multimedia = new Multimedia(ShardedId.of(id));
        multimedia.setPath("/test/path/model/" + id);
        multimedia.setTypeCode(new MultimediaTypeCode("JPEG_IMAGE"));
        multimedia.setMediaCharacterCode(new MediaCharacterCode("IMAGE"));

        // to make sure, the repository will set it somehow
        Assert.assertNull(multimedia.getSku());

        // multimedia.setSku(articleSku);

        Multimedia saved = multimediaRepository.save(multimedia, sku);
        Assert.assertNotNull(saved.getSku());
        Assert.assertNotNull(saved.getShardKey());
    }

    @Test
    public void readArticleSkus() {
        final List<ArticleSku> resultArticleSkuList = this.articleSkuRepository.findAll();
        Assert.assertNotNull(resultArticleSkuList);
        Assert.assertFalse(resultArticleSkuList.isEmpty());
        Assert.assertEquals(this.articleSkuList.size(), resultArticleSkuList.size());

        LOG.info("---------------------------------------");
        printArticleSkus(this.articleSkuList);

        LOG.info("---------------------------------------");
        printArticleSkus(resultArticleSkuList);

        LOG.info("---------------------------------------");
        for (final ArticleSku r : resultArticleSkuList) {
            final FindBySku predicate = new FindBySku(r.asString());
            final ArticleSku orig = getFirst(filter(this.articleSkuList, predicate), null);
            Assert.assertNotNull(orig);
            LOG.info("TRY_EQUALS : with orig: {} and fromDB: {}", orig, r);

            // TODO, this should be inspected on the original zeos-catalog code
            // equals-method is not correct implemented
// Assert.assertEquals(orig, r);
        }

        LOG.info("---------------------------------------");

        LOG.info("--------------WRITE MULTIMEDIA-------------------------");

        final List<ShardedId> codes = Lists.newArrayList();

        for (final ArticleSku r : resultArticleSkuList) {

            codes.add(ShardedId.of(r.getId()));

            final Multimedia multimedia = new Multimedia(ShardedId.of(r.getId()));
            multimedia.setPath("/test/path/model/" + r.getId());
            multimedia.setTypeCode(new MultimediaTypeCode("JPEG_IMAGE"));
            multimedia.setMediaCharacterCode(new MediaCharacterCode("IMAGE"));

            multimedia.setSku(r);
            this.multimediaRepository.saveAndFlush(multimedia);
        }

        LOG.info("--------------LIST MULTIMEDIA START-------------------------");

        LOG.info("--------------LIST MULTIMEDIA - SINGLE CALL -------------------------");

        for (final ShardedId si : codes) {

            final Multimedia m = this.multimediaRepository.findOne(si.asLong());

            LOG.info("FOUND : {}", m.toString());
        }

        LOG.info("--------------LIST MULTIMEDIA - ITERABLE CALL -------------------------");

        List<Multimedia> multiMediaResult = null;

        // Variante 1, Predicates + QueryDSL, seems to work
        final Iterable<Multimedia> result = this.multimediaRepository.findAll(idIn(transform(codes)));

        multiMediaResult = extracts(result);

        // VARIANTE 2, CustomImplementation, does not work
        // multiMediaResult = this.multimediaRepository.findByCodes(codes);

        for (final Multimedia m : multiMediaResult) {
            LOG.info("MM : {}", m.toString());
        }

        LOG.info("--------------LIST MULTIMEDIA END-------------------------");

    }

    /**
     * Just to see something on console. Just believe what you can see. ;-)
     *
     * @param  skus
     */
    private static void printArticleSkus(final List<ArticleSku> skus) {

        for (final ArticleSku s : skus) {
            LOG.info("ARTICLESKU:  {}", s);
        }

    }

    /**
     * To fetch {@link Multimedia}s from the Iterable.
     *
     * @param   iterable
     *
     * @return
     */
    private List<Multimedia> extracts(final Iterable<Multimedia> iterable) {

        final List<Multimedia> result = Lists.newArrayList();

        for (final Multimedia m : iterable) {
            result.add(m);
        }

        return result;
    }

    @Configuration
    @EnableJpaRepositories("de.zalando.catalog.backend.repository")
    @EnableJpaAuditing
    @Import({ ShardedJpaConfig.class, ShardedDataSourceConfig.class, VendorAdapterDatabaseConfig.class })
    static class TestConfig {

        @Bean
        public PartitioningPolicy skuShardingPolicy() {
            final ShardedObjectPartitionPolicy sopp = new ShardedObjectPartitionPolicy();
            sopp.setShardingStrategy(new SkuShardingStrategy());
            return sopp;
        }
    }
}
