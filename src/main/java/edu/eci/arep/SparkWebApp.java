package edu.eci.arep;

import static spark.Spark.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Random;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class SparkWebApp {

    public static void main(String[] args) {
        secure(getKeyStore(), getKeyStorePassword(), null, null);
        loadTrustStore();
        // SecureURLReader.read();
        port(getPort());
        get("/getnum", (req, res) -> {
            Random random = new Random();

            int value = random.nextInt(11);
            System.out.println(value);
            return value;
        });
    }


    static void loadTrustStore(){
        File trustStoreFile = new File(getTrustStore());
        char[] trustStorePassword = getKeyStorePassword().toCharArray();

        try {
            // Load the trust store, the default type is "pkcs12", the alternative is "jks"
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(new FileInputStream(trustStoreFile), trustStorePassword);
            // Get the singleton instance of the TrustManagerFactory
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());

            // Itit the TrustManagerFactory using the truststore object
            tmf.init(trustStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            SSLContext.setDefault(sslContext);
        } catch (NoSuchAlgorithmException | CertificateException | IOException | KeyStoreException | KeyManagementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 5000;
        // returns default port if heroku-port isn't set (i.e. on localhost)
    }

    static String getKeyStorePassword() {
        if (System.getenv("KEYSTOREPWD") != null) {
            return System.getenv("KEYSTOREPWD");
        }
        return "123456";
    }

    static String getKeyStore() {
        if (System.getenv("KEYSTORE") != null) {
            return System.getenv("KEYSTORE");
        }
        return "ssl/aws2.p12";
    }

    static String getTrustStorePassword() {
        if (System.getenv("TRUSTSTORE") != null) {
            return System.getenv("TRUSTSTOREPWD");
        }
        return "123456";
    }

    static String getTrustStore() {
        if (System.getenv("TRUSTSTORE") != null) {
            return System.getenv("TRUSTSTORE");
        }
        return "./ssl/myTrustStore";
    }

}
