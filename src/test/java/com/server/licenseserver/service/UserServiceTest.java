package com.server.licenseserver.service;

import com.server.licenseserver.entity.Role;
import com.server.licenseserver.entity.User;
import com.server.licenseserver.exception.UserAlreadyExistsException;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private User user;

    @Autowired
    @Lazy
    public UserServiceTest(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    void setUp() {
        user = new User();
        user.setLogin("User1");
        user.setPassword("test_test");
        Role role = new Role();
        role.setName("ROLE_USER");
        user.setRole(role);
    }

    @AfterEach
    void tearDown() {
        user = null;
    }

    @Test
    void saveUser() {
        try {
            User userFromDb = userService.saveUser(user);
            assertNotNull(userFromDb);
            assertEquals(user.getPassword(), userFromDb.getPassword());
            assertTrue(CoreMatchers.is(user.getRole()).matches(userFromDb.getRole()));
            assertTrue(CoreMatchers.is(user.getLogin()).matches(userFromDb.getLogin()));
        } catch (UserAlreadyExistsException e) {
            e.printStackTrace();
        }
    }

    @Test
    void findByLogin() {
        User userFromDb = userService.findByLogin(user.getLogin());
        assertNotNull(userFromDb);
        assertTrue(passwordEncoder.matches(user.getPassword(), userFromDb.getPassword()));
        assertEquals(user.getRole().getName(), userFromDb.getRole().getName());
        assertTrue(CoreMatchers.is(user.getLogin()).matches(userFromDb.getLogin()));}

    @Test
    void findByLoginAndPassword() {
        User userFromDb = userService.findByLoginAndPassword(user.getLogin(), user.getPassword());
        assertNotNull(userFromDb);
        assertTrue(passwordEncoder.matches(user.getPassword(), userFromDb.getPassword()));
        assertEquals(user.getRole().getName(), userFromDb.getRole().getName());
        assertTrue(CoreMatchers.is(user.getLogin()).matches(userFromDb.getLogin()));
    }
}