package com.fenixcommunity.centralspace.Converter;

import lombok.extern.java.Log;
import org.springframework.security.crypto.codec.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.persistence.Transient;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Properties;
import java.util.logging.Level;

@Converter
@Log
public class CryptoJpaConverter implements AttributeConverter<String, String> {

    private static String ALGORITHM;
    private static byte[] KEY;
    private static final String algorithm_property_key = "encryption.algorithm";
    private static final String secret_property_key = "encryption.key";
    private static final String security_file = "security.properties";
    private static final Properties properties;

    static {
        properties = new Properties();
        try {
            properties.load(CryptoJpaConverter.class.getClassLoader().getResourceAsStream(security_file));
        } catch (IOException e) {
            log.log(Level.SEVERE, "Could not load properties file 'security.properties' using unsecure encryption key.");
            //todo obsluz
            throw new RuntimeException();
        }
        ALGORITHM = (String) properties.get(algorithm_property_key);
        KEY = ((String) properties.get(secret_property_key)).getBytes();
    }

    @Override
    @Transient
    public String convertToDatabaseColumn(String ccData) {
        if (ccData == null) {
            //todo zastapic innym?
            throw new NullPointerException();
        }
        Key key = new SecretKeySpec(KEY, ALGORITHM);
        try {
            final Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.ENCRYPT_MODE, key);
            final String encrypted = new String(Base64.encode(c
                    .doFinal(ccData.getBytes())), StandardCharsets.UTF_8);
            return encrypted;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    @Transient
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            //todo zastapic innym?
            throw new NullPointerException();
        }
        Key key = new SecretKeySpec(KEY, ALGORITHM);
        try {
            final Cipher c = Cipher.getInstance(ALGORITHM);
            c.init(Cipher.DECRYPT_MODE, key);

            final String decrypted = new String(c.doFinal(Base64
                    .decode(dbData.getBytes(StandardCharsets.UTF_8))));
            return decrypted;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
