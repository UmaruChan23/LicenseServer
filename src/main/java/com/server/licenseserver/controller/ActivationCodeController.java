package com.server.licenseserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.licenseserver.exception.ActivationException;
import com.server.licenseserver.exception.ProductNotFoundException;
import com.server.licenseserver.exception.TrialAlreadyExistsException;
import com.server.licenseserver.model.ActivationRequest;
import com.server.licenseserver.model.GenerateCodeRequest;
import com.server.licenseserver.model.GenerateTrialRequest;
import com.server.licenseserver.model.Ticket;
import com.server.licenseserver.security.jwt.JwtProvider;
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

    private final UserService userService;

    @Autowired
    public ActivationCodeController(LicenseService licenseService,
                                    JwtProvider jwtProvider, UserService userService) {

        this.licenseService = licenseService;
        this.jwtProvider = jwtProvider;
        this.userService = userService;
    }

    @PostMapping("/generate")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER')")
    public String getActivationCode(@RequestBody GenerateCodeRequest license) throws ProductNotFoundException {
        return licenseService.createNewActivationCode(license);
    }

    @PostMapping ("/trial")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER', 'ROLE_USER')")
    public String getTrialCode(@RequestHeader("Authorization") String token,
                               @RequestBody String json) throws TrialAlreadyExistsException,
            ProductNotFoundException,
            JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        long productId = root.path("productId").asLong();
        String subToken = token.substring(7);
        GenerateTrialRequest request = new GenerateTrialRequest(
                jwtProvider.getLoginFromToken(subToken),
                jwtProvider.getDeviceIdFromToken(subToken),
                productId
        );
        return licenseService.generateTrial(request);
    }

    @PostMapping(value = "/activate", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_SELLER', 'ROLE_USER')")
    public ResponseEntity<Ticket> activateCode(@RequestHeader("Authorization") String token,
                                               @RequestBody String code) throws ActivationException {
        String subToken = token.substring(7);
        ActivationRequest request = new ActivationRequest(
                jwtProvider.getDeviceIdFromToken(subToken),
                jwtProvider.getLoginFromToken(subToken),
                code
        );
        return ResponseEntity.ok(licenseService.activateLicense(request));
    }
}
