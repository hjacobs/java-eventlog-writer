package de.zalando.jpa.example.auditing;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.AbstractPersistable;

import com.google.common.base.Objects;

@Entity
public class Account extends AbstractPersistable<Long> {

    private static final long serialVersionUID = 1L;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
// @NotNull // if this is set, eclipselink will not allow this
    @Column(updatable = false, nullable = false) // if we set nullable=false, the database will not allow this
// @Column(nullable = false)
    private Date created;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;

    public Date getCreated() {
        return created;
    }

    public Date getLastModified() {
        return lastModified;
    }

    // this is a bad thing, there is a good reason why setId() is protected.
    // but some people want to use it, maybe the wrong way ? ;-)
    @Override
    public void setId(final Long id) {
        super.setId(id);
    }

    @Override
    public String toString() {

        return Objects.toStringHelper(this).add("id", getId()).add("created", getCreated())
                      .add("lastModified", getLastModified()).toString();
    }

}
