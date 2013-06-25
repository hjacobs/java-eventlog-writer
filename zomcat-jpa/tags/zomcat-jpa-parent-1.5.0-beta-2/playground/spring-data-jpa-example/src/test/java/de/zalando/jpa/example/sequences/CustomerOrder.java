package de.zalando.jpa.example.sequences;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.google.common.collect.Lists;

/**
 * @author  jbellmann
 */
@Entity
@Table(name = "customerOrder")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_order_id_seq")
    @SequenceGenerator(name = "customer_order_id_seq", sequenceName = "customer_order_id_seq", allocationSize = 1)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "customerOrder", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderLine> orderLines = Lists.newArrayList();

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void addOrderLine(final OrderLine orderLine) {
        orderLine.setCustomerOrder(this);
        this.orderLines.add(orderLine);
    }

    public void setOrderLines(final List<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public Long getId() {
        return id;
    }

}
