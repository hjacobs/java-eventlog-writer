package de.zalando.catalog.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.zalando.catalog.domain.multimedia.ArticleSku;

public interface ArticleSkuRepository extends JpaRepository<ArticleSku, Long> { }
