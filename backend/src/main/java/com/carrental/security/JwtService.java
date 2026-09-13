package com.carrental.security;

import com.carrental.modules.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtService {
    private final SecretKey signingKey;
    private final long accessTokenExpirationMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.access-token-expiration-ms:900000}") long accessTokenExpirationMs) {
        if (secret.length() < 32) throw new IllegalArgumentException("JWT secret must contain at least 32 characters");
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMs = accessTokenExpirationMs;
    }

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        List<String> roles = user.getRoles().stream().map(role -> role.getName()).toList();
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessTokenExpirationMs)))
                .signWith(signingKey)
                .compact();
    }

    public boolean isValid(String token) {
        try { parseClaims(token); return true; }
        catch (RuntimeException exception) { return false; }
    }

    public String getSubject(String token) { return getClaim(token, Claims::getSubject); }
    public List<String> getRoles(String token) { return parseClaims(token).get("roles", List.class); }
    public long getAccessTokenExpirationMs() { return accessTokenExpirationMs; }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
    }

    private <T> T getClaim(String token, Function<Claims, T> resolver) { return resolver.apply(parseClaims(token)); }
}