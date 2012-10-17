package de.zalando.jpa.example.test;

import java.util.List;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import org.springframework.transaction.annotation.Transactional;

import de.zalando.jpa.example.domain.Role;
import de.zalando.jpa.example.domain.User;
import de.zalando.jpa.example.domain.UserEnumType;
import de.zalando.jpa.example.service.UserService;

import junit.framework.Assert;

@TransactionConfiguration(defaultRollback = true)
@ContextConfiguration({ "classpath:context.xml" })
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceTestIT {

    @Autowired
    UserService userService;

    @Test
    public void registerUser() {
        final User user = new User();
        user.setName("bubu");
        user.setUserEnumType(UserEnumType.VIP);
        userService.register(user);

        final List<User> allUsers = userService.findAllUsers();
        final User savedUser = allUsers.get(0);
        Assert.assertEquals("There should be one saved user", 1, allUsers.size());
        Assert.assertEquals("User should be equal to saved user", user, savedUser);
        Assert.assertEquals("Enum type check", user.getUserEnumType(), UserEnumType.VIP);
    }

    @Test
    public void registerUserWithRoles() {
        final User user = new User();

        final Role roleAdmin = new Role(user, "admin");
        final Role roleUser = new Role(user, "user");

        user.setName("bubu");

        userService.register(user);

        final List<User> allUsers = userService.findAllUsers();
        final User savedUser = allUsers.get(0);

        Assert.assertEquals("The user should be equal to saved user", user, savedUser);

        Assert.assertEquals("The user should have 2 roles", 2, savedUser.getRoles().size());
        Assert.assertTrue("The user should have role user", savedUser.getRoles().contains(roleUser));
        Assert.assertTrue("The user should have role admin", savedUser.getRoles().contains(roleAdmin));
    }
}
