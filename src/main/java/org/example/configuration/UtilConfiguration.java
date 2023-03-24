package org.example.configuration;

import org.example.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class UtilConfiguration {

    @Bean
    @Scope("singleton")
    public JwtUtil jwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") String expiration) {

        return new JwtUtil(secret, expiration);
    }
}
