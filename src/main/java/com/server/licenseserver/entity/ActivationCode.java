package com.server.licenseserver.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "codes")
public class ActivationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String code;
    private int activeDevices;
    private int deviceCount;
    private int duration;
    private Date firstActivationDate;
    private String type;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "code")
    List<License> license;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    public ActivationCode(String code) {
        this.code = code;
    }

    public ActivationCode() {
    }

    public boolean canAddDevice() {
        return activeDevices < deviceCount;
    }

    public boolean canRemoveDevice() {
        return activeDevices > 0;
    }

    public void increaseActiveDeviceCount() {
        if (canAddDevice()) {
            activeDevices++;
        }
    }

    public void decreaseActiveDeviceCount() {
        if (canRemoveDevice()) {
            activeDevices--;
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<License> getLicense() {
        return license;
    }

    public void setLicense(List<License> license) {
        this.license = license;
    }

    public int getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Date getFirstActivationDate() {
        return firstActivationDate;
    }

    public void setFirstActivationDate(Date firstActivationDate) {
        this.firstActivationDate = firstActivationDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
