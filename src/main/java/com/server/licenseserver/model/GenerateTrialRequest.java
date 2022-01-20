package com.server.licenseserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateTrialRequest {
    private String login;
    private String deviceId;
    private String productName;
}
