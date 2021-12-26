package com.server.licenseserver.service;

import com.server.licenseserver.entity.ActivationCode;
import com.server.licenseserver.entity.Product;
import com.server.licenseserver.entity.Trial;
import com.server.licenseserver.entity.User;
import com.server.licenseserver.model.GenerateCodeRequest;
import com.server.licenseserver.repo.*;
import com.server.licenseserver.security.jwt.JwtProvider;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class LicenseService {

    private final ActivationCodeRepo activationCodeRepo;

    private final LicenseRepo licenseRepo;

    private final UserRepo userRepo;

    private final TrialRepo trialRepo;

    private final ProductRepo productRepo;

    private final JwtProvider provider;

    public LicenseService(ActivationCodeRepo activationCodeRepo,
                          LicenseRepo licenseRepo,
                          UserRepo userRepo,
                          TrialRepo trialRepo,
                          ProductRepo productRepo, JwtProvider provider) {
        this.activationCodeRepo = activationCodeRepo;
        this.licenseRepo = licenseRepo;
        this.userRepo = userRepo;
        this.trialRepo = trialRepo;
        this.productRepo = productRepo;
        this.provider = provider;
    }

    public String createNewActivationCode(GenerateCodeRequest request) {
        ActivationCode activationCode = generateCodeFromRequest(request);
        activationCodeRepo.save(activationCode);
        return activationCode.getCode();
    }

    private ActivationCode generateCodeFromRequest(GenerateCodeRequest request) {
        ActivationCode activationCode = new ActivationCode();
        activationCode.setCode(generateCode());
        activationCode.setDeviceCount(request.getDeviceCount());
        activationCode.setDuration(request.getDuration());
        activationCode.setType(request.getType());
        Product product = productRepo.getById(request.getProductId());
        activationCode.setProduct(product);
        return activationCode;
    }

    public String generateTrial(String token, String productName) {
        String login = provider.getLoginFromToken(token);
        String deviceId = provider.getDeviceIdFromToken(token);
        User user = userRepo.findByLogin(login);
        Product product = productRepo.findByName(productName);
        if (user != null) {
            if (trialRepo.findByUserId(user.getId()) == null) {
                Trial trial = new Trial();
                trial.setUserId(user.getId());
                trial.setDeviceId(deviceId);
                trialRepo.save(trial);
                return createNewActivationCode(new GenerateCodeRequest(1, 30 , "TRIAL", product.getId()));
            }
            return null;
        }
        throw new UsernameNotFoundException("user not found");
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
