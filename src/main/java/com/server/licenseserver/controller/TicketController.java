package com.server.licenseserver.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.server.licenseserver.exception.model.InvalidTicketException;
import com.server.licenseserver.model.Ticket;
import com.server.licenseserver.security.jwt.JwtProvider;
import com.server.licenseserver.service.LicenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @ResponseBody
    public ResponseEntity<Ticket> renewTicket(@RequestHeader("Authorization") String token,
                                              @RequestBody String json)
            throws InvalidTicketException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        long productId = root.path("productId").asLong();
        String subToken = token.substring(7);
        String login = jwtProvider.getLoginFromToken(subToken);
        String deviceId = jwtProvider.getDeviceIdFromToken(subToken);
        Ticket newTicket = licenseService.refreshTicket(productId, login, deviceId);
        newTicket.signTicket();
        return ResponseEntity.ok(newTicket);
    }
}
