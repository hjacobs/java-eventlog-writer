package de.zalando.jpa.example.article;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleModelRepository extends JpaRepository<ArticleModel, ArticleSku> { }
