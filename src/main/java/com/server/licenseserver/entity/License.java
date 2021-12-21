package com.server.licenseserver.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "license")
public class License {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "code_id")
    private ActivationCode code;
    private Date activationDate;
    private Date endingDate;
    private long deviseId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ActivationCode getCode() {
        return code;
    }

    public void setCode(ActivationCode code) {
        this.code = code;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(Date activationDate) {
        this.activationDate = activationDate;
    }

    public Date getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(Date endingDate) {
        this.endingDate = endingDate;
    }
}
