package org.example.security;


import lombok.Data;
import org.example.exceptions.InvalidSecretKeyException;

@Data
public class SecurityData {

    private byte[] secretKey;

    private byte[] interactionKey;

    private Cryptographer cryptographer;

    public SecurityData(Cryptographer cryptographer, String secret) {
        this.cryptographer = cryptographer;
        this.secretKey = cryptographer.encrypt(secret.getBytes());
    }

    public void setInteractionKey(String secretKey, String interactionKey) throws InvalidSecretKeyException {

        if(!checkSecretKey(secretKey))
            throw new InvalidSecretKeyException();

        this.interactionKey = cryptographer.encrypt(interactionKey.getBytes());
    }

    public boolean checkSecretKey(String secretKey) {

        return secretKey != null && new String(cryptographer.decrypt(this.secretKey)).equals(secretKey);
    }

    public boolean checkInteractionKey(String interactionKey){

        return interactionKey != null && new String(cryptographer.decrypt(this.interactionKey)).equals(interactionKey);
    }
}
