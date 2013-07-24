package de.zalando.data.jpa.repository.sample;

import org.springframework.data.jpa.repository.JpaRepository;

import de.zalando.data.jpa.domain.sample.AnnotatedProduct;

/**
 * Example-Repository.
 *
 * @author  jbellmann
 */
public interface AnnotatedKeyableProductRepository extends JpaRepository<AnnotatedProduct, Integer> { }
