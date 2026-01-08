package com.divination.backend.repository;

import com.divination.backend.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    
    // 查詢最新的驗證碼
    Optional<VerificationCode> findTopByEmailOrderByCreatedAtDesc(String email);
    
    // 刪除過期的驗證碼
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}