package de.zalando.jpa.example.autoid;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author  jbellmann
 */
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> { }
