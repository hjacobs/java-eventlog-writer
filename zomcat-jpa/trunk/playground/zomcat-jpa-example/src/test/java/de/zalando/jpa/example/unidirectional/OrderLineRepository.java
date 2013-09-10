package de.zalando.jpa.example.unidirectional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author  jbellmann
 */
public interface OrderLineRepository extends JpaRepository<OrderLine, Long> {

    // Please do not name this query 'findByCustomerOrder', that will fail
    List<OrderLine> findByCustomerorder(final CustomerOrder customerOrder);

}
