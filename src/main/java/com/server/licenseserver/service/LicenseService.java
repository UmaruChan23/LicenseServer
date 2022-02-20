package com.server.licenseserver.service;

import com.server.licenseserver.entity.*;
import com.server.licenseserver.exception.ActivationException;
import com.server.licenseserver.exception.ProductNotFoundException;
import com.server.licenseserver.exception.TrialAlreadyExistsException;
import com.server.licenseserver.exception.handler.InvalidTicketException;
import com.server.licenseserver.model.*;
import com.server.licenseserver.repo.*;
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

    public LicenseService(ActivationCodeRepo activationCodeRepo, LicenseRepo licenseRepo, UserService userService, ActivationService activationService, TrialRepo trialRepo, ProductRepo productRepo, JwtProvider provider, UserRepo userRepo) {
        this.activationCodeRepo = activationCodeRepo;
        this.licenseRepo = licenseRepo;
        this.userService = userService;
        this.activationService = activationService;
        this.trialRepo = trialRepo;
        this.productRepo = productRepo;
    }

    public String createNewActivationCode(GenerateCodeRequest request) throws ProductNotFoundException {
        ActivationCode activationCode = generateCodeFromRequest(request);
        activationCodeRepo.save(activationCode);
        return activationCode.getCode();
    }

    private ActivationCode generateCodeFromRequest(GenerateCodeRequest request) throws ProductNotFoundException {
        ActivationCode activationCode = new ActivationCode();
        activationCode.setCode(generateCode());
        activationCode.setDeviceCount(request.getDeviceCount());
        activationCode.setDuration(request.getDuration());
        activationCode.setType(request.getType());
        Product product = productRepo.findById(request.getProductId());
        if (product != null) {
            activationCode.setProduct(product);
            activationCodeRepo.save(activationCode);
            return activationCode;
        }
        throw new ProductNotFoundException("product not found");
    }

    @Transactional
    public String generateTrial(GenerateTrialRequest request)
            throws TrialAlreadyExistsException, ProductNotFoundException {
        String login = request.getLogin();
        String deviceId = request.getDeviceId();
        long productId = request.getProductId();
        User user = userService.findByLogin(login);
        Product product = productRepo.findById(productId);
        if (product != null) {
            if (user != null) {
                if (trialRepo.findByUserIdAndDeviceId(user.getId(), deviceId) == null) {
                    Trial trial = new Trial();
                    trial.setProductId(product.getId());
                    trial.setUserId(user.getId());
                    trial.setDeviceId(deviceId);
                    trialRepo.save(trial);
                    return createNewActivationCode(new GenerateCodeRequest(1, 30, "TRIAL", product.getId()));
                }
                throw new TrialAlreadyExistsException("trial has already been activated for this user");
            }
            throw new UsernameNotFoundException("user not found");
        } else {
            throw new ProductNotFoundException("product not found");
        }
    }

    public Ticket activateLicense(ActivationRequest request) throws ActivationException {
        String deviceId = request.getDeviceId();
        ActivationCode code = activationCodeRepo.findByCode(request.getCode());
        if (code != null) {
            User user = userService.findByLogin(request.getLogin());
            License currentLicense = getActivatedLicense(deviceId,
                    user.getId(),
                    code.getProduct().getId()).orElse(null);
            if (currentLicense != null) {
                if (currentLicense.getCode().getType().equalsIgnoreCase("TRIAL")) {
                    blockTrial(currentLicense);
                } else {
                    throw new ActivationException("there is already an active license");
                }
            }
        } else {
            throw new ActivationException("invalid code");
        }
        Ticket ticket = new Ticket(activationService.activate(code, deviceId, request.getLogin()));
        ticket.signTicket();
        return ticket;
    }

    public Optional<License> getActivatedLicense(String deviceId, long userId, long productId) {
        List<License> licenses = licenseRepo.findAllByDeviceId(deviceId);
        for (License license : licenses) {
            if (!license.isBlocked() && license.getEndingDate().after(new Date())) {
                if (license.getCode().getOwner().getId() == userId) {
                    if (license.getCode().getProduct().getId() == productId) {
                        return Optional.of(license);
                    }
                }
            }
        }
        return Optional.empty();
    }

    public void blockTrial(License license) {
        license.setBlocked(true);
        licenseRepo.save(license);
    }

    public Ticket refreshTicket(long productId, String login, String deviceId) throws InvalidTicketException {
        User user = userService.findByLogin(login);
        License license = getActivatedLicense(deviceId, user.getId(), productId).orElse(null);
        if (license != null && license.getActivationDate().before(new Date())
                && license.getEndingDate().after(new Date())
                && !license.isBlocked()) {
            Calendar currentDate = new GregorianCalendar();
            currentDate.add(Calendar.DAY_OF_MONTH, 1);
            return new Ticket(license);
        }
        throw new InvalidTicketException("invalid ticket");
    }

    private String generateCode() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 20;
        Random random = new Random();

        return random.ints(leftLimit,
                rightLimit + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    }

    public List<CodeInfoForClient> getAllUserCodes(String login) {
        User user = userService.findByLogin(login);
        List<ActivationCode> codes = activationCodeRepo.findAllByOwner(user);
        List<CodeInfoForClient> resultList = new ArrayList<>();
        for (ActivationCode code : codes) {
            resultList.add(new CodeInfoForClient(code));
        }
        return resultList;
    }
}
