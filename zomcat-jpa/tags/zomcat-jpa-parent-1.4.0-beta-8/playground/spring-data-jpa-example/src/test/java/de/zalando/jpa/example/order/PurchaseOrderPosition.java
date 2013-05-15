package de.zalando.jpa.example.order;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import javax.validation.constraints.NotNull;

import com.google.common.base.Objects;

@SuppressWarnings("serial")
@Entity
@Table(name = "purchase_order_position")
// @SequenceGenerator(
// name = "purchase_order_position_id_seq", sequenceName = "purchase_order_pos_id_seq", allocationSize = 1
// )
public class PurchaseOrderPosition implements Serializable {

    @Id
// @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchase_order_position_id_seq")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Version
    private Integer version;

    private long quanity = 5;

    private String comment = "Comment";

    @NotNull
    @JoinColumn(name = "po_id")
    @ManyToOne(cascade = CascadeType.REFRESH)
    private PurchaseOrder purchaseOrder;

    public PurchaseOrderPosition() { }

    public PurchaseOrderPosition(final PurchaseOrder po) {
        this.purchaseOrder = po;
    }

    public long getQuanity() {
        return quanity;
    }

    public void setQuanity(final long quanity) {
        this.quanity = quanity;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public Integer getVersion() {
        return version;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", getId()).add("version", getVersion()).toString();
    }
}
