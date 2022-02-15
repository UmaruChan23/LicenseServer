package com.server.licenseserver.model;

import com.server.licenseserver.entity.License;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

import javax.validation.constraints.NotNull;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

@Data
@AllArgsConstructor
public class Ticket {
    private Date ticketExpDate;

    @NotNull
    private Date activationDate;

    @NotNull
    private Date licenseExpDate;

    @NotNull
    private long userId;

    @NotNull
    private String deviceId;

    @NotNull
    private String type;

    @NotNull
    private boolean blocked;

    private byte[] signature;

    public Ticket(License license) {
        Calendar currentDate = new GregorianCalendar();
        currentDate.add(Calendar.DAY_OF_MONTH, 1);
        this.ticketExpDate = currentDate.getTime();
        this.activationDate = license.getActivationDate();
        this.licenseExpDate = license.getEndingDate();
        this.userId = license.getCode().getOwner().getId();
        this.deviceId = license.getDeviceId();
        this.type = license.getCode().getType();
        this.blocked = false;
    }

    public void signTicket() {
        try {
            this.signature = createSignature(this.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] createSignature(byte[] data) throws Exception {

        Security.addProvider(new BouncyCastleProvider());

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        char[] keyStorePassword = "changeit".toCharArray();

        keyStore.load(new FileInputStream("/opt/diplomKeyStore"), keyStorePassword);

        KeyStore.ProtectionParameter entryPassword =
                new KeyStore.PasswordProtection(keyStorePassword);

        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry)
                keyStore.getEntry("diplom", entryPassword);

        X509Certificate[] certificates = new X509Certificate[1];
        certificates[0] = (X509Certificate) privateKeyEntry.getCertificate();

        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyEntry.getPrivateKey().getEncoded());
        KeyFactory keyFactory = KeyFactory.getInstance("ECGOST3410-2012", "BC");
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        CMSTypedData msg = new CMSProcessableByteArray(data);
        Store certStore = new JcaCertStore(Arrays.asList(certificates));
        CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
        ContentSigner signer = new org.bouncycastle.operator.jcajce.JcaContentSignerBuilder("GOST3411withECGOST3410").setProvider("BC").build(privateKey);
        gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().setProvider("BC").build()).build(signer, (X509Certificate) certificates[0]));
        gen.addCertificates(certStore);
        CMSSignedData sigData = gen.generate(msg, true);

        return sigData.getEncoded();
    }
}
