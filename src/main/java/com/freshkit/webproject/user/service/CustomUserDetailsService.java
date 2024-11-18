package com.freshkit.webproject.user.service;

import com.freshkit.webproject.user.dto.CustomUserDetails;
import com.freshkit.webproject.user.dto.UserEntity;
import com.freshkit.webproject.user.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * CustomUserDetailsService는 Spring Security의 UserDetailsService를 구현하여
 * 사용자 인증과 관련된 로직을 처리합니다.
 * - 데이터베이스에서 사용자 정보를 조회하고, 이를 기반으로 인증 객체를 생성합니다.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    /**
     * CustomUserDetailsService 생성자.
     * @param userMapper 사용자 정보 조회를 위한 매퍼
     */
    public CustomUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 사용자 이름으로 사용자 정보를 로드합니다.
     * - 데이터베이스에서 사용자를 조회하여 Spring Security가 사용하는 UserDetails 객체를 반환합니다.
     *
     * @param memberId 조회할 사용자의 고유 ID (username)
     * @return UserDetails Spring Security에서 사용하는 사용자 정보 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우 발생
     */
    @Override
    public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException {
        // 데이터베이스에서 사용자 정보 조회
        UserEntity userData = userMapper.findByUsername(memberId);

        // 사용자 정보가 없을 경우 예외 처리
        if (userData == null) {
            throw new UsernameNotFoundException("User not found with username: " + memberId);
        }

        // 사용자 정보를 기반으로 CustomUserDetails 객체 생성 및 반환
        return new CustomUserDetails(userData);
    }
}
