package com.divination.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DivinationDTO {
    
    // 占卜請求（包含用戶問題）
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DivinationRequest {
        private String question;
    }

    // 占卜結果回應
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DivinationResponse {
        private Long historyId;
        private String type;
        private String question;
        private PoemData poem;
        private LocalDateTime createdAt;
    }

    // 籤詩資料
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PoemData {
        private Long id;
        private Integer number;
        private String tibetanCode;
        private String direction;
        private String title;
        private String summary;
        
        // 文殊籤詩的額外欄位
        private String familyLife;
        private String strategy;
        private String fortune;
        private String enemy;
        private String travel;
        private String illness;
        private String practice;
        private String lostItem;
        private String visitor;
        private String otherMatters;
        private String advice;
    }

    // 歷史記錄回應
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryResponse {
        private Long id;
        private String type;
        private String question;
        private Integer poemNumber;
        private String title;
        private String summary;
        private LocalDateTime createdAt;
    }

    // 日記請求
@Data
@NoArgsConstructor
@AllArgsConstructor
public static class NoteRequest {
    private Long historyId;
    private String note;
    private Boolean isAccurate;  // null, true, false
}

// 日記回應
@Data
@NoArgsConstructor
@AllArgsConstructor
public static class NoteResponse {
    private Long id;
    private Long historyId;
    private String note;
    private Boolean isAccurate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// 歷史記錄回應（加入日記資訊）- 替換原本的 HistoryResponse
@Data
@NoArgsConstructor
@AllArgsConstructor
public static class HistoryWithNoteResponse {
    private Long id;
    private String type;
    private String question;
    private Integer poemNumber;
    private String title;
    private String summary;
    private LocalDateTime createdAt;
    private boolean hasNote;  // 是否有日記
    private String notePreview;  // 日記預覽（前50字）
    private Boolean isAccurate;  // 準不準
}

// 每日一籤回應
@Data
@NoArgsConstructor
@AllArgsConstructor
public static class DailyResponse {
    private Long id;
    private JapanPoemData poem;
    private LocalDate divinationDate;
    private LocalDateTime createdAt;
    private boolean alreadyDrawnToday;  
     private String question; 
}

// 日本籤詩資料
@Data
@NoArgsConstructor
@AllArgsConstructor
public static class JapanPoemData {
    private Long id;
    private String number;
    private String fortuneLevel;
    private String content;
    private String wish;
    private String illness;
    private String waitingPerson;
    private String lostItem;
    private String lifeEvents;
}

// 每日籤歷史記錄
@Data
@NoArgsConstructor
@AllArgsConstructor
public static class DailyHistoryResponse {
    private Long id;
    private String number;
    private String fortuneLevel;
    private String contentPreview;  // 內容預覽
    private LocalDate divinationDate;
    
}

}