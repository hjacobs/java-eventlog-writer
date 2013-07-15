package de.zalando.jpa.example.identity;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.google.common.collect.Lists;

/**
 * @author  jbellmann
 */
@Entity
@Table(name = "customer_order")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // important senseless field, otherwise we got an [EclipseLink-6023] error
    private int nothing = 6;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customerOrder", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderLine> orderLines = Lists.newArrayList();

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void addOrderLine(final OrderLine orderLine) {
        orderLine.setCustomerOrder(this);
        this.orderLines.add(orderLine);
    }

}
