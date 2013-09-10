package de.zalando.jpa.example.article;

import org.junit.Test;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;

/**
 * @author  jbellmann
 */
@DirtiesContext
public class ArticleIT extends AbstractArticleTestSupport {

    @Test
    @Rollback(false)
    public void testSaveArticleModel() {
        super.doTestSaveArticleModel();
    }
}
