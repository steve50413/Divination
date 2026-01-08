package com.divination.backend.repository;

import com.divination.backend.model.JapanPoem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface JapanPoemRepository extends JpaRepository<JapanPoem, Long> {
    
    // 隨機取得一首籤詩
    @Query(value = "SELECT * FROM japan_poem ORDER BY RAND() LIMIT 1", nativeQuery = true)
    JapanPoem findRandomPoem();
}