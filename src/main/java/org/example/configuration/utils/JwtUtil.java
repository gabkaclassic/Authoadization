package org.example.configuration.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.accounts.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Component
public class JwtUtil {

    private final byte[] secret;

    private final Long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") String expiration) {
        this.secret = secret.getBytes();
        this.expiration = Long.parseLong(expiration);
    }

    public String extractUsername(String authToken) {

        String key = Base64.getEncoder().encodeToString(secret);
        String subject;

        try {

            subject = Jwts.parserBuilder().setSigningKey(key).build()
                    .parseClaimsJws(authToken).getBody()
                    .getSubject();
        }
        catch (Exception e) {
            subject = null;
            System.out.println(e);
        }

        return subject;
    }

    public Claims getClaimsFromToken(String authToken) {

        return Jwts.parserBuilder().setSigningKey(secret).build()
                .parseClaimsJws(authToken).getBody();
    }

    public boolean validateToken(String authToken) {

        return getClaimsFromToken(authToken)
                .getExpiration().before(new Date());
    }

    public String generateToken(Account account) {

        var claims = new HashMap<String, Object>();
        claims.put("role", account.getAuthorities());

        var creation = new Date();
        var expiration = new Date(creation.getTime() + this.expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(account.getUsername())
                .setIssuedAt(creation)
                .setExpiration(expiration)
                .signWith(Keys.hmacShaKeyFor(secret))
                .compact();
    }
}
