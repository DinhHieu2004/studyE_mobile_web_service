package com.example.studyE.util;

import com.example.studyE.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    private final String jwtSecret = "OTabde1FltyqeE78jvxioy3lyxbK2kNkL9UoPkwT4sXr3vMQx9HeQZ0V1jDyLU18";

    private final long expirationMs = 99900000;

    private final Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

    public static String extractUid(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey("OTabde1FltyqeE78jvxioy3lyxbK2kNkL9UoPkwT4sXr3vMQx9HeQZ0V1jDyLU18".getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Không thể trích xuất UID từ token", e);
        }
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUid())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .claim("userId", user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUidFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return String.valueOf(claims.get("userId", Long.class));
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract userId from token", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("email", String.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract userId from token", e);
        }
    }

    public static Long getUserIdFromToken(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof Map) {
            Map<String, Object> userDetails = (Map<String, Object>) principal;
            String userIdStr = userDetails.get("userId").toString();
            Long userId = Long.valueOf(userIdStr);

           return userId;
        }
        return null;
    }

}
