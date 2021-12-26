package com.server.licenseserver.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "product")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private boolean isBlocked = false;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    List<ActivationCode> codes;
}
