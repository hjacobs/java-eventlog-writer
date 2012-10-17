package de.zalando.jpa.example.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;

import de.zalando.jpa.AbstractEntity;

@Entity
@Table(name = "role", schema = GlobalIdentifier.SCHEME_PUBLIC)
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
        this.user = user;
        user.getRoles().add(this);
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
