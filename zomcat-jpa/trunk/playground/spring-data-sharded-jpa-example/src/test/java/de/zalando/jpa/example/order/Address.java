package de.zalando.jpa.example.order;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import javax.validation.constraints.NotNull;

import org.eclipse.persistence.annotations.ReplicationPartitioning;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.google.common.base.Objects;

@Entity
@Table(name = "purchase_order_address", schema = "zzj_data")
@ReplicationPartitioning(name = "Replicate", connectionPools = {"default", "node2"})
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    private String street;

    @Version
    private Integer version;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @CreatedBy
    private String createdBy;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationDate;

    @LastModifiedBy
    private String modifiedBy;

    @NotNull
    @OneToOne(mappedBy = "address")
    private PurchaseOrder purchaseOrder;

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public Integer getVersion() {
        return version;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", getId()).add("version", getVersion()).toString();
    }

    public void setPurchaseOrder(final PurchaseOrder purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }
}
