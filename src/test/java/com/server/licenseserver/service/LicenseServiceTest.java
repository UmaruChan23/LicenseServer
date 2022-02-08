package com.server.licenseserver.service;

import com.server.licenseserver.entity.*;
import com.server.licenseserver.exception.ActivationException;
import com.server.licenseserver.exception.ProductNotFoundException;
import com.server.licenseserver.exception.TrialAlreadyExistsException;
import com.server.licenseserver.model.ActivationRequest;
import com.server.licenseserver.model.GenerateCodeRequest;
import com.server.licenseserver.model.GenerateTrialRequest;
import com.server.licenseserver.model.Ticket;
import com.server.licenseserver.repo.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class LicenseServiceTest {

    @Autowired
    private LicenseService licenseService;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private RoleRepo roleRepo;

    @MockBean
    private ProductRepo productRepo;

    @MockBean
    private ActivationCodeRepo activationCodeRepo;

    @MockBean
    private TrialRepo trialRepo;

    @Test
    void createNewActivationCode() throws ProductNotFoundException {
        List<ActivationCode> codeList = new ArrayList<>();
        Mockito.when(activationCodeRepo.save(Mockito.any((ActivationCode.class)))).thenAnswer( i -> {
            ActivationCode activationCode = i.getArgument(0);
            codeList.add(activationCode);
            return null;
        });
        Mockito.doReturn(new Product()).when(productRepo).findById(1);
        GenerateCodeRequest request = new GenerateCodeRequest(1, 30, "TRIAL", 1);
        String code = licenseService.createNewActivationCode(request);
        ActivationCode activationCode = codeList.get(0);
        assertNotNull(activationCode);
        assertEquals(request.getDeviceCount(), activationCode.getDeviceCount());
        assertEquals(request.getDuration(), activationCode.getDuration());
        assertEquals(request.getType(), activationCode.getType());
        assertEquals(code, activationCode.getCode());
    }

    @Test
    void generateTrial() throws TrialAlreadyExistsException, ProductNotFoundException {
        List<ActivationCode> codeList = new ArrayList<>();
        Mockito.when(activationCodeRepo.save(Mockito.any((ActivationCode.class)))).thenAnswer( i -> {
            ActivationCode activationCode = i.getArgument(0);
            codeList.add(activationCode);
            return null;
        });
        List<Trial> trials = new ArrayList<>();
        Mockito.when(trialRepo.save(Mockito.any((Trial.class)))).thenAnswer( i -> {
            Trial trial = i.getArgument(0);
            trials.add(trial);
            return null;
        });
        Product product = new Product();
        product.setId(1);
        Mockito.doReturn(product).when(productRepo).findById(1);
        Mockito.doReturn(new User()).when(userRepo).findByLogin("user");
        GenerateTrialRequest request = new GenerateTrialRequest("user", "123",  1);
        String code = licenseService.generateTrial(request);
        Trial trial = trials.get(0);
        ActivationCode activationCode = codeList.get(0);
        assertNotNull(activationCode);
        assertNotNull(trial);
        assertNotNull(code);
    }

    @Test
    void trialAlreadyTaken() {
        GenerateTrialRequest request = new GenerateTrialRequest("user", "123",  1);
        User user = new User();
        user.setId(1);
        Product product = new Product();
        product.setId(1);
        Mockito.doReturn(product).when(productRepo).findById(1);
        Mockito.doReturn(user).when(userRepo).findByLogin("user");
        Mockito.doReturn(new Trial()).when(trialRepo).findByUserIdAndDeviceId(1, "123");
        Throwable throwable = assertThrows(TrialAlreadyExistsException.class, () -> {
            String newCode = licenseService.generateTrial(request);
        });
        assertNotNull(throwable.getMessage());
    }

    @Test
    void productNotExist(){
        GenerateTrialRequest request = new GenerateTrialRequest("user", "123",  2L);
        Throwable throwable = assertThrows(ProductNotFoundException.class, () -> {
            String newCode = licenseService.generateTrial(request);
        });
        assertNotNull(throwable.getMessage());
    }

    @Test
    void activateLicense() throws ActivationException, ProductNotFoundException {
        GenerateCodeRequest generateCodeRequest = new GenerateCodeRequest(1, 30, "TRIAL", 1L);
        String code = licenseService.createNewActivationCode(generateCodeRequest);
        ActivationRequest activationRequest = new ActivationRequest("user", "123", code);
        Ticket ticket = licenseService.activateLicense(activationRequest);
        assertNotNull(ticket);
        assertNotNull(ticket.getSignature());
        Period period = Period.between(
                ticket.getActivationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(),
                ticket.getLicenseExpDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        assertEquals(generateCodeRequest.getDuration(), period.getDays());

    }

    @Test
    void getActivatedLicense() {
    }

    @Test
    void blockTrial() {
    }

    @Test
    void getActivatedLicenseForProduct() {
    }

    @Test
    void refreshTicket() {
    }
}