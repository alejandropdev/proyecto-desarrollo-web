package com.muk.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {

    // You can use a more robust secret or inject from application.properties
    @Value("${jwt.secret:dGhpcy1pcy1hLXZlcnktc2VjdXJlLWtleS1mb3Itand0LW11ay1wcm9qZWN0LTEyMzQ1Njc4kw==}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private int expiration;

    private Key secretKey;

    @PostConstruct
    protected void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expiration))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException e) {
            System.err.println("Token mal formado");
        } catch (UnsupportedJwtException e) {
            System.err.println("Token no soportado");
        } catch (ExpiredJwtException e) {
            System.err.println("Token expirado");
        } catch (IllegalArgumentException e) {
            System.err.println("Token vacío");
        } catch (SecurityException e) {
            System.err.println("Fallo en la firma");
        }
        return false;
    }
}
