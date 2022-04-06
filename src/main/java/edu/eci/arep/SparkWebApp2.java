package edu.eci.arep;

import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.google.common.hash.Hashing;

import spark.Request;
import spark.Response;
import spark.Session;

public class SparkWebApp2 {

    private static Map<String,String> users = new HashMap<>();
    public static void main(String[] args) {
        staticFiles.location("/public");
        String sha256hex = Hashing.sha256().hashString("admin", StandardCharsets.UTF_8).toString();
        users.put("admin",sha256hex);
        port(getPort());
        secure(getKeyStore(), getKeyStorePassword(), getTrustStore(), getTrustStorePassword());
        loadTrustStore();
        before("/auth/*",SparkWebApp2::auth);
        post("/logein", (req, res) -> {
            System.out.println("Entro");
            req.session(true);
            String pas = req.queryParams("pass");
            boolean resp = pas.equals(users.get(req.queryParams("user")));
            String redirect = "";
            res.type("application/json");
            if(resp){
                req.session().attribute("AUTH",true);
                redirect = "\"auth/hello\"";
            }else redirect="\"/\"";
            return "{\"ubi\":"+redirect+"}";
        });
        get("/auth/hello", (req, res) -> {
            return "Hello your number is: "+ readURL("https://ec2-54-87-13-198.compute-1.amazonaws.com:5000/getnum");
        });
        post("/logeout", (req, res) -> {
            req.session(true);
            req.session().attribute("AUTH",false);
            return true;
        });
    }



    private static void auth(Request rec, Response res){
        rec.session(true);
        Session session = rec.session();
        if(session.isNew()) rec.session().attribute("AUTH",false);
        else{
            if(!(boolean)rec.session().attribute("AUTH"))res.redirect("/");
        }
    }

    public static String readURL(String sitetoread) {
        try {
            // Crea el objeto que representa una URL2
            URL siteURL = new URL(sitetoread);
            // Crea el objeto que URLConnection
            URLConnection urlConnection = siteURL.openConnection();
            // Obtiene los campos del encabezado y los almacena en un estructura Map
            Map<String, List<String>> headers = urlConnection.getHeaderFields();
            // Obtiene una vista del mapa como conjunto de pares <K,V>
            // para poder navegarlo
            Set<Map.Entry<String, List<String>>> entrySet = headers.entrySet();
            // Recorre la lista de campos e imprime los valores
            for (Map.Entry<String, List<String>> entry : entrySet) {
                String headerName = entry.getKey();

                //Si el nombre es nulo, significa que es la linea de estado
                if (headerName != null) {
                    System.out.print(headerName + ":");
                }
                List<String> headerValues = entry.getValue();
                for (String value : headerValues) {
                    System.out.print(value);
                }
                System.out.println("");
            }

            System.out.println("-------message-body------");
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String inputLine = null;
            while ((inputLine = reader.readLine()) != null) {
                System.out.println(inputLine);
                return inputLine;
            }
        } catch (IOException x) {
            System.err.println(x);
        }
        return "-1";
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
        return 5001;
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
        return "ssl/aws1.p12";
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
