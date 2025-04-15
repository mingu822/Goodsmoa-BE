package com.goodsmoa.goodsmoa_BE.user.Repository;

import com.goodsmoa.goodsmoa_BE.user.Entity.UserAccountEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRepository extends JpaRepository<UserAccountEntity, Long> {

    // 유저 기준으로 계좌 1개 조회 (1:1 관계)
    UserAccountEntity findByUser(UserEntity user);
}
