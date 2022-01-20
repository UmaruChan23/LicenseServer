package com.server.licenseserver.controller;

import com.server.licenseserver.entity.License;
import com.server.licenseserver.entity.User;
import com.server.licenseserver.model.ActivationRequest;
import com.server.licenseserver.model.GenerateCodeRequest;
import com.server.licenseserver.model.GenerateTrialRequest;
import com.server.licenseserver.model.Ticket;
import com.server.licenseserver.security.jwt.JwtProvider;
import com.server.licenseserver.service.ActivationService;
import com.server.licenseserver.service.LicenseService;
import com.server.licenseserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/code")
public class ActivationCodeController {

    private final JwtProvider jwtProvider;

    private final LicenseService licenseService;

    @Autowired
    public ActivationCodeController(LicenseService licenseService,
                                    JwtProvider jwtProvider) {

        this.licenseService = licenseService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
    public String getActivationCode(@RequestBody GenerateCodeRequest license) {
        return licenseService.createNewActivationCode(license);
    }

    @PostMapping ("/trial")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER', 'ROLE_USER')")
    public String getTrialCode(@RequestHeader("Authorization") String token,
                               @RequestBody String productName) {
        String subToken = token.substring(7);
        GenerateTrialRequest request = new GenerateTrialRequest(
                jwtProvider.getLoginFromToken(subToken),
                jwtProvider.getDeviceIdFromToken(subToken),
                productName
        );
        return licenseService.generateTrial(request);
    }

    @PostMapping(value = "/activate", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER', 'ROLE_USER')")
    public ResponseEntity<Ticket> activateCode(@RequestHeader("Authorization") String token,
                                               @RequestBody String code) {
        ActivationRequest request = new ActivationRequest(
                jwtProvider.getDeviceIdFromToken(token.substring(7)),
                code
        );
        return ResponseEntity.ok(licenseService.activateLicense(request));
    }
}
