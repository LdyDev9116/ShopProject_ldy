package com.freshkit.webproject.user.service;

import com.freshkit.webproject.user.dto.AdminDto;
import com.freshkit.webproject.user.mapper.AdminMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AdminService는 사용자 관리와 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * - 사용자의 조회, 생성, 업데이트, 삭제 기능을 제공합니다.
 */
@Service
public class AdminService {

    private final AdminMapper adminMapper;

    /**
     * AdminService 생성자.
     * @param adminMapper 사용자 관리와 관련된 데이터베이스 작업을 처리하는 매퍼
     */
    public AdminService(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    /**
     * 모든 사용자 목록을 조회합니다.
     * @return List<AdminDto> 형태의 사용자 목록
     */
    public List<AdminDto> getAllUsers() {
        return adminMapper.getAllUsers();
    }

    /**
     * 사용자를 저장하거나, 이미 존재하는 경우 업데이트합니다.
     * - 사용자 ID가 존재하지 않으면 새로운 사용자로 추가하고, 존재하면 정보를 업데이트합니다.
     *
     * @param user 저장하거나 업데이트할 사용자 정보를 담은 AdminDto 객체
     */
    public void saveOrUpdateUser(AdminDto user) {
        if (adminMapper.getUserById(user.getMemberId()) == null) {
            adminMapper.insertUser(user);
        } else {
            adminMapper.updateUser(user);
        }
    }

    /**
     * 특정 사용자의 정보를 조회합니다.
     *
     * @param memberId 조회할 사용자의 고유 ID
     * @return AdminDto 형태의 사용자 정보
     */
    public AdminDto getUserById(String memberId) {
        return adminMapper.getUserById(memberId);
    }

    /**
     * 특정 사용자를 삭제합니다.
     *
     * @param memberId 삭제할 사용자의 고유 ID
     */
    public void deleteUser(String memberId) {
        adminMapper.deleteUser(memberId);
    }
}
