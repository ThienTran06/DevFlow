package com.hamy.devflow.auth;

import com.hamy.devflow.user.CustomUserDetailsService;
import com.hamy.devflow.user.User;
import com.hamy.devflow.user.UserRepository;
import com.hamy.devflow.user.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.management.relation.Role;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;


@Service
public class JwtService {
    private final static String ACCESS_TOKEN_SECRET_KEY =  "my-super-secret-key-my-super-secret-key-123456";
    private final static String REFRESH_TOKEN_SERECT_KEY = "my-super-secret-key-my-super-secret-key-1234567";
    private final static long ACCESS_TOKEN_EXPIRATION_TIME = 1000*60*60*24;
    private final static long REFRESH_TOKEN_EXPIRATION_TIME = 1000*60*60*24*7;
    public String generateAccessToken(User user) {
        Map<String, Object> claims = Map.of(
                "role" , user.getRole().name()
        );
        return Jwts.builder()
                .claims(claims)
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(getAccessTokenSigningKey())
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(getRefreshTokenSigningKey())
                .compact();
    }
    private SecretKey getAccessTokenSigningKey() {
        byte[] keyBytes = ACCESS_TOKEN_SECRET_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private SecretKey getRefreshTokenSigningKey() {
        byte[] keyBytes = REFRESH_TOKEN_SERECT_KEY.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    private  Claims parseClaims(String token, SecretKey serectKey){
        return Jwts.parser()
                .verifyWith(serectKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Claims parseAccessClaims(String token) {
        return parseClaims(token, getAccessTokenSigningKey());
    }
    public Claims parseRefreshClaims(String token) {
        return parseClaims(token, getRefreshTokenSigningKey());
    }
    public String extractUserId(Claims claims) {
        return claims.getSubject();
    }
    public Date extractExpiration(Claims claims) {
        return claims.getExpiration();
    }
    public Date extractIssuedAt(Claims claims) {
        return claims.getIssuedAt();
    }
    public UserRole extractRole(String token) {
        String role = parseAccessClaims(token).get("role", String.class);
        return UserRole.valueOf(role);
    }



}
