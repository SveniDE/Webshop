package de.meinshop.webshop.helper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Erzeugt und prueft Passwort-Hashwerte (PBKDF2 mit SHA-256, Salt, 600000 Runden).
 * Gespeichert wird: pbkdf2_sha256$RUNDEN$SALT$HASH. Das Passwort selbst nie.
 */
public class PasswortHasher {

    private static final String ALGORITHMUS = "PBKDF2WithHmacSHA256";
    private static final String KENNUNG = "pbkdf2_sha256";
    private static final int RUNDEN = 600000;
    private static final int SALT_BYTES = 16;
    private static final int HASH_BITS = 256;

    private PasswortHasher() {
    }

    //Klartext-Passwort in speicherbaren Hashtext umwandeln. 
    public static String hashe(String passwort) {
        byte[] salt = new byte[SALT_BYTES];
        new SecureRandom().nextBytes(salt);
        byte[] hash = berechne(passwort, salt, RUNDEN);
        Base64.Encoder b64 = Base64.getEncoder();
        return KENNUNG + "$" + RUNDEN + "$" + b64.encodeToString(salt) + "$"
                + b64.encodeToString(hash);
    }

    //Prueft, ob das Passwort zum gespeicherten Hashtext passt. 
    public static boolean pruefe(String passwort, String gespeichert) {
        String[] teile = gespeichert.split("\\$");
        if (teile.length != 4 || !teile[0].equals(KENNUNG)) {
            throw new IllegalArgumentException("Unbekanntes Hashformat");
        }
        int runden = Integer.parseInt(teile[1]);
        Base64.Decoder b64 = Base64.getDecoder();
        byte[] salt = b64.decode(teile[2]);
        byte[] erwartet = b64.decode(teile[3]);
        byte[] berechnet = berechne(passwort, salt, runden);
        return MessageDigest.isEqual(erwartet, berechnet);
    }

    private static byte[] berechne(String passwort, byte[] salt, int runden) {
        PBEKeySpec spec = new PBEKeySpec(passwort.toCharArray(), salt, runden, HASH_BITS);
        try {
            SecretKeyFactory fabrik = SecretKeyFactory.getInstance(ALGORITHMUS);
            return fabrik.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Passwort-Hash nicht moeglich", e);
        } finally {
            spec.clearPassword();
        }
    }
}
