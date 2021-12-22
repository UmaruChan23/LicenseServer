package com.server.licenseserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenerateCodeRequest {
    private int deviceCount;
    private int duration;
    private String type;
}
