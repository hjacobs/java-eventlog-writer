package de.zalando.jpa;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import de.zalando.jpa.utils.DefensiveCopy;

@MappedSuperclass
public abstract class AbstractEntity {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractEntity.class);

    /*
     * replace this by an abstract function so that each entity class must implement the correct getter.
     */
    protected abstract Class<? extends AbstractEntity> getEntityClass();

    // getter for the typename of this entity.
    public String getEntityTypename() {
        return getEntityClass().getCanonicalName();
    }

    protected AbstractEntity() {
        setModificationDateAutomatically = true;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    // do you need optimitic locking support?
    @Version
    @Column
    private Integer version;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false, updatable = false)
    @NotNull
    private Date creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "modification_date", nullable = false)
    @NotNull
    private Date modificationDate;

    @Transient
    private boolean persistent = false;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public Date getCreationDate() {
        return DefensiveCopy.ofDate(creationDate);
    }

    public void setCreationDate(final Date creationDate) {
        this.creationDate = DefensiveCopy.ofDate(creationDate);
    }

    public Date getModificationDate() {
        return DefensiveCopy.ofDate(modificationDate);
    }

    public void setModificationDate(final Date modificationDate) {
        this.modificationDate = DefensiveCopy.ofDate(modificationDate);
        this.setModificationDateAutomatically = false;
    }

    public boolean isPersistent() {
        return this.persistent;
    }

    /**
     * Helper flag indicating if this entity is marked for deletion.
     */
    @Transient
    private boolean wasRemoved;

    protected boolean getIsRemoving() {
        return this.wasRemoved;
    }

    /**
     * Helper flag indicating if modification date should be set automatically.
     */
    @Transient
    private boolean setModificationDateAutomatically;

    /**
     * onPrePersist pipes the @PrePersist annotation through the hierarchy of objects. Override this function to
     * maintain the correct order of calls.
     */
    protected void onPrePersist() { }

    /**
     * onPreUpdate pipes the @PreUpdate annotation through the hierarchy of objects. Override this function to maintain
     * the correct order of calls.
     */
    protected void onPreUpdate() { }

    /**
     * onPostLoad pipes the @PostLoad annotation through the hierarchy of objects. Override this function to maintain
     * the correct order of calls.
     */
    protected void onPostLoad() { }

    /**
     * onPostLoad pipes the @PreRemove annotation through the hierarchy of objects. Override this function to maintain
     * the correct order of calls.
     */
    protected void onPreRemove() { }

    // life cycle callbacks

    @PrePersist
    public void prePersist() {
        onPrePersist();

        final Date currentDate = new Date();
        if (this.creationDate == null) {
            this.creationDate = currentDate;
        }

        if (setModificationDateAutomatically == true) {
            this.modificationDate = currentDate;
        }
    }

    @PostPersist
    public void postPersist() {
        this.persistent = true;
    }

    @PreUpdate
    public void preUpdate() {
        onPreUpdate();
        if (setModificationDateAutomatically == true) {
            this.modificationDate = new Date();
        }
    }

    @PostLoad
    public void initializeAbstractPeristent() {
        onPostLoad();
        wasRemoved = false;
        this.persistent = true;
    }

    @PreRemove
    public void onDeleteAbstractPeristent() {
        onPreRemove();
        wasRemoved = true;
    }

    // ------ Collection helper functions ------
    // -
/*
 *  protected <EntityType, FieldType> void removeInverseReference(final Attribute<EntityType, FieldType> member,
 *          final Attribute<FieldType, EntityType> inverse) {
 *      final EntityType localMember = getValue(this, member);
 *      if (localMember != null) {
 *          final FieldType inverseCollection = getValue(localMember, inverse);
 *          if (inverseCollection != null) {
 *              removeInverseReference(localMember, inverseCollection);
 *          }
 *      }
 *
 *  }
 *
 *  protected <EntityType, FieldType> FieldType getValue(final EntityType entity,
 *          final Attribute<EntityType, FieldType> setType) {
 *      try {
 *          final Member member = setType.getJavaMember();
 *          if (member instanceof Method) {
 *
 *              // this should be a getter method:
 *              return (FieldType) ((Method) member).invoke(entity);
 *          } else if (member instanceof Field) {
 *              return (FieldType) ((Field) member).get(entity);
 *          } else {
 *              throw new IllegalArgumentException("Unexpected java member type. Expecting method or field, found: "
 *                      + member);
 *          }
 *      } catch (final Exception e) {
 *          throw new RuntimeException(e);
 *      }
 *  }
 */
    /**
     * Remove 'this' from the collection of the argument object, assuming the collection is the non-owning side of a
     * bi-directional reference. Only updates the collection if needed (loaded in memory.)
     */
    protected void removeInverseReference(final AbstractEntity persistent, final Collection<?> collection) {
        if (persistent.getIsRemoving() == false) {
            if (!collection.remove(persistent)) {
                LOG.debug("persistent entity not found in inverse relation");
            }
        }
    }

    // --------------------------------------------------------------------------
    // -
    /**
     * Add this to the collection of the argument object, assuming the collection is the non-owning side of a
     * bi-directional reference. Only updates the collection if needed (loaded in memory.)
     */
    @SuppressWarnings("unchecked")
    protected void addInverseReference(final AbstractEntity persistent,
            @SuppressWarnings("rawtypes") final Collection collection) {
        if (persistent.getIsRemoving() == false) {
            collection.add(persistent);
        }
    }

    /**
     * Hashcode based on id and class -> logging an error while the primary key is NULL. Hashcode & Equals have some
     * difficulties in the persistence life cycle. for reference see https://community.jboss.org/wiki/EqualsAndHashCode
     */

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        // If we have an id, use it's hash
        if (getId() != null) {
            result = prime * getId().hashCode();
            result = prime * result + getEntityClass().hashCode();
        } else {
            LOG.error("You are using the current entity hashcode without prior persiting. "
                    + "This may lead to an unexpected behaviour if used in collections."
                    + "Try to persist the entity before the current action.");

            result = System.identityHashCode(this);
        }

        return result;
    }

    /**
     * Implementation that compares two instances based on the primary key. Hashcode & Equals have some difficulties in
     * the persistence life cycle. for reference see https://community.jboss.org/wiki/EqualsAndHashCode
     *
     * @param   obj  The object to compare to
     *
     * @return  true if the objects are equal
     */
    @Override
    public boolean equals(final Object obj) {

        // TODO not consistent with hashCode()!!! Arggghhh! - but there is no "common solution for this.
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        // Our type?
        if (obj instanceof AbstractEntity) {
            Preconditions.checkArgument(this.getId() != null, "entity.id cannot be null.");

            // Cast
            final AbstractEntity other = (AbstractEntity) obj;

            // Equality means "same id" and same class
            if (this.getEntityClass().equals(other.getEntityClass()) && this.getId().equals(other.getId())) {
                return true;
            }
        }

        // Not equal!
        return false;
    }
}
