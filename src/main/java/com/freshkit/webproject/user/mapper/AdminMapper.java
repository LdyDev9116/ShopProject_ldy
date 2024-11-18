package com.freshkit.webproject.user.mapper;

import com.freshkit.webproject.user.dto.AdminDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AdminMapper는 사용자 관리와 관련된 데이터베이스 작업을 처리하는 MyBatis 매퍼 인터페이스입니다.
 * - 사용자 조회, 생성, 업데이트, 삭제 기능을 제공합니다.
 */
@Mapper
public interface AdminMapper {

    /**
     * 모든 사용자 목록을 조회합니다.
     *
     * @return List<AdminDto> 형태의 사용자 목록
     */
    List<AdminDto> getAllUsers();

    /**
     * 특정 사용자 정보를 조회합니다.
     *
     * @param memberId 조회할 사용자의 고유 ID
     * @return AdminDto 형태의 사용자 정보
     */
    AdminDto getUserById(String memberId);

    /**
     * 새로운 사용자를 데이터베이스에 삽입합니다.
     *
     * @param user 삽입할 사용자 정보가 담긴 AdminDto 객체
     */
    void insertUser(AdminDto user);

    /**
     * 기존 사용자의 정보를 업데이트합니다.
     *
     * @param user 업데이트할 사용자 정보가 담긴 AdminDto 객체
     */
    void updateUser(AdminDto user);

    /**
     * 특정 사용자를 데이터베이스에서 삭제합니다.
     *
     * @param memberId 삭제할 사용자의 고유 ID
     */
    void deleteUser(String memberId);
}
