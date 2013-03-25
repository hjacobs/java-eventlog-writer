package de.zalando.data.jpa.domain.sample;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

import de.zalando.data.annotation.BusinessKey;

@Entity
public class AnnotatedProduct extends AbstractPersistable<Integer> {

    private static final long serialVersionUID = 1L;

    @BusinessKey("BusinessKeyProduct")
    private String businessKey;

    @CreatedBy
    private String creator;

    @LastModifiedBy
    private String auditor;

    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date lastModified;

    public String getBusinessKey() {
        return businessKey;
    }

    public String getCreatedBy() {
        return creator;
    }

    public String getLastModifiedBy() {
        return auditor;
    }

    public Date getCreated() {
        return created;
    }

    public Date getLastModified() {
        return lastModified;
    }
}
