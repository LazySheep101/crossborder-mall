package com.crossborder.mall.security;

import com.crossborder.mall.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    public String createToken(Long userId, String nickName, String role) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + jwtProperties.getExpirationMs());
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("nickName", nickName)
                .claim("role", role == null ? "USER" : role)
                .issuedAt(now)
                .expiration(exp)
                .signWith(secretKey())
                .compact();
    }

    public Long getUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    public String getNickName(String token) {
        Object v = parseClaims(token).get("nickName");
        return v == null ? null : String.valueOf(v);
    }

    public String getRole(String token) {
        Object v = parseClaims(token).get("role");
        return v == null ? "USER" : String.valueOf(v);
    }

    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Date getExpiration(String token) {
        return parseClaims(token).getExpiration();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey secretKey() {
        byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes.length >= 32 ? keyBytes : pad(keyBytes));
    }

    private byte[] pad(byte[] src) {
        byte[] out = new byte[32];
        System.arraycopy(src, 0, out, 0, Math.min(src.length, 32));
        return out;
    }
}
