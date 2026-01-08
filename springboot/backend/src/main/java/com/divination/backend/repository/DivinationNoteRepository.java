package com.divination.backend.repository;

import com.divination.backend.model.DivinationNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DivinationNoteRepository extends JpaRepository<DivinationNote, Long> {
    
    // 根據歷史記錄 ID 查詢日記
    Optional<DivinationNote> findByHistoryId(Long historyId);
    
    // 根據用戶 ID 和歷史記錄 ID 查詢
    Optional<DivinationNote> findByUserIdAndHistoryId(Long userId, Long historyId);
    
    // 檢查是否存在日記
    boolean existsByHistoryId(Long historyId);
}