package de.zalando.jpa.example.unidirectional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author  jbellmann
 */
@Entity
@Table(name = "unidir_customer_order")
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unidir_customer_order_id_seq")
    @SequenceGenerator(
        name = "unidir_customer_order_id_seq", sequenceName = "unidir_customer_order_id_seq", allocationSize = 1
    )
    private Long id;

    public Long getId() {
        return id;
    }

}
