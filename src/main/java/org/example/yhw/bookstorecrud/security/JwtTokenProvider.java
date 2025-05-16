package org.example.yhw.bookstorecrud.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private final String jwtSecret = "YourSecretKeyReplaceMefjoafljaflajsdlfjsdfkajslfkjsdlfajflakjfalskdfjasldkfjasldfjaslfkjsdflasjflasjflsajflsafjaslkfjaslkfjasklfjsljlksjfalsjfslk";
    private final long jwtExpirationInMs = 86400000; // 1 day

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
