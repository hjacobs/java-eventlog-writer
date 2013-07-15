package de.zalando.jpa.example.article;

import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

import org.springframework.data.jpa.auditing.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import org.springframework.transaction.annotation.Transactional;

import de.zalando.jpa.config.JpaConfig;

/**
 * The testcode for integration and unit-test.
 *
 * @author  jbellmann
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@Transactional
public abstract class AbstractArticleTestSupport {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractArticleTestSupport.class);

    public static final String packagesToScan = "de.zalando.jpa.example.article";

    @Autowired
    private ArticleModelRepository articleModelRepository;

    public void doTestSaveArticleModel() {
        ArticleModel articleModel = new ArticleModel();
        articleModelRepository.saveAndFlush(articleModel);
    }

    @Configuration
    @EnableJpaRepositories(AbstractArticleTestSupport.packagesToScan)
    @EnableJpaAuditing
    @Import({ JpaConfig.class })
    @ImportResource({ "classpath:/enableAuditing.xml" })
    static class TestConfig { }
}
