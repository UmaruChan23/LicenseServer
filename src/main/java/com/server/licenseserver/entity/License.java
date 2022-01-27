package com.server.licenseserver.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "license")
@Data
public class License {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "code_id")
    private ActivationCode code;
    private Date activationDate;
    private Date endingDate;
    private String deviceId;
    private boolean blocked = false;
}
