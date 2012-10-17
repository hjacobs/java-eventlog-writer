package de.zalando.jpa.example.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import javax.validation.constraints.NotNull;

@Entity
@Table(name = "role", schema = GlobalIdentifier.SCHEME_PUBLIC)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "r_id")
    private Long id;

    @Column(name = "r_name", columnDefinition = "TEXT")
    private String name;

    @ManyToOne(cascade = {}, optional = false)
    @JoinColumn(name = "r_user_id", nullable = false)
    @NotNull
    private User user;

    public Role() { }

    public Role(final User user, final String name) {
        setUser(user);
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
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
}
