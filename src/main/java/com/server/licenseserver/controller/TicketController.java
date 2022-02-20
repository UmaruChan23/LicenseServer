package com.server.licenseserver.controller;

import com.server.licenseserver.exception.handler.InvalidTicketException;
import com.server.licenseserver.model.Ticket;
import com.server.licenseserver.security.jwt.JwtProvider;
import com.server.licenseserver.service.LicenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    private final LicenseService licenseService;

    private final JwtProvider jwtProvider;

    public TicketController(LicenseService licenseService, JwtProvider jwtProvider) {
        this.licenseService = licenseService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping
    public ResponseEntity<Ticket> renewTicket(@RequestHeader("Authorization") String token,
                                              @RequestBody long productId)
            throws InvalidTicketException {
        String subToken = token.substring(7);
        String login = jwtProvider.getLoginFromToken(subToken);
        String deviceId = jwtProvider.getDeviceIdFromToken(subToken);
        Ticket newTicket = licenseService.refreshTicket(productId, login, deviceId);
        newTicket.signTicket();
        return ResponseEntity.ok(newTicket);
    }
}
