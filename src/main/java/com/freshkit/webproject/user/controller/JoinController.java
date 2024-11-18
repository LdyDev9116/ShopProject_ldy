package com.freshkit.webproject.user.controller;

import com.freshkit.webproject.user.dto.JoinEntity;
import com.freshkit.webproject.user.service.JoinService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * JoinController는 회원가입 관련 요청을 처리하는 컨트롤러입니다.
 * - 회원가입 페이지 이동, 회원가입 처리, 아이디 존재 여부 확인 기능을 제공합니다.
 */
@Controller
public class JoinController {

    private final JoinService joinService;

    /**
     * JoinController 생성자.
     * @param joinService 회원가입과 관련된 비즈니스 로직을 처리하는 서비스
     */
    @Autowired
    public JoinController(JoinService joinService) {
        this.joinService = joinService;
    }

    /**
     * 회원가입 페이지로 이동합니다.
     * - 빈 JoinEntity 객체를 모델에 추가하여 Thymeleaf 템플릿에 전달합니다.
     *
     * @param model 뷰에 데이터를 전달하는 모델 객체
     * @return "account-signup" 뷰 이름 (회원가입 페이지)
     */
    @GetMapping("/account-signup")
    public String loginPage2(Model model) {
        model.addAttribute("joinEntity", new JoinEntity());
        return "account-signup";
    }

    /**
     * 회원가입 처리를 수행합니다.
     * - JoinEntity 객체를 받아 회원가입 프로세스를 수행하며, 성공 시 성공 메시지를 반환하고, 실패 시 에러 메시지를 반환합니다.
     *
     * @param joinEntity 가입할 회원 정보를 담고 있는 DTO 객체
     * @return 회원가입 성공 시 HTTP 200 응답과 성공 메시지, 실패 시 HTTP 409 응답과 에러 메시지
     */
    @PostMapping("/account-signup")
    public ResponseEntity<String> join2(@ModelAttribute JoinEntity joinEntity) {
        try {
            joinService.joinProcess(joinEntity);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    /**
     * 아이디가 이미 존재하는지 확인합니다.
     * - 클라이언트에서 전달한 아이디가 데이터베이스에 이미 존재하는지 여부를 반환합니다.
     *
     * @param memberId 확인할 사용자 아이디
     * @return 아이디가 존재하면 true, 존재하지 않으면 false와 함께 HTTP 200 응답
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> checkMemberIdExists(@RequestParam String memberId) {
        boolean exists = joinService.checkMemberIdExists(memberId);
        return ResponseEntity.ok(exists);
    }
}
