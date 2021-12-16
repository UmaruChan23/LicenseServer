package com.server.licenseserver.service;

import com.server.licenseserver.entity.ActivationCode;
import com.server.licenseserver.entity.License;
import com.server.licenseserver.repo.ActivationCodeRepo;
import com.server.licenseserver.repo.LicenseRepo;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class LicenseService {

    final ActivationCodeRepo activationCodeRepo;

    final LicenseRepo licenseRepo;

    public LicenseService(ActivationCodeRepo activationCodeRepo, LicenseRepo licenseRepo) {
        this.activationCodeRepo = activationCodeRepo;
        this.licenseRepo = licenseRepo;
    }

    public String save(License license) {
        ActivationCode activationCode = new ActivationCode(generateCode());
        activationCodeRepo.save(activationCode);
        license.setCode(activationCode);
        licenseRepo.save(license);
        return activationCode.getCode();
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
