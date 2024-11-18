package com.freshkit.webproject.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${spring.jwt.secret}")
    private String secret;

    @Value("${spring.jwt.expiration}")
    private long expirationTime;

    @Value("${spring.jwt.refreshExpiration}")
    private long refreshExpirationTime;

    public String createJwt(String memberId, String role, long expirationTime) {
        return createToken(memberId, role, expirationTime);
    }

    public String createRefreshToken(String memberId, String role, long expirationTime) {
        return createToken(memberId, role, expirationTime);
    }

    private String createToken(String memberId, String role, long expirationTime) {
        String token = Jwts.builder()
                .setSubject(memberId) // 사용자 ID를 토큰의 Subject로 설정
                .claim("role", role) // 사용자 역할을 클레임에 포함
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 발급 시간
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 만료 시간 설정
                .signWith(SignatureAlgorithm.HS256, secret) // 서명 알고리즘과 비밀 키를 사용해 서명
                .compact(); // 토큰 생성
        logger.info("토큰 생성: {}", token); // 로그로 생성된 토큰 출력
        return token; // 생성된 토큰 반환
    }

    public Boolean isExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            logger.error("토큰 만료 확인 중 오류 발생: {}", token, e);
            return true;
        }
    }

    public String getMemberId(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String getRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            logger.error("토큰에서 클레임을 추출하는 중 오류 발생: {}", token, e);
            throw e;
        }
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public Boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.error("JWT 토큰이 null이거나 빈 문자열입니다");
            return false;
        }
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            logger.info("JWT 유효성 검증 완료: {}", token);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("잘못된 JWT 토큰: {}", token, e);
            return false;
        } catch (ExpiredJwtException e) {
            logger.error("만료된 JWT 토큰: {}", token, e);
            return false;
        } catch (Exception e) {
            logger.error("유효하지 않은 JWT 토큰: {}", token, e);
            return false;
        }
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public long getRefreshExpirationTime() {
        return refreshExpirationTime;
    }

    public String getCookieValue(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(name)) {
                    logger.info("쿠키 발견 {}: {}", name, cookie.getValue());
                    return cookie.getValue();
                }
            }
        }
        logger.warn("쿠키 {}을(를) 찾을 수 없습니다", name);
        return null;
    }

    // 토큰의 남은 유효 기간 계산 메소드 추가
    public long getRemainingTime(String token) {
        try {
            Date expiration = extractExpiration(token);
            long remainingTime = expiration.getTime() - System.currentTimeMillis();
            return remainingTime > 0 ? remainingTime : 0;
        } catch (Exception e) {
            logger.error("토큰 남은 유효 기간 계산 중 오류 발생: {}", token, e);
            return 0;
        }
    }

    // 토큰 연장 메소드 추가
    public String extendToken(String token, long additionalTime) {
        try {
            Claims claims = extractAllClaims(token);
            String memberId = claims.getSubject();
            String role = claims.get("role", String.class);
            long newExpirationTime = additionalTime;
            return createToken(memberId, role, newExpirationTime);
        } catch (Exception e) {
            logger.error("토큰 연장 중 오류 발생: {}", token, e);
            return null;
        }
    }

    // getClaims 메소드 추가
    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            logger.error("토큰에서 클레임을 추출하는 중 오류 발생: {}", token, e);
            throw e;
        }
    }
}
