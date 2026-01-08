package com.divination.backend.repository;

import com.divination.backend.model.DivinationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DivinationHistoryRepository extends JpaRepository<DivinationHistory, Long> {
    
    // 根據用戶 ID 查詢所有歷史記錄（按時間倒序）
    List<DivinationHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // 統計用戶總占卜次數
    long countByUserId(Long userId);
    
    // 統計用戶某種類型的占卜次數
    long countByUserIdAndType(Long userId, String type);

     Long countByCreatedAtAfter(LocalDateTime date);
    Long countByType(String type);
    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}