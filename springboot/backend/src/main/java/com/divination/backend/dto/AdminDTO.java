package com.divination.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AdminDTO {
    
    // 籤詩請求（用於新增/編輯）
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PoemRequest {
        private Long id;  // 編輯時需要，新增時為 null
        private String type;  // "manju", "avalo", "japan"
        
        // 文殊/觀音共用欄位
        private String tibetanCode;
        private String direction;
        private String title;
        private String summary;
        
        // 文殊專用欄位
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
        
        // 日本籤詩專用欄位
        private String number;
        private String fortuneLevel;
        private String content;
        private String wish;
        private String waitingPerson;
        private String lifeEvents;
    }
    
    // 籤詩回應
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PoemResponse {
        private Long id;
        private String type;
        private String displayName;  // 顯示名稱（標題或籤號）
        private String preview;  // 內容預覽
    }
}