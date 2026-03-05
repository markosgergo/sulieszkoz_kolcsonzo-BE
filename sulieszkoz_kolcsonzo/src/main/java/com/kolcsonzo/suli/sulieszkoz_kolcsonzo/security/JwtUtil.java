package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    // (Éles ipari projektekben ezt az application.properties-ből szokták beolvasni)
    private final String SECRET = "SuliEszkozKolcsonzoNagyonTitkosKulcs2024BackendApp";
    private final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // 10 óráig érvényes a token
    private final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60 * 10;

    // Email cím (username) kinyerése a tokenből
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Lejárat ellenőrzése
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // A token létrehozása
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // Beletesszük a szerepkört is a tokenbe, hogy a frontend is lássa
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());

        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // Ez az email cím lesz
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SECRET_KEY)
                .compact();
    }

    // A token ellenőrzése
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}