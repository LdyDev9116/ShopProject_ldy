package com.freshkit.webproject.user.service;

import com.freshkit.webproject.jwt.JwtUtil;
import com.freshkit.webproject.user.dto.UserEntity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AuthService는 사용자 인증 및 JWT 관련 작업을 처리하는 서비스 클래스입니다.
 * - 로그인 상태 확인, 사용자 인증, JWT 토큰에서 사용자 정보 추출 등의 기능을 제공합니다.
 */
@Service
@Log4j2
public class AuthService {

    private final JwtUtil jwtUtil;

    /**
     * AuthService 생성자.
     * @param jwtUtil JWT 관련 작업을 처리하는 유틸리티 클래스
     */
    @Autowired
    public AuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 사용자 인증을 처리합니다.
     * - 사용자 아이디와 비밀번호를 확인하여 인증된 사용자 정보를 반환합니다.
     * - 이 메서드는 예제용이며 실제 인증 로직이 필요합니다.
     *
     * @param memberId 사용자 아이디
     * @param password 사용자 비밀번호
     * @return 인증된 사용자 정보를 담은 UserEntity 객체 (인증 실패 시 null 반환)
     */
    public UserEntity authenticate(String memberId, String password) {
        // 실제 사용자 인증 로직 (예제)
        if ("testuser".equals(memberId) && "password".equals(password)) {
            return new UserEntity(memberId, password, "Test User", "testuser@example.com", "010-1234-5678", "USER");
        }
        return null;
    }

    /**
     * 사용자의 로그인 상태를 확인합니다.
     * - HTTP 요청의 쿠키에서 JWT 토큰을 추출하여 유효성을 검증합니다.
     *
     * @param request HTTP 요청 객체
     * @return 사용자가 로그인 상태인지 여부 (true: 로그인 상태, false: 비로그인 상태)
     */
    public Boolean isLoggedIn(HttpServletRequest request) {
        String jwtToken = jwtUtil.getCookieValue(request, "jwtToken");
        log.info("JWT Token: " + jwtToken);
        return jwtToken != null && jwtUtil.validateToken(jwtToken);
    }

    /**
     * JWT 토큰에서 사용자 아이디를 추출합니다.
     * - HTTP 요청의 쿠키에서 JWT 토큰을 추출하고, 해당 토큰이 유효하면 사용자 아이디를 반환합니다.
     *
     * @param request HTTP 요청 객체
     * @return 사용자 아이디 (토큰이 유효하지 않거나 존재하지 않을 경우 null 반환)
     */
    public String getUserId(HttpServletRequest request) {
        String jwtToken = jwtUtil.getCookieValue(request, "jwtToken");
        log.info("JWT Token: " + jwtToken);
        if (jwtToken != null && jwtUtil.validateToken(jwtToken)) {
            return jwtUtil.getMemberId(jwtToken);
        }
        return null;
    }
}
