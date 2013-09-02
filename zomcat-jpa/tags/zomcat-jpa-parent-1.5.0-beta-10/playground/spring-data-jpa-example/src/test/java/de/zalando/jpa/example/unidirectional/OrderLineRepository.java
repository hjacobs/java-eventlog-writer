package de.zalando.jpa.example.unidirectional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author  jbellmann
 */
public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {

    List<OrderLine> findByCustomerOrder(final CustomerOrder customerOrder);

}
