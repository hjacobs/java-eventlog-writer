package de.zalando.jpa.example.sharding;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;

import org.eclipse.persistence.annotations.Partitioned;

@Entity
@Table(name = "order_Line")
@Partitioned("Replicate")
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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