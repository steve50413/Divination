package com.divination.backend.repository;

import com.divination.backend.model.DailyDivination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DailyDivinationRepository extends JpaRepository<DailyDivination, Long> {
    
    // 查詢用戶今天是否已經抽過籤
    Optional<DailyDivination> findByUserIdAndDivinationDate(Long userId, LocalDate date);
    
    // 查詢用戶所有每日籤記錄（按日期倒序）
    List<DailyDivination> findByUserIdOrderByDivinationDateDesc(Long userId);
    
    // 檢查今天是否已抽過籤
    boolean existsByUserIdAndDivinationDate(Long userId, LocalDate date);
}