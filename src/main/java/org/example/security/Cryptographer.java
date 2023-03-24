package org.example.security;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Cryptographer {

    private Cipher cipher;
    private SecretKey key;

    public Cryptographer(
            @Value("${encryption.algorithm.key}") String algorithmKey,
            @Value("${encryption.algorithm.cipher}") String algorithmCipher,
            @Value("${encryption.key}") String key
    ) throws NoSuchPaddingException, NoSuchAlgorithmException {

        this.key = new SecretKeySpec(key.getBytes(), algorithmKey);
        cipher = Cipher.getInstance(algorithmCipher);
    }

    private byte[] processString(byte[] input, int mode) {

        try {
            cipher.init(mode, key);

            return cipher.doFinal(input);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] encrypt(byte[] input) { return (input == null) ? null : processString(input, Cipher.ENCRYPT_MODE); }
    public byte[] decrypt(byte[] input) { return  (input == null) ? null : processString(input, Cipher.DECRYPT_MODE); }
    public boolean matches(byte[] crypt, String value) { return new String(decrypt(crypt)).equals(value); }
}
