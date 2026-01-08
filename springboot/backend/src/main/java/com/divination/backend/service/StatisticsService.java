package com.divination.backend.service;

import com.divination.backend.dto.StatisticsDTO;
import com.divination.backend.repository.UserRepository;
import com.divination.backend.repository.DivinationHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {
    
    private final UserRepository userRepository;
    private final DivinationHistoryRepository historyRepository;
    
    // 獲取總覽數據
    public StatisticsDTO.Overview getOverview() {
        // 總用戶數
        Long totalUsers = userRepository.count();
        
        // 總占卜次數
        Long totalDivinations = historyRepository.count();
        
        // 今日占卜數
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        Long todayDivinations = historyRepository.countByCreatedAtAfter(todayStart);
        
        return new StatisticsDTO.Overview(
            totalUsers,
            totalDivinations,
            todayDivinations
        );
    }
    
    // 獲取占卜類型分布
    public StatisticsDTO.TypeDistribution getTypeDistribution() {
        // 各類型數量
        Long manjuCount = historyRepository.countByType("manju");
        Long avaloCount = historyRepository.countByType("avalo");
        Long japanCount = historyRepository.countByType("japan");
        
        Long total = manjuCount + avaloCount + japanCount;
        
        // 計算百分比
        Double manjuPercentage = total > 0 ? (manjuCount * 100.0 / total) : 0.0;
        Double avaloPercentage = total > 0 ? (avaloCount * 100.0 / total) : 0.0;
        Double japanPercentage = total > 0 ? (japanCount * 100.0 / total) : 0.0;
        
        return new StatisticsDTO.TypeDistribution(
            manjuCount,
            avaloCount,
            japanCount,
            Math.round(manjuPercentage * 100.0) / 100.0,
            Math.round(avaloPercentage * 100.0) / 100.0,
            Math.round(japanPercentage * 100.0) / 100.0
        );
    }
    
    // 獲取趨勢數據
    public List<StatisticsDTO.TrendData> getTrend(int days) {
        List<StatisticsDTO.TrendData> trendList = new ArrayList<>();
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = date.plusDays(1).atStartOfDay();
            
            Long count = historyRepository.countByCreatedAtBetween(dayStart, dayEnd);
            
            trendList.add(new StatisticsDTO.TrendData(
                date.toString(),
                count
            ));
        }
        
        return trendList;
    }
}
