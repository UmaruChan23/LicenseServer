package com.server.licenseserver.model;

import com.server.licenseserver.entity.License;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Data
public class Ticket {
    private Date ticketExpDate;

    private Date activationDate;
    private Date licenseExpDate;
    private String deviceId;
    private String type;
    private boolean blocked;
    private byte[] signature;

    public Ticket(License license) {
        Calendar currentDate = new GregorianCalendar();
        currentDate.add(Calendar.DAY_OF_MONTH, 1);
        this.ticketExpDate = currentDate.getTime();
        this.activationDate = license.getActivationDate();
        this.licenseExpDate = license.getEndingDate();
        this.deviceId = license.getDeviceId();
        this.type = license.getCode().getType();
        this.blocked = false;

        //create signature
    }

    public void signTicket() {
        this.signature = createSignature(this.toString().getBytes(StandardCharsets.UTF_8));
    }

    private byte[] createSignature(byte[] bytes) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA");
            keyPairGenerator.initialize(2048);

            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            PrivateKey privateKey = keyPair.getPrivate();

            Signature signature = Signature.getInstance("SHA256withDSA");

            signature.initSign(privateKey);

            signature.update(bytes);

            return signature.sign();

        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            e.printStackTrace();
            return null;
        }
    }
}
