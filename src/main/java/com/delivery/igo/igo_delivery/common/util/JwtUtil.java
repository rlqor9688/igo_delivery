//package com.delivery.igo.igo_delivery.common.util;
//
//import com.delivery.igo.igo_delivery.api.user.entity.UserRole;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
////import org.example.expert.domain.common.exception.ServerException;
////import org.example.expert.domain.user.enums.UserRole;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.rmi.ServerException;
//import java.security.Key;
//import java.util.Base64;
//import java.util.Date;
//
//@Slf4j(topic = "JwtUtil")
//@Component
//public class JwtUtil {
//
//    private static final String BEARER_PREFIX = "Bearer ";
//    private static final long TOKEN_TIME = 60 * 60 * 1000L; // 60분
//
//    @Value("${jwt.secret.key}")
//    private String secretKey;
//    private Key key;
//    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
//
//    @PostConstruct
//    public void init() {
//        byte[] bytes = Base64.getDecoder().decode(secretKey);
//        key = Keys.hmacShaKeyFor(bytes);
//    }
//
//    public String createToken(Long userId, String email, UserRole userRole) {
//        Date date = new Date();
//
//        return BEARER_PREFIX +
//                Jwts.builder()
//                        .setSubject(String.valueOf(userId))
//                        .claim("email", email)
//                        .claim("userRole", userRole)
//                        .setExpiration(new Date(date.getTime() + TOKEN_TIME))
//                        .setIssuedAt(date) // 발급일
//                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
//                        .compact();
//    }
//
//    public String substringToken(String tokenValue) {
//        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
//            return tokenValue.substring(7);
//        }
//        throw new ServerException("Not Found Token");
//    }
//
//    public Claims extractClaims(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public Long getUserId(String token) {
//        Claims claims = extractClaims(token);
//        return Long.valueOf(claims.getSubject());
//    }
//
//    public String getUserEmail(String token) {
//        Claims claims = extractClaims(token);
//        return claims.get("email", String.class);
//    }
//
//    public UserRole getUserRole(String token) {
//        Claims claims = extractClaims(token);
//        String roleString = claims.get("userRole", String.class);
//
//        if (roleString == null) {
//            throw new ServerException("권한 정보가 없습니다.");
//        }
//
//        try {
//            return UserRole.valueOf(roleString);
//        } catch (IllegalArgumentException e) {
//            throw new ServerException("유효하지 않은 사용자 권한입니다.");
//        }
//    }
//}
