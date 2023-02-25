package org.example.security;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class Cryptographer {

    private Cipher cipher;
    private SecretKey key;

    public Cryptographer(
            String algorithmKey,
            String algorithmCipher,
            String key
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

    public byte[] encrypt(byte[] input) { return processString(input, Cipher.ENCRYPT_MODE); }
    public byte[] decrypt(byte[] input) { return processString(input, Cipher.DECRYPT_MODE); }
}
