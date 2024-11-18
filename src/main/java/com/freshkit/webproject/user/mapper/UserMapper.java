package com.freshkit.webproject.user.mapper;

import com.freshkit.webproject.user.dto.UserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

/**
 * UserMapper는 사용자와 관련된 데이터베이스 작업을 처리하는 MyBatis 매퍼 인터페이스입니다.
 * - 사용자 생성, 조회, 존재 여부 확인 등의 기능을 제공합니다.
 */
@Mapper
public interface UserMapper {

    /**
     * 주어진 사용자 ID(memberId)가 데이터베이스에 존재하는지 확인합니다.
     *
     * @param memberId 확인할 사용자 ID
     * @return 존재하면 true, 존재하지 않으면 false
     */
    boolean existsByUsername(String memberId);

    /**
     * 새로운 사용자를 데이터베이스에 삽입합니다.
     *
     * @param user 삽입할 사용자 정보를 담은 UserEntity 객체
     */
    void insertUser(UserEntity user);

    /**
     * 주어진 사용자 ID(memberId)에 해당하는 사용자 정보를 조회합니다.
     *
     * @param memberId 조회할 사용자 ID
     * @return UserEntity 객체로 반환 (사용자가 존재하지 않으면 null 반환 가능)
     */
    UserEntity findByUsername(String memberId);

    /**
     * 주어진 사용자 ID(memberId)와 역할(role)을 기준으로 사용자 정보를 조회합니다.
     *
     * @param memberId 조회할 사용자 ID
     * @return Optional<UserEntity> 형태로 사용자 정보를 반환
     */
    Optional<UserEntity> findByUserIdrole(String memberId);
}
