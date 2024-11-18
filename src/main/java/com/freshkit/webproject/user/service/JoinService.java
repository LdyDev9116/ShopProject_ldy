package com.freshkit.webproject.user.service;

import com.freshkit.webproject.user.dto.JoinEntity;
import com.freshkit.webproject.user.dto.UserEntity;
import com.freshkit.webproject.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * JoinService는 회원가입과 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * - 회원가입 프로세스, 아이디 중복 확인 등의 기능을 제공합니다.
 */
@Service
public class JoinService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * JoinService 생성자.
     * @param userMapper 사용자 관련 데이터베이스 작업을 처리하는 매퍼
     * @param bCryptPasswordEncoder 비밀번호 암호화를 위한 BCryptPasswordEncoder 인스턴스
     */
    @Autowired
    public JoinService(UserMapper userMapper, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userMapper = userMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * 회원가입 처리를 수행합니다.
     * - 입력된 회원 정보를 기반으로 새로운 사용자를 데이터베이스에 저장합니다.
     * - 회원 아이디가 이미 존재하면 예외를 발생시킵니다.
     *
     * @param joinEntity 회원가입 정보를 담고 있는 DTO 객체
     * @throws IllegalArgumentException 이미 존재하는 사용자 이름인 경우 발생
     */
    public void joinProcess(JoinEntity joinEntity) {
        String memberId = joinEntity.getMemberId();
        String pwd = joinEntity.getPassword();

        // 사용자 아이디가 이미 존재하는지 확인
        Boolean isExist = userMapper.existsByUsername(memberId);
        if (isExist) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(pwd);

        // UserEntity 객체 생성 및 데이터 설정
        UserEntity data = new UserEntity();
        data.setMemberId(memberId);
        data.setPwd(encodedPassword);
        data.setName(joinEntity.getUsername());
        data.setEmail(joinEntity.getEmail());
        data.setPhone(joinEntity.getPhone());
        data.setRole("ROLE_USER");

        // 데이터베이스에 사용자 저장
        userMapper.insertUser(data);
        System.out.println("회원 가입을 진행합니다.");
    }

    /**
     * 아이디가 이미 존재하는지 확인합니다.
     * - 입력된 아이디가 데이터베이스에 존재하는지 여부를 반환합니다.
     *
     * @param memberId 확인할 사용자 아이디
     * @return 존재하면 true, 존재하지 않으면 false
     */
    public boolean checkMemberIdExists(String memberId) {
        return userMapper.existsByUsername(memberId);
    }
}
