package de.zalando.jpa.example.order;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.google.common.base.Objects;

import de.zalando.data.annotation.BusinessKey;

@Entity
@Table(schema = "zzj_data", name = "purchase_order")
@SequenceGenerator(
    name = "purchase_order_id_seq", sequenceName = "zzj_data.purchase_order_po_id_seq", allocationSize = 1
)
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "purchase_order_id_seq")
    private Integer id;

    private String brandCode;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus = OrderStatus.INITIAL;

    @BusinessKey("UUID")
    private String businessKey;

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

    @Version
    private Integer version;

    public String getBrandCode() {
        return brandCode;
    }

    public void setBrandCode(final String brandCode) {
        this.brandCode = brandCode;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(final OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getBusinessKey() {
        return businessKey;
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

    public Integer getVersion() {
        return version;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", getId()).add("brandCode", getBrandCode())
                      .add("orderStatus", getOrderStatus().name()).add("createdBy", getCreatedBy())
                      .add("creationDate", getCreationDate()).add("modificationDate", getModificationDate())
                      .add("modifiedBy", getModifiedBy()).toString();
    }
}
