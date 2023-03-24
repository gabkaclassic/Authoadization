package org.example.security;


import lombok.Data;
import org.example.exceptions.InvalidSecretKeyException;
import org.example.utils.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Data
public class SecurityData {

    private byte[] secretKey;

    private byte[] interactionKey;

    private Cryptographer cryptographer;

    private final JwtUtil jwtUtil;

    public SecurityData(Cryptographer cryptographer, String secret, JwtUtil jwtUtil) {
        this.cryptographer = cryptographer;
        this.jwtUtil = jwtUtil;
        this.secretKey = cryptographer.encrypt(secret.getBytes());
    }

    public String getInteractionKey() {
        return new String(interactionKey);
    }
    public ResponseEntity<String> setInteractionKey(String secretKey, String interactionKey) {

        if(!checkSecretKey(secretKey))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid secret key");

        jwtUtil.setKey(interactionKey.getBytes());

        return ResponseEntity.ok().build();
    }

    public boolean checkSecretKey(String secretKey) {

        return secretKey != null && cryptographer.matches(this.secretKey, secretKey);
    }
}
