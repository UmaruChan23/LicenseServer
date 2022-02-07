package com.server.licenseserver.controller;

import com.server.licenseserver.exception.handler.InvalidTicketException;
import com.server.licenseserver.model.Ticket;
import com.server.licenseserver.service.LicenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    private final LicenseService licenseService;

    public TicketController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @PostMapping
    public ResponseEntity<Ticket> renewTicket(@Valid @RequestBody Ticket ticket) throws InvalidTicketException {
        Ticket newTicket = licenseService.refreshTicket(ticket);
        newTicket.signTicket();
        return ResponseEntity.ok(newTicket);
    }
}
