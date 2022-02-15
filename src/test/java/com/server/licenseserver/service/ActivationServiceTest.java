package com.server.licenseserver.service;

import com.server.licenseserver.entity.ActivationCode;
import com.server.licenseserver.entity.License;
import com.server.licenseserver.entity.Product;
import com.server.licenseserver.exception.ActivationException;
import com.server.licenseserver.repo.ActivationCodeRepo;
import com.server.licenseserver.repo.LicenseRepo;
import com.server.licenseserver.repo.ProductRepo;
import com.server.licenseserver.repo.UserRepo;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ActivationServiceTest {

    @Autowired
    private ActivationService activationService;

    @MockBean
    private LicenseRepo licenseRepo;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private ProductRepo productRepo;

    @MockBean
    private ActivationCodeRepo activationCodeRepo;

    private ActivationCode code;
    private final String deviceId = "123";
    private final String login = "test";

    @BeforeEach
    void setup() {
        code = new ActivationCode();
        code.setId(1);
        code.setCode("qwerty");
        code.setDeviceCount(1);
        code.setDuration(30);
        code.setProduct(new Product());
    }

    @AfterEach
    void tearDown() {
        code = null;
    }

    @Test
    void successfulActivation() throws ActivationException {
        License license = activationService.activate(code, deviceId, login);
        assertEquals(code.getCode(), license.getCode().getCode());
        assertFalse(license.isBlocked());
        assertEquals(code.getFirstActivationDate(), license.getActivationDate());
        assertEquals(deviceId, license.getDeviceId());
    }
}