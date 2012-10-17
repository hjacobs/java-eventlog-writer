package de.zalando.jpa.example.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import de.zalando.jpa.EntityManagerContext;
import de.zalando.jpa.example.domain.User;

@Repository
public class UserRepository extends EntityManagerContext {

    public List<User> findAllUsers() {
        return em().createQuery("select u from User u", User.class).getResultList();
    }

    public void save(final User user) {
        em().persist(user);
    }

}
