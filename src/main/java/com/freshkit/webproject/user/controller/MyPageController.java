package com.freshkit.webproject.user.controller;

import com.freshkit.webproject.jwt.JwtUtil;
import com.freshkit.webproject.product.dto.OrderDto;
import com.freshkit.webproject.user.service.AuthService;
import com.freshkit.webproject.user.service.MyPageService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * MyPageController는 사용자 마이페이지 관련 요청을 처리하는 컨트롤러입니다.
 * - JWT 토큰을 통해 사용자를 인증하고, 주문 내역을 조회하여 프로필 페이지에 표시합니다.
 */
@Controller
public class MyPageController {

    private static final Logger logger = LoggerFactory.getLogger(MyPageController.class);
    private final JwtUtil jwtUtil;
    private final MyPageService myPageService;

    @Autowired
    private AuthService authService;

    /**
     * MyPageController 생성자.
     * @param jwtUtil JWT 관련 유틸리티 클래스
     * @param myPageService 마이페이지 관련 비즈니스 로직을 처리하는 서비스
     */
    public MyPageController(JwtUtil jwtUtil, MyPageService myPageService) {
        this.jwtUtil = jwtUtil;
        this.myPageService = myPageService;
    }

    /**
     * 사용자 프로필 페이지를 반환합니다.
     * - JWT 토큰을 통해 사용자를 인증하고, 사용자의 주문 내역을 조회하여 모델에 추가합니다.
     *
     * @param request HTTP 요청 객체로, 쿠키에서 JWT 토큰을 가져오는 데 사용됩니다.
     * @param model 뷰에 데이터를 전달하기 위한 모델 객체
     * @return "account-profile" 뷰 이름 (프로필 페이지)
     */
    @GetMapping("/account-profile")
    public String accountProfilePage(HttpServletRequest request, Model model) {
        String jwtToken = getCookieValue(request, "jwtToken");

        // 로그인 상태 확인 후 모델에 추가
        Boolean isLoggedIn = authService.isLoggedIn(request);
        model.addAttribute("isLoggedIn", isLoggedIn != null && isLoggedIn);

        // JWT 토큰이 유효한지 확인하고 사용자 주문 내역 조회
        if (jwtToken != null && !jwtToken.isEmpty() && !jwtUtil.isExpired(jwtToken)) {
            Claims claims = jwtUtil.getClaims(jwtToken);
            String memberId = String.valueOf(claims.getSubject());

            List<OrderDto> orders = myPageService.getUserOrders(memberId);
            logger.info("memberId: {}", memberId);
            logger.info("Orders: {}", orders); // 주문 내역 확인을 위한 로그

            // 주문 내역이 없을 경우 메시지와 빈 리스트 추가
            if (orders == null || orders.isEmpty()) {
                model.addAttribute("orders", List.of()); // 빈 리스트로 초기화
                model.addAttribute("message", "주문 내역이 없습니다.");
            } else {
                model.addAttribute("orders", orders);
            }
        } else {
            model.addAttribute("message", "JWT Token is missing or invalid");
        }

        return "account-profile";
    }

    /**
     * 요청의 쿠키에서 지정된 이름의 값을 가져옵니다.
     * - 쿠키가 존재하지 않을 경우 null을 반환합니다.
     *
     * @param request HTTP 요청 객체
     * @param name 쿠키의 이름
     * @return 쿠키 값 또는 null (쿠키가 존재하지 않는 경우)
     */
    private String getCookieValue(HttpServletRequest request, String name) {
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
}
