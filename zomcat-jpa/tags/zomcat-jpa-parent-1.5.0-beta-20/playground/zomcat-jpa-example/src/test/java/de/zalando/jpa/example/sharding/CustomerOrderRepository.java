package de.zalando.jpa.example.sharding;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author  jbellmann
 */
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long>, CustomerOrderRepositoryCustom { }
