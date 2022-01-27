package com.server.licenseserver.model;

import com.server.licenseserver.entity.License;
import lombok.Data;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

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
    }

    public void signTicket() {
        try {
            this.signature = createSignature(this.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] createSignature(byte[] data) throws Exception {

        System.out.println(new File(Ticket.class
                .getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .toURI()).getParent());

        Security.addProvider(new BouncyCastleProvider());

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        char[] keyStorePassword = "changeit".toCharArray();

        keyStore.load(new FileInputStream("diplomKeyStore"), keyStorePassword);

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

        Store store = sigData.getCertificates();
        SignerInformationStore signers = sigData.getSignerInfos();
        Collection c = signers.getSigners();
        Iterator it = c.iterator();
        while (it.hasNext()) {
            SignerInformation sig = (SignerInformation) it.next();
            Collection certCollection = store.getMatches(sig.getSID());
            Iterator certIt = certCollection.iterator();
            X509CertificateHolder certHolder = (X509CertificateHolder) certIt.next();
            X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certHolder);
            if (sig.verify(new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(cert))) {
                System.out.println("verified");
            }
        }


        return sigData.getEncoded();
    }
}
