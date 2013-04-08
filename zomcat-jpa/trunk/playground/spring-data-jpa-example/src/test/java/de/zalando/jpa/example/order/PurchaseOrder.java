package de.zalando.jpa.example.order;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.google.common.base.Objects;

import de.zalando.data.annotation.BusinessKey;

@SuppressWarnings("serial")
@Entity
@Table(name = "purchase_order")
public class PurchaseOrder extends AbstractPersistable<Integer> {

    private String brandCode;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

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

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", getId()).add("brandCode", getBrandCode())
                      .add("orderStatus", getOrderStatus().name()).add("createdBy", getCreatedBy())
                      .add("creationDate", getCreationDate()).add("modificationDate", getModificationDate())
                      .add("modifiedBy", getModifiedBy()).toString();
    }
}
