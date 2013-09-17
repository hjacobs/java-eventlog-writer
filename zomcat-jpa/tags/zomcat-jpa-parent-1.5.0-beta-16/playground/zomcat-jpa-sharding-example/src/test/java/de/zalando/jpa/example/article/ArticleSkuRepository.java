package de.zalando.jpa.example.article;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author  jbellmann
 */
public interface ArticleSkuRepository extends JpaRepository<ArticleSku, Long> { }
