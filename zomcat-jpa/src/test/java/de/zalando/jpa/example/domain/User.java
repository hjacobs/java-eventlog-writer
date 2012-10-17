package de.zalando.jpa.example.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import de.zalando.jpa.AbstractEntity;

@Entity
@Table(name = "users", schema = GlobalIdentifier.SCHEME_PUBLIC)
public class User extends AbstractEntity {

    @Override
    protected Class<? extends AbstractEntity> getEntityClass() {
        return User.class;
    }

    @Column(columnDefinition = "text")
    private String name;

    /*
     * @Type( type = PGEnumType.TYPE, parameters = {@Parameter(name =
     * PGEnumType.PARAM, value = "de.zalando.jpa.example.domain.UserEnumType")}
     * )
     */

    @Column(columnDefinition = "text")
    @Enumerated(EnumType.STRING)
    private UserEnumType userEnumType;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Role> roles = new HashSet<Role>();

    public User() { }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public UserEnumType getUserEnumType() {
        return userEnumType;
    }

    public void setUserEnumType(final UserEnumType userEnumType) {
        this.userEnumType = userEnumType;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void addRole(final Role role) {
        role.setUser(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime * ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((userEnumType == null) ? 0 : userEnumType.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        final User other = (User) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        if (userEnumType != other.userEnumType) {
            return false;
        }

        return true;
    }

}
