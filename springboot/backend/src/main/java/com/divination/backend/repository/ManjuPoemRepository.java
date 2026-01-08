package com.divination.backend.repository;

import com.divination.backend.model.ManjuPoem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ManjuPoemRepository extends JpaRepository<ManjuPoem, Long> {
    
    // 隨機取得一筆籤詩（用於占卜）
    @Query(value = "SELECT * FROM manju_poem ORDER BY RAND() LIMIT 1", nativeQuery = true)
    ManjuPoem findRandomPoem();
}