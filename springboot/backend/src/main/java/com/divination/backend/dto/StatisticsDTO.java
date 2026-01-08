package com.divination.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class StatisticsDTO {
    
    // 總覽數據
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Overview {
        private Long totalUsers;           // 總用戶數
        private Long totalDivinations;     // 總占卜次數
        private Long todayDivinations;     // 今日占卜數
    }
    
    // 類型分布
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TypeDistribution {
        private Long manjuCount;           // 文殊占卜次數
        private Long avaloCount;           // 觀音占卜次數
        private Long japanCount;           // 每日一籤次數
        private Double manjuPercentage;    // 文殊百分比
        private Double avaloPercentage;    // 觀音百分比
        private Double japanPercentage;    // 每日一籤百分比
    }
    
    // 趨勢數據
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendData {
        private String date;               // 日期（yyyy-MM-dd）
        private Long count;                // 當日占卜次數
    }
}