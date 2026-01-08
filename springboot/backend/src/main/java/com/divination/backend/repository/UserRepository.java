package com.divination.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.divination.backend.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 根據 email 查詢用戶（登入時用）
    Optional<User> findByEmail(String email);
    
    // 檢查 email 是否已存在（註冊時用）
    boolean existsByEmail(String email);
    
    // 根據 nickname 查詢用戶
    Optional<User> findByNickname(String nickname);
}
