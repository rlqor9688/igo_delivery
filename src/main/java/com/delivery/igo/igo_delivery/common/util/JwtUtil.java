package com.delivery.igo.igo_delivery.common.util;

import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import com.delivery.igo.igo_delivery.common.exception.AuthException;
import com.delivery.igo.igo_delivery.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    private static final String BEARER_PREFIX = "Bearer ";

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.expiration.access}")
    private long tokenTimeSec;

    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createToken(Users savedUser) {
        Date date = new Date();

        // 토큰 발급
        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(savedUser.getId()))
                        .claim("email", savedUser.getEmail())
                        .claim("nickname", savedUser.getNickname())
                        .claim("userRole", savedUser.getUserRole())
                        .setExpiration(new Date(date.getTime() + tokenTimeSec * 1000))  // 만료 시간
                        .setIssuedAt(date)                                              // 발급 시간
                        .signWith(key, signatureAlgorithm)                              // 서명
                        .compact();                                                     // JWT 문자열 생성
    }

    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(BEARER_PREFIX.length());
        }
        throw new AuthException(ErrorCode.JWT_NOT_FOUND_TOKEN);
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserId(String token) {
        Claims claims = extractClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    public String getUserNickname(String token) {
        Claims claims = extractClaims(token);
        return claims.get("nickname", String.class);
    }

    public String getUserEmail(String token) {
        Claims claims = extractClaims(token);
        return claims.get("email", String.class);
    }

    public UserRole getUserRole(String token) {
        Claims claims = extractClaims(token);
        String roleString = claims.get("userRole", String.class);

        if (roleString == null) {
            throw new AuthException(ErrorCode.USER_NOT_FOUND_ROLE);
        }

        try {
            return UserRole.valueOf(roleString);
        } catch (IllegalArgumentException e) {
            throw new AuthException(ErrorCode.INVALID_USER_ROLE);
        }
    }
}
