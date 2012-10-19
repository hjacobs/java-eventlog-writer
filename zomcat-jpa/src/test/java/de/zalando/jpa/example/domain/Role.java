package de.zalando.jpa.example.domain;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.Attribute;

import javax.validation.constraints.NotNull;

import de.zalando.jpa.AbstractEntity;

@Entity
@Table(name = "role", schema = GlobalIdentifier.SCHEME_ZTEST_SHARD1)
public class Role extends AbstractEntity {

    @Override
    protected Class<? extends AbstractEntity> getEntityClass() {
        return Role.class;
    }

    @Column(columnDefinition = "text")
    private String name;

    @ManyToOne(cascade = {}, optional = false)
    @JoinColumn(nullable = false, name = "user_id")
    @NotNull
    private User user;

    @Override
    protected void onPreRemove() {
        super.onPreRemove();

        // when deleting this role, we must remove ourself from all roles in users.
        // to maintain a correct (set of roles) collection at the user's side:
        // removeInverseReference(Role_.user, User_.roles);
        if (this.user != null) {
            removeInverseReference(this, this.user.getRoles());
        }
    }

    /**
     * Fetches the value of the given SingularAttribute on the given entity.
     *
     * @see  http://stackoverflow.com/questions/7077464/how-to-get-singularattribute-mapped-value-of-a-persistent-object
     */
    @SuppressWarnings("unchecked")
    public static <EntityType, FieldType> FieldType getValue(final EntityType entity,
            final Attribute<EntityType, FieldType> field) {
        try {
            final Member member = field.getJavaMember();
            if (member instanceof Method) {

                // this should be a getter method:
                return (FieldType) ((Method) member).invoke(entity);
            } else if (member instanceof Field) {
                return (FieldType) ((Field) member).get(entity);
            } else {
                throw new IllegalArgumentException("Unexpected java member type. Expecting method or field, found: "
                        + member);
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Role() { }

    public Role(final User user, final String name) {

        // first set all ob your busines object (defining the uniqueness of your object)
        this.name = name;

        // last set all relations (that may use this object within collections..)
        setUser(user);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {

        // if we set a new user - this entity may be connected to
        // an other, old user.
        // we need to remove ourself from the old user
        if (this.user != null) {
            removeInverseReference(this, this.user.getRoles());
        }

        this.user = user;
        if (this.user != null) {
            addInverseReference(this, this.user.getRoles());
        }
    }

    /**
     * Define "business-unique-hashcode & equals for every entity"
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        final int result = prime * ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        final Role other = (Role) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        return true;
    }

}
