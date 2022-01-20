package com.server.licenseserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ActivationRequest {
    String deviceId;
    String code;
}
