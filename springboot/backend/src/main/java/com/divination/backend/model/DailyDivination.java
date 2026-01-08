package com.divination.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_divination")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyDivination {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "poem_id", nullable = false)
    private Long poemId;
    
    @Column(name = "divination_date", nullable = false)
    private LocalDate divinationDate;  // 占卜日期（只記錄日期，不含時間）
    
     @Column(columnDefinition = "TEXT")
    private String question;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
   
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (divinationDate == null) {
            divinationDate = LocalDate.now();
        }
    }
}