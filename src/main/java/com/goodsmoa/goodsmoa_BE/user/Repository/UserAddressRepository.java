package com.goodsmoa.goodsmoa_BE.user.Repository;


import com.goodsmoa.goodsmoa_BE.user.Entity.UserAddressEntity;
import com.goodsmoa.goodsmoa_BE.user.Entity.UserEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserAddressRepository extends JpaRepository<UserAddressEntity, Long> {

    //유저아이디 기준으로 배송지 다 비우기
    void deleteAllByUser(UserEntity user);

    // 유저 기준으로 배송지 전체 조회
    List<UserAddressEntity> findAllByUser(UserEntity user);
}
