package com.movieproject.common.security;

import com.movieproject.domain.user.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    // 블랙리스트 저장용
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    // 토큰과 만료시간 매핑 (메모리 정리용)
    private final Map<String, Long> tokenExpirations = new ConcurrentHashMap<>();

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration:1800000}") long accessTokenExpiration, // 30분
            @Value("${jwt.refresh-token-expiration:1209600000}") long refreshTokenExpiration // 2주
    ) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);

        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public String createAccessToken(Long userId, String username, Role role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .claim("role", role.name())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            // 블랙리스트 확인 먼저
            if (isTokenBlacklisted(token)) {
                log.error("블랙리스트에 등록된 토큰입니다");
                return false;
            }

            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT 토큰이 만료되었습니다");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다");
        } catch (MalformedJwtException e) {
            log.error("JWT 토큰이 올바르지 않습니다");
        } catch (SecurityException e) {
            log.error("JWT 서명이 올바르지 않습니다");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 비어있습니다");
        }
        return false;
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(claims.getSubject());
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("username", String.class);
    }

    // 토큰 무효화 (블랙리스트에 추가)
    public void invalidateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            /**

            블랙리스트에 추가 이게 중요한 핵심로직

                  * */
            blacklistedTokens.add(token);
            // 만료시간도 저장 (메모리 정리용)
            tokenExpirations.put(token, claims.getExpiration().getTime());

            log.info("토큰이 블랙리스트에 추가되었습니다");
        } catch (Exception e) {
            log.error("토큰 무효화 중 오류 발생", e);
        }
    }

    // 블랙리스트 확인
    public boolean isTokenBlacklisted(String token) {
        // 만료된 토큰들 정리
        cleanupExpiredTokens();
        return blacklistedTokens.contains(token);
    }

    // 만료된 토큰들을 블랙리스트에서 제거 (메모리 절약)
    private void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        tokenExpirations.entrySet().removeIf(entry -> {
            if (entry.getValue() < now) {
                blacklistedTokens.remove(entry.getKey());
                return true;
            }
            return false;
        });
    }
}