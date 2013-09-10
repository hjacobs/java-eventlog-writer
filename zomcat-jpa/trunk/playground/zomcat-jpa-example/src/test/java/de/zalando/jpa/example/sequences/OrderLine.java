package de.zalando.jpa.example.sequences;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;

@Entity
@Table(name = "sequences_order_line")
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequences_order_line_id_seq")
    @SequenceGenerator(
        name = "sequences_order_line_id_seq", sequenceName = "sequences_order_line_id_seq", allocationSize = 100
    )
    private Long id;

    @NotNull
    @ManyToOne
    private CustomerOrder customerOrder;

    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setCustomerOrder(final CustomerOrder customerOrder) {
        this.customerOrder = customerOrder;
    }

}
