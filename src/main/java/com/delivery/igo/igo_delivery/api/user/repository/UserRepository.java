package com.delivery.igo.igo_delivery.api.user.repository;

import com.delivery.igo.igo_delivery.api.user.entity.UserStatus;
import com.delivery.igo.igo_delivery.api.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    // 유저 상태 조회
    Optional<Users> findByEmailAndUserStatus(String email, UserStatus userStatus);

    boolean existsByEmail(String email);

    boolean existsByEmailAndUserStatus(String email, UserStatus userStatus);

    boolean existsByNicknameAndUserStatus(String nickname, UserStatus userStatus);
}
