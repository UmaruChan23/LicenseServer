package com.server.licenseserver.controller;

import com.server.licenseserver.entity.License;
import com.server.licenseserver.exception.UserAlreadyExistsException;
import com.server.licenseserver.model.GenerateCodeRequest;
import com.server.licenseserver.security.jwt.JwtProvider;
import com.server.licenseserver.service.ActivationService;
import com.server.licenseserver.service.LicenseService;
import com.server.licenseserver.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/code")
public class ActivationCodeController {

    private final JwtProvider jwtProvider;

    private final LicenseService licenseService;

    private final UserService userService;

    private final ActivationService activationService;

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
    public String getActivationCode(@RequestBody GenerateCodeRequest license,
                                    @RequestHeader("Authorization") String token) {
        if (jwtProvider.validateToken(token)) {
            if (!userService.findByLogin(jwtProvider.getLoginFromToken(token))
                    .getRole()
                    .getName()
                    .equals("ROLE_USER")) {
                return licenseService.createNewActivationCode(license);
            }
            return "permission denied";
        }
        return "invalid token";
    }

    @GetMapping("/trial")
    public String getTrialCode(@RequestHeader("Authorization") String token) {
        if (jwtProvider.validateToken(token)) {
            return licenseService.generateTrial(token);
        }
        return "invalid token";
    }

    @PostMapping("/activate")
    private License activateCode(@RequestHeader("Authorization") String token,
                                 @RequestBody String code) {
        return activationService.activate(code, jwtProvider.getDeviceIdFromToken(token));
    }
}
