package de.zalando.jpa.example.order;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.Partitioned;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import de.zalando.data.annotation.BusinessKey;

@Entity
@Table(name = "purchase_order", schema = "zzj_data")
@Partitioned("Replicate")
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "purchaseOrder", orphanRemoval = true)
    private List<PurchaseOrderPosition> positions = Lists.newArrayList();

    @OneToOne
    private Address address;

    private String fieldWithoutAnnotation;

    @Column(name = "field_with_annotation")
    private String fieldwithannotation;

    private boolean ordered;

    @Column(name = "is_canceled")
    private boolean canceled;

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

    public List<PurchaseOrderPosition> getPositions() {
        return positions;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(final Address address) {
        this.address = address;
        this.address.setPurchaseOrder(this);
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(final boolean ordered) {
        this.ordered = ordered;
    }

    public String getFieldWithoutAnnotation() {
        return fieldWithoutAnnotation;
    }

    public void setFieldWithoutAnnotation(final String fieldWithoutAnnotation) {
        this.fieldWithoutAnnotation = fieldWithoutAnnotation;
    }

    public String getFieldwithannotation() {
        return fieldwithannotation;
    }

    public void setFieldwithannotation(final String fieldwithannotation) {
        this.fieldwithannotation = fieldwithannotation;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", getId()).add("brandCode", getBrandCode())
                      .add("orderStatus", getOrderStatus().name()).add("createdBy", getCreatedBy())
                      .add("creationDate", getCreationDate()).add("modificationDate", getModificationDate())
                      .add("modifiedBy", getModifiedBy()).add("version", getVersion()).toString();
    }
}
