package com.server.licenseserver.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name ="users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String login;

    @Column
    private String password;

    private boolean hasActivatedLicense = false;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

}
