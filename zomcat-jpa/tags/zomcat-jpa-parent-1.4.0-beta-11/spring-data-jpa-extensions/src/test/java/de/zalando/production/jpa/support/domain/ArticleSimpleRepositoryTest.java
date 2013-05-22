package de.zalando.production.jpa.support.domain;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.transaction.annotation.Transactional;

import de.zalando.data.jpa.domain.support.SkuIdGenerator;
import de.zalando.data.jpa.domain.support.SkuIdHandler;

import de.zalando.production.jpa.config.JpaConfig;

/**
 * Author: clohmann Date: 06.05.13 Time: 18:21
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
@ActiveProfiles("HSQL")
public class ArticleSimpleRepositoryTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ArticleSimpleRepository articleSimpleRepository;

    @Autowired
    private SkuIdHandler skuIdHandler;

    @Autowired
    private SkuIdGenerator skuIdGenerator;

    @Before
    public void setUp() {
        Assert.assertNotNull(dataSource);
        Assert.assertNotNull(articleSimpleRepository);
        Assert.assertNotNull(skuIdHandler);
        Assert.assertNotNull(skuIdGenerator);
    }

    @Test
    public void testPersist() {
        ArticleSimple articleSimple = new ArticleSimple();

        //
        articleSimple = articleSimpleRepository.saveAndFlush(articleSimple);
        Assert.assertNotNull(articleSimple.id);
        Assert.assertTrue(articleSimple.id < 0);
    }

    @Configuration
    @Import(JpaConfig.class)
    @ImportResource("classpath:/skuRepo.xml")
    static class TestConfig { }
}
