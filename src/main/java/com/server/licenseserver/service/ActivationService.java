package com.server.licenseserver.service;

import com.server.licenseserver.entity.ActivationCode;
import com.server.licenseserver.entity.License;
import com.server.licenseserver.entity.User;
import com.server.licenseserver.exception.ActivationException;
import com.server.licenseserver.repo.ActivationCodeRepo;
import com.server.licenseserver.repo.LicenseRepo;
import com.server.licenseserver.repo.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Service
public class ActivationService {

    private final ActivationCodeRepo activationCodeRepo;

    private final LicenseRepo licenseRepo;

    private final UserRepo userRepo;

    public ActivationService(ActivationCodeRepo activationCodeRepo, LicenseRepo licenseRepo, UserRepo userRepo) {
        this.activationCodeRepo = activationCodeRepo;
        this.licenseRepo = licenseRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public License activate(ActivationCode activationCode, String deviceId) throws ActivationException {
        Calendar calendar = new GregorianCalendar();
        if (activationCode != null && activationCode.canAddDevice()) {
            activationCode.increaseActiveDeviceCount();
            activationCode.setFirstActivationDate(new Date());
            License license = new License();
            license.setActivationDate(activationCode.getFirstActivationDate());
            calendar.add(Calendar.DAY_OF_MONTH, activationCode.getDuration());
            license.setEndingDate(new Date(calendar.getTimeInMillis()));
            license.setDeviceId(deviceId);
            activationCodeRepo.save(activationCode);
            license.setCode(activationCode);
            licenseRepo.save(license);
            return license;
        }
        throw new ActivationException("Activation code not found or maximum number of devices reached");
    }
}
