package com.server.licenseserver.controller;

import com.server.licenseserver.entity.License;
import com.server.licenseserver.service.LicenseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/code")
public class ActivationCodeController {

    final LicenseService service;

    public ActivationCodeController(LicenseService service) {
        this.service = service;
    }

    @GetMapping
    public String getActivationCode(@RequestBody License license) {
        return service.save(license);
    }

}
