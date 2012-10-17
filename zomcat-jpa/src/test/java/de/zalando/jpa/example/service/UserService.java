package de.zalando.jpa.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import de.zalando.jpa.example.domain.User;
import de.zalando.jpa.example.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepositry;

    public List<User> findAllUsers() {
        return userRepositry.findAllUsers();
    }

    @Transactional
    public void register(final User user) {
        userRepositry.save(user);
    }
}
