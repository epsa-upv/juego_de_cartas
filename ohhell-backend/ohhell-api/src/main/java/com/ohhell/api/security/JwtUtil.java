package com.ohhell.api.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

public class JwtUtil {

    private static final Key KEY =
            Keys.hmacShaKeyFor(
                    "super-secret-key-super-secret-key-123456".getBytes()
            );

    private static final long EXPIRATION_MS = 1000L * 60 * 60 * 24;

    public static String generateToken(UUID userId, String email) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Claims validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(KEY)
                .parseClaimsJws(token)
                .getBody();
    }

    public static UUID getUserIdFromToken(String token) {
        return UUID.fromString(validateToken(token).getSubject());
    }

    public static String getEmailFromToken(String token) {
        return validateToken(token).get("email", String.class);
    }
}
