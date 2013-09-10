package de.zalando.data.jpa.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Objects;

@Entity
@Table(name = "purchase_order_position")
public class PurchaseOrderPosition implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "purchase_order_position_id_seq")
    private Integer id;

    private int quantity;

    private String productNumber;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(final int quantity) {
        this.quantity = quantity;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(final String productNumber) {
        this.productNumber = productNumber;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", id).add("quantity", getQuantity())
                      .add("productnumber", getProductNumber()).toString();
    }

}
