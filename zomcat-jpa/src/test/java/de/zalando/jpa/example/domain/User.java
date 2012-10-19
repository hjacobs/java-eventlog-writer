package de.zalando.jpa.example.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Converter;

import de.zalando.jpa.AbstractEntity;
import de.zalando.jpa.types.PGEnumTypeConverter;

@Entity
@Table(name = "users", schema = GlobalIdentifier.SCHEME_ZTEST_SHARD1)
@Converter(name = "pgEnumTypeConverter", converterClass = PGEnumTypeConverter.class)
public class User extends AbstractEntity {

    @Override
    protected Class<? extends AbstractEntity> getEntityClass() {
        return User.class;
    }

    @Column(columnDefinition = "text")
    private String name;

    @Column
    @Convert("pgEnumTypeConverter")
    private UserEnumType userEnumType;

    // with the help of the columnDefinition you can change the enum-type used in postgres:
    @Column(columnDefinition = "other_enum_type_test")
    @Convert("pgEnumTypeConverter")
    private OtherEnumType otherEnumType;

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

    public OtherEnumType getOtherEnumType() {
        return otherEnumType;
    }

    public void setOtherEnumType(final OtherEnumType otherEnumType) {
        this.otherEnumType = otherEnumType;
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
        int result = 0;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((otherEnumType == null) ? 0 : otherEnumType.hashCode());
        result = prime * result + ((userEnumType == null) ? 0 : userEnumType.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final User other = (User) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        if (otherEnumType != other.otherEnumType) {
            return false;
        }

        if (userEnumType != other.userEnumType) {
            return false;
        }

        return true;
    }
}
