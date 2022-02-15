package com.server.licenseserver.model;

import com.server.licenseserver.entity.ActivationCode;
import lombok.Data;

import java.util.Date;

@Data
public class CodeInfoForClient {
    private String code;
    private int activeDevices;
    private int deviceCount;
    private int duration;
    private Date firstActivationDate;
    private String type;

    public CodeInfoForClient(ActivationCode activationCode) {
        this.code = activationCode.getCode();
        this.activeDevices = activationCode.getActiveDevices();
        this.deviceCount = activationCode.getDeviceCount();
        this.duration = activationCode.getDuration();
        this.firstActivationDate = activationCode.getFirstActivationDate();
        this.type = activationCode.getType();
    }
}
