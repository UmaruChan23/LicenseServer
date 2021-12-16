package com.server.licenseserver.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    private boolean isBlocked = false;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product")
    List<ActivationCode> codes;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }

    public List<ActivationCode> getCodes() {
        return codes;
    }

    public void setCodes(List<ActivationCode> codes) {
        this.codes = codes;
    }
}
