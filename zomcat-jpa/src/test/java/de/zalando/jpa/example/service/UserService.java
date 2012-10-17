package de.zalando.jpa.example.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import de.zalando.jpa.example.domain.User;
import de.zalando.jpa.example.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> findAllUsers() {
        return userRepository.findAllUsers();
    }

    public void register(final User user) {
        userRepository.save(user);
    }
}
