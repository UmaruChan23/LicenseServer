package com.server.licenseserver.service;

import com.server.licenseserver.entity.ActivationCode;
import com.server.licenseserver.entity.License;
import com.server.licenseserver.repo.ActivationCodeRepo;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Service
public class ActivationService {

    private final ActivationCodeRepo activationCodeRepo;

    public ActivationService(ActivationCodeRepo activationCodeRepo) {
        this.activationCodeRepo = activationCodeRepo;
    }

    public License activate(String code, String deviceId) {
        ActivationCode activationCode = activationCodeRepo.findByCode(code);
        Calendar calendar = new GregorianCalendar();
        if (activationCode != null && activationCode.canAddDevice()) {
            activationCode.increaseActiveDeviceCount();
            activationCode.setFirstActivationDate(new Date());
            License license = new License();
            license.setActivationDate(activationCode.getFirstActivationDate());
            calendar.add(Calendar.DAY_OF_MONTH, activationCode.getDuration());
            license.setEndingDate(new Date(calendar.getTimeInMillis()));
            license.setDeviseId(deviceId);
            activationCodeRepo.save(activationCode);
            return license;
        }
        return null;
    }
}
