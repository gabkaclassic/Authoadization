package org.example.configuration.utils;

import org.example.security.Cryptographer;
import org.example.security.SecurityData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class SecurityUtils {

    @Bean
    @Scope(scopeName = "singleton")
    public SecurityData data(@Autowired Cryptographer cryptographer, @Value("${secret_key}") String secret) {
        return new SecurityData(cryptographer, secret);
    }

    @Bean
    @Scope(scopeName = "singleton")
    public Cryptographer cryptographer(@Value("${encryption.algorithm.key}") String algorithmKey,
                                       @Value("${encryption.algorithm.cipher}") String algorithmCipher,
                                       @Value("${encryption.key}") String key) throws NoSuchPaddingException, NoSuchAlgorithmException {
        return new Cryptographer(algorithmKey, algorithmCipher, key);
    }
}
