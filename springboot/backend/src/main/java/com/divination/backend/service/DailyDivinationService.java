package com.divination.backend.service;

import com.divination.backend.dto.DivinationDTO;
import com.divination.backend.model.DailyDivination;
import com.divination.backend.model.JapanPoem;
import com.divination.backend.repository.DailyDivinationRepository;
import com.divination.backend.repository.JapanPoemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyDivinationService {
    
    private final JapanPoemRepository japanPoemRepository;
    private final DailyDivinationRepository dailyDivinationRepository;
    private final com.divination.backend.repository.DivinationHistoryRepository historyRepository;
    
    // 抽每日一籤 - 支援訪客模式
    public DivinationDTO.DailyResponse drawDailyPoem(Long userId, String question) {
        JapanPoem poem = japanPoemRepository.findRandomPoem();
        if (poem == null) {
            throw new RuntimeException("籤詩資料庫為空");
        }
        
        Long dailyId = null;
        LocalDateTime createdAt = LocalDateTime.now();
        
        // 只有登入用戶才儲存記錄
        if (userId != null) {
            // 儲存到 daily_divination 表
            DailyDivination daily = new DailyDivination();
            daily.setUserId(userId);
            daily.setPoemId(poem.getId());
            daily.setDivinationDate(LocalDate.now());
            daily.setQuestion(question);
            daily = dailyDivinationRepository.save(daily);
            dailyId = daily.getId();
            createdAt = daily.getCreatedAt();
            
            // 同時存到 divination_history 表
            com.divination.backend.model.DivinationHistory history = 
                new com.divination.backend.model.DivinationHistory();
            history.setUserId(userId);
            history.setType("japan");
            history.setQuestion(question);
            history.setPoemId(poem.getId());
            history.setTibetanCode(poem.getNumber());
            history.setTitle(poem.getFortuneLevel());
            history.setSummary(poem.getContent());
            historyRepository.save(history);
        }
        
        return new DivinationDTO.DailyResponse(
            dailyId,  // 訪客模式時為 null
            convertToPoemData(poem),
            LocalDate.now(),
            createdAt,
            false,
            question
        );
    }
    
    // 檢查今天是否已抽過籤
    public boolean hasDrawnToday(Long userId) {
        return dailyDivinationRepository.existsByUserIdAndDivinationDate(
            userId, 
            LocalDate.now()
        );
    }
    
    // 查詢今天的籤詩
    public DivinationDTO.DailyResponse getTodayPoem(Long userId) {
        LocalDate today = LocalDate.now();
        
        DailyDivination daily = dailyDivinationRepository
                .findByUserIdAndDivinationDate(userId, today)
                .orElseThrow(() -> new RuntimeException("今天還沒有抽籤"));
        
        JapanPoem poem = japanPoemRepository.findById(daily.getPoemId())
                .orElseThrow(() -> new RuntimeException("籤詩不存在"));
        
        return new DivinationDTO.DailyResponse(
            daily.getId(),
            convertToPoemData(poem),
            daily.getDivinationDate(),
            daily.getCreatedAt(),
            true,
            daily.getQuestion()
        );
    }
    
    // 查詢每日籤歷史
    public List<DivinationDTO.DailyHistoryResponse> getDailyHistory(Long userId) {
        List<DailyDivination> records = dailyDivinationRepository
                .findByUserIdOrderByDivinationDateDesc(userId);
        
        return records.stream()
            .map(record -> {
                JapanPoem poem = japanPoemRepository.findById(record.getPoemId())
                        .orElse(null);
                
                if (poem == null) return null;
                
                String preview = poem.getContent() != null && poem.getContent().length() > 50
                    ? poem.getContent().substring(0, 50) + "..."
                    : poem.getContent();
                
                return new DivinationDTO.DailyHistoryResponse(
                    record.getId(),
                    poem.getNumber(),
                    poem.getFortuneLevel(),
                    preview,
                    record.getDivinationDate()
                );
            })
            .filter(r -> r != null)
            .collect(java.util.stream.Collectors.toList());
    }
    
    // 轉換為 PoemData
    private DivinationDTO.JapanPoemData convertToPoemData(JapanPoem poem) {
        return new DivinationDTO.JapanPoemData(
            poem.getId(),
            poem.getNumber(),
            poem.getFortuneLevel(),
            poem.getContent(),
            poem.getWish(),
            poem.getIllness(),
            poem.getWaitingPerson(),
            poem.getLostItem(),
            poem.getLifeEvents()
        );
    }
}