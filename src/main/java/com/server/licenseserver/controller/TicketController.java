package com.server.licenseserver.controller;

import com.server.licenseserver.entity.License;
import com.server.licenseserver.model.Ticket;
import com.server.licenseserver.service.LicenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ticket")
public class TicketController {

    private final LicenseService licenseService;

    public TicketController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    //1)Проверить наличие лицензий в базе по deviceId
    //2)Найти не заблокиованную лицензию из списка
    //3)Проверить даты тикета и базы
    @PostMapping
    public ResponseEntity<Ticket> renewTicket(@RequestBody Ticket ticket) {

        //проверка подписи тикета
        Ticket newTicket = licenseService.refreshTicket(ticket);
        newTicket.signTicket();
        return ResponseEntity.ok(newTicket);
    }
}
