package com.goodsmoa.goodsmoa_BE.user.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import com.goodsmoa.goodsmoa_BE.user.Repository.UserRepository;

@Service // 이 클래스가 서비스임을 Spring에게 알려주는 어노테이션
public class UserService {

    @Autowired
    private UserRepository userRepository; // UsersRepository 의존성 주입


    // ID로 사용자 조회
    public User getUserById(String id) {
        // findById는 Optional을 반환하므로, orElseThrow()로 존재하지 않을 경우 예외 처리
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 ID의 사용자를 찾을 수 없습니다."));
    }
}
