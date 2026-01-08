package com.divination.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "divination_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DivinationHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(nullable = false, length = 20)
    private String type;  // "manju" 或 "avalo"
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;  // 用戶的問題
    
    @Column(name = "poem_id", nullable = false)
    private Long poemId;  // 抽到的籤詩 ID
    
    @Column(name = "poem_number")
    private Integer poemNumber;  // 籤號
    
    @Column(length = 100)
    private String title;  // 籤詩標題
    
    @Column(name = "tibetan_code", length = 50)
    private String tibetanCode;  // 藏文代碼
    
    @Column(columnDefinition = "TEXT")
    private String summary;  // 籤詩內容
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}