package com.server.licenseserver.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "trials")
@Data
public class Trial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "device_id")
    private String deviceId;
}
