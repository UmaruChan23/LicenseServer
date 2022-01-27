package com.server.licenseserver.service;

import com.server.licenseserver.entity.*;
import com.server.licenseserver.model.ActivationRequest;
import com.server.licenseserver.model.GenerateCodeRequest;
import com.server.licenseserver.model.GenerateTrialRequest;
import com.server.licenseserver.model.Ticket;
import com.server.licenseserver.repo.ActivationCodeRepo;
import com.server.licenseserver.repo.LicenseRepo;
import com.server.licenseserver.repo.ProductRepo;
import com.server.licenseserver.repo.TrialRepo;
import com.server.licenseserver.security.jwt.JwtProvider;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class LicenseService {

    private final ActivationCodeRepo activationCodeRepo;

    private final LicenseRepo licenseRepo;

    private final UserService userService;

    private final ActivationService activationService;

    private final TrialRepo trialRepo;

    private final ProductRepo productRepo;

    public LicenseService(ActivationCodeRepo activationCodeRepo,
                          LicenseRepo licenseRepo,
                          UserService userService,
                          ActivationService activationService, TrialRepo trialRepo,
                          ProductRepo productRepo, JwtProvider provider) {
        this.activationCodeRepo = activationCodeRepo;
        this.licenseRepo = licenseRepo;
        this.userService = userService;
        this.activationService = activationService;
        this.trialRepo = trialRepo;
        this.productRepo = productRepo;
    }

    public String createNewActivationCode(GenerateCodeRequest request, User user) {
        ActivationCode activationCode = generateCodeFromRequest(request, user);
        activationCodeRepo.save(activationCode);
        return activationCode.getCode();
    }

    private ActivationCode generateCodeFromRequest(GenerateCodeRequest request, User user) {
        ActivationCode activationCode = new ActivationCode();
        activationCode.setCode(generateCode());
        activationCode.setDeviceCount(request.getDeviceCount());
        activationCode.setDuration(request.getDuration());
        activationCode.setType(request.getType());
        activationCode.setOwner(user);
        Product product = productRepo.getById(request.getProductId());
        activationCode.setProduct(product);
        return activationCode;
    }

    @Transactional
    public String generateTrial(GenerateTrialRequest request) {
        String login = request.getLogin();
        String deviceId = request.getDeviceId();
        //сделать поиск по id +
        long productId = request.getProductId();
        User user = userService.findByLogin(login);
        Product product = productRepo.findById(productId);
        //открыть транзакцию +
        if (user != null) {
            if (trialRepo.findByUserIdAndDeviceId(user.getId(), deviceId) == null) {
                Trial trial = new Trial();
                trial.setProductId(product.getId());
                trial.setUserId(user.getId());
                trial.setDeviceId(deviceId);
                trialRepo.save(trial);
                return createNewActivationCode(new GenerateCodeRequest(1, 30, "TRIAL", product.getId()), user);
            }
            return null;
        }
        throw new UsernameNotFoundException("user not found");
    }

    public Ticket activateLicense(ActivationRequest request) {
        String deviceId = request.getDeviceId();
        String code = request.getCode();
        License currentLicense = getActivatedLicenseForProduct(deviceId, code);
        if (currentLicense != null) {
            if (currentLicense.getCode().getType().equalsIgnoreCase("TRIAL")) {
                blockTrial(currentLicense);
            } else {
                //выкинуть исключение
                return null;
            }
        }
        Ticket ticket = new Ticket(activationService.activate(code, deviceId));
        ticket.signTicket();
        return ticket;
    }

    public License getActivatedLicense(String deviceId) {
        List<License> licenses = licenseRepo.findAllByDeviceId(deviceId);
        for (License license : licenses) {
            if (!license.isBlocked() && license.getEndingDate().after(new Date())) {
                return license;
            }
        }
        return null;
    }

    public void blockTrial(License license) {
        license.setBlocked(true);
        licenseRepo.save(license);
    }

    public License getActivatedLicenseForProduct(String deviceId, String code) {
        License license = getActivatedLicense(deviceId);
        if (license != null) {
            ActivationCode activationCode = activationCodeRepo.findByCode(code);
            if (license.getCode().getProduct().getId() == activationCode.getProduct().getId()) {
                return license;
            }
        }
        return null;
    }

    public Ticket refreshTicket(Ticket ticket) {
        License license = getActivatedLicense(ticket.getDeviceId());
        if (ticket.getActivationDate().equals(license.getActivationDate()) &&
            ticket.getLicenseExpDate().equals(license.getActivationDate()) &&
            !license.isBlocked()) {
            Calendar currentDate = new GregorianCalendar();
            currentDate.add(Calendar.DAY_OF_MONTH, 1);
            ticket.setTicketExpDate(currentDate.getTime());
        }
        return ticket;
    }

    private String generateCode() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 20;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        return generatedString;
    }
}
