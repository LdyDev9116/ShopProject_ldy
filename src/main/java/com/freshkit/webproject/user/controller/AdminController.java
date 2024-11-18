package com.freshkit.webproject.user.controller;

import com.freshkit.webproject.user.dto.AdminDto;
import com.freshkit.webproject.user.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AdminController는 관리자 페이지와 관련된 요청을 처리하는 컨트롤러입니다.
 * - 관리자 페이지 조회
 * - 사용자 생성 및 수정
 * - 사용자 조회 및 삭제 등의 기능을 제공합니다.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * 관리자 페이지를 반환하는 메서드.
     * - 모든 사용자의 목록을 조회하여 관리자 페이지에 전달합니다.
     *
     * @param model 뷰에 데이터를 전달하는 모델 객체
     * @return "admin" 뷰 이름 (관리자 페이지)
     */
    @GetMapping
    public String adminPage(Model model) {
        List<AdminDto> users = adminService.getAllUsers();
        model.addAttribute("users", users);
        return "admin";
    }

    /**
     * 사용자를 생성하거나 업데이트하는 메서드.
     * - `AdminDto` 객체를 사용하여 새로운 사용자 정보를 저장하거나 기존 사용자를 업데이트합니다.
     *
     * @param user 생성 또는 업데이트할 사용자 정보를 담은 DTO 객체
     * @return HTTP 200 응답 (성공적으로 처리된 경우)
     */
    @PostMapping("/user")
    @ResponseBody
    public ResponseEntity<Void> createOrUpdateUser(@ModelAttribute AdminDto user) {
        adminService.saveOrUpdateUser(user);
        return ResponseEntity.ok().build();
    }

    /**
     * 특정 사용자 정보를 조회하는 메서드.
     * - `memberId`에 해당하는 사용자의 정보를 조회합니다.
     *
     * @param memberId 조회할 사용자의 고유 ID
     * @return 사용자의 정보를 담은 `AdminDto` 객체와 HTTP 200 응답
     */
    @GetMapping("/user/{memberId}")
    @ResponseBody
    public ResponseEntity<AdminDto> getUser(@PathVariable String memberId) {
        AdminDto user = adminService.getUserById(memberId);
        return ResponseEntity.ok(user);
    }

    /**
     * 특정 사용자를 삭제하는 메서드.
     * - `memberId`에 해당하는 사용자를 삭제합니다.
     *
     * @param memberId 삭제할 사용자의 고유 ID
     * @return HTTP 200 응답 (성공적으로 처리된 경우)
     */
    @DeleteMapping("/user/{memberId}")
    @ResponseBody
    public ResponseEntity<Void> deleteUser(@PathVariable String memberId) {
        adminService.deleteUser(memberId);
        return ResponseEntity.ok().build();
    }
}
