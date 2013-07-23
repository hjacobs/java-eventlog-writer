/**
 * Marcel Wieczorek
 * Zalando GmbH
 * Nov 26, 2012 5:14:50 PM
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
public abstract class AbstractModifiable extends AbstractCreatable {

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    protected Date lastModified;

    @NotEmpty
    protected String lastModifiedBy;

    /**
     * @return  the lastModified
     */
    public Date getLastModified() {
        return this.lastModified;
    }

    /**
     * @param  lastModified  the lastModified to set
     */
    public void setLastModified(final Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @return  the lastModifiedBy
     */
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     * @param  lastModifiedBy  the lastModifiedBy to set
     */
    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy == null ? null : lastModifiedBy.trim();
    }

}
