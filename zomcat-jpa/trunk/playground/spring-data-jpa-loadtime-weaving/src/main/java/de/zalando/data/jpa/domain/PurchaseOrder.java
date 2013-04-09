package de.zalando.data.jpa.domain;

import java.io.Serializable;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import de.zalando.data.annotation.BusinessKey;

@Entity
@Table(name = "purchase_order")
public class PurchaseOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "purchase_order_id_seq")
    private Integer id;

    @BusinessKey("UUID")
    private String businessKey;

    @OneToMany
    private List<PurchaseOrderPosition> positions = Lists.newArrayList();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_invoice_address_id")
    private InvoiceAddress invoiceAdress;

    public String getBusinessKey() {
        return businessKey;
    }

    public List<PurchaseOrderPosition> getPositions() {
        return positions;
    }

    public void setPositions(final List<PurchaseOrderPosition> positions) {
        this.positions = positions;
    }

    public InvoiceAddress getInvoiceAdress() {
        return invoiceAdress;
    }

    public void setInvoiceAdress(final InvoiceAddress invoiceAdress) {
        this.invoiceAdress = invoiceAdress;
    }

    public String toString() {
        return Objects.toStringHelper(this).add("id", id).add("businesskey", getBusinessKey()).toString();
    }

}