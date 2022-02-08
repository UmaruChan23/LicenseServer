package com.server.licenseserver.service;

import com.server.licenseserver.entity.Role;
import com.server.licenseserver.entity.User;
import com.server.licenseserver.exception.UserAlreadyExistsException;
import com.server.licenseserver.repo.RoleRepo;
import com.server.licenseserver.repo.UserRepo;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @Autowired
    @Lazy
    public UserServiceTest(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeAll
    public void setUp() {
        user = new User();
        user.setLogin("User1");
        user.setPassword("test_test");
        Role role = new Role();
        role.setName("ROLE_USER");
        user.setRole(role);
    }

    @AfterAll
    public void tearDown() {
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
}