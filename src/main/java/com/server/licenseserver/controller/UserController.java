package com.server.licenseserver.controller;

import com.server.licenseserver.entity.ActivationCode;
import com.server.licenseserver.model.CodeInfoForClient;
import com.server.licenseserver.security.jwt.JwtProvider;
import com.server.licenseserver.service.LicenseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final JwtProvider jwtProvider;

    private final LicenseService licenseService;

    public UserController(JwtProvider jwtProvider, LicenseService licenseService) {
        this.jwtProvider = jwtProvider;
        this.licenseService = licenseService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER', 'ROLE_USER')")
    public List<CodeInfoForClient> getAllUserLicenses(@RequestHeader("Authorization") String token) {
        String subToken = token.substring(7);
        return licenseService.getAllUserCodes(jwtProvider.getLoginFromToken(subToken));
    }
}
