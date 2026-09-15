package com.vehiclestation.auth.security;

import com.vehiclestation.auth.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long expirationMinutes;

    public JwtService(
            JwtEncoder jwtEncoder,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt =
                now.plus(expirationMinutes, ChronoUnit.MINUTES);

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getEmail())
                .claim("userId", user.getUserId())
                .claim("name", user.getFullName())
                .claim(
                        "roles",
                        List.of(user.getRole().name())
                )
                .build();

        return jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();
    }

    public long getExpirationSeconds() {
        return expirationMinutes * 60;
    }
}