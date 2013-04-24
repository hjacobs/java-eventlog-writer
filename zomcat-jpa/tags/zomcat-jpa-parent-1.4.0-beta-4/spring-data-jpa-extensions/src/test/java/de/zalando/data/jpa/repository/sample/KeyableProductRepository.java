package de.zalando.data.jpa.repository.sample;

import org.springframework.data.jpa.repository.JpaRepository;

import de.zalando.data.jpa.domain.sample.BusinessKeyAwareProduct;

/**
 * Example-Repository.
 *
 * @author  jbellmann
 */
public interface KeyableProductRepository extends JpaRepository<BusinessKeyAwareProduct, Integer> { }
