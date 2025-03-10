package com.goodsmoa.goodsmoa_BE.user.Repository;

import com.goodsmoa.goodsmoa_BE.user.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User,String> {
}
