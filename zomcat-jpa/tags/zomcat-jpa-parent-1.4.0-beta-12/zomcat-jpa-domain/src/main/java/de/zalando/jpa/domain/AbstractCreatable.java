/**
 * Marcel Wieczorek
 * Zalando GmbH
 * Nov 26, 2012 5:12:12 PM
 */
package de.zalando.jpa.domain;

import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author  <a href="mailto:marcel.wieczorek@zalando.de" title="Marcel Wieczorek">mwieczorek</a>
 */
@MappedSuperclass
public abstract class AbstractCreatable {

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    protected Date created = new Date();

    @NotEmpty
    protected String createdBy;

    /**
     * @return  the created
     */
    public Date getCreated() {
        return this.created;
    }

    /**
     * @param  created  the created to set
     */
    public void setCreated(final Date created) {
        this.created = created;
    }

    /**
     * @return  the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param  createdBy  the createdBy to set
     */
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy == null ? null : createdBy.trim();
    }

}
