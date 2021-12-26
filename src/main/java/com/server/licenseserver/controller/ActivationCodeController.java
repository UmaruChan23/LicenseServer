package com.server.licenseserver.controller;

import com.server.licenseserver.entity.License;
import com.server.licenseserver.model.GenerateCodeRequest;
import com.server.licenseserver.security.jwt.JwtProvider;
import com.server.licenseserver.service.ActivationService;
import com.server.licenseserver.service.LicenseService;
import com.server.licenseserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/code")
public class ActivationCodeController {

    private final JwtProvider jwtProvider;

    private final LicenseService licenseService;

    private final UserService userService;

    private final ActivationService activationService;

    @Autowired
    public ActivationCodeController(LicenseService licenseService,
                                    UserService userService,
                                    JwtProvider jwtProvider,
                                    ActivationService activationService) {

        this.licenseService = licenseService;
        this.userService = userService;
        this.jwtProvider = jwtProvider;
        this.activationService = activationService;
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
        return licenseService.generateTrial(token.substring(7), productName);
    }

    @PostMapping("/activate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER', 'ROLE_USER')")
    public License activateCode(@RequestHeader("Authorization") String token,
                                 @RequestBody String code) {
        return activationService.activate(code, jwtProvider.getDeviceIdFromToken(token.substring(7)));
    }
}
