package de.zalando.jpa.example.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users", schema = GlobalIdentifier.SCHEME_PUBLIC)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "u_id")
    private Long id;

    @Column(name = "u_name")
    private String name;

    /*
     * @Type( type = PGEnumType.TYPE, parameters = {@Parameter(name =
     * PGEnumType.PARAM, value = "de.zalando.jpa.example.domain.UserEnumType")}
     * )
     */

    @Column(name = "u_user_enum_type")
    @Enumerated
    private UserEnumType userEnumType;

    @OneToMany(cascade = {CascadeType.ALL}, mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Role> roles = new HashSet<Role>();

    public User() { }

    public Long getId() {
        return id;
    }

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

}
