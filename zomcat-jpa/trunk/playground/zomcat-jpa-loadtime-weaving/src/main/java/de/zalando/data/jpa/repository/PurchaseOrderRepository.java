package de.zalando.data.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.zalando.data.jpa.domain.PurchaseOrder;

/**
 * We only need this repositories. Positions will be accessed always via Order.
 *
 * @author  jbellmann
 */
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {

    PurchaseOrder findByBusinessKey(String businessKey);

}
