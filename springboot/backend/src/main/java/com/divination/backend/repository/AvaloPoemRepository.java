package com.divination.backend.repository;

import com.divination.backend.model.AvaloPoem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AvaloPoemRepository extends JpaRepository<AvaloPoem, Long> {
    
    // 隨機取得一筆籤詩（用於占卜）
    @Query(value = "SELECT * FROM avalo_poem ORDER BY RAND() LIMIT 1", nativeQuery = true)
    AvaloPoem findRandomPoem();
}