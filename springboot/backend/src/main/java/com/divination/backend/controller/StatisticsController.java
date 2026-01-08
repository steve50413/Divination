package com.divination.backend.controller;

import com.divination.backend.dto.AuthDTO;
import com.divination.backend.dto.StatisticsDTO;
import com.divination.backend.security.JwtUtil;
import com.divination.backend.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class StatisticsController {
    
    private final StatisticsService statisticsService;
    private final JwtUtil jwtUtil;
    
    // 獲取統計總覽
    @GetMapping("/overview")
    public ResponseEntity<?> getOverview(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            jwtUtil.getUserIdFromToken(token);  // 驗證 token
            
            StatisticsDTO.Overview overview = statisticsService.getOverview();
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "查詢成功",
                overview
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    // 獲取占卜類型分布
    @GetMapping("/type-distribution")
    public ResponseEntity<?> getTypeDistribution(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            jwtUtil.getUserIdFromToken(token);
            
            StatisticsDTO.TypeDistribution distribution = statisticsService.getTypeDistribution();
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "查詢成功",
                distribution
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    // 獲取趨勢數據
    @GetMapping("/trend")
    public ResponseEntity<?> getTrend(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "7") int days) {
        try {
            String token = authHeader.replace("Bearer ", "");
            jwtUtil.getUserIdFromToken(token);
            
            java.util.List<StatisticsDTO.TrendData> trend = statisticsService.getTrend(days);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "查詢成功",
                trend
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
}