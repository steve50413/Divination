package com.divination.backend.controller;

import com.divination.backend.dto.AuthDTO;
import com.divination.backend.dto.DivinationDTO;
import com.divination.backend.model.DailyDivination;
import com.divination.backend.model.DivinationHistory;
import com.divination.backend.model.JapanPoem;
import com.divination.backend.repository.DailyDivinationRepository;
import com.divination.backend.repository.DivinationHistoryRepository;
import com.divination.backend.repository.JapanPoemRepository;
import com.divination.backend.security.JwtUtil;
import com.divination.backend.service.DailyDivinationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/daily")
@CrossOrigin(origins = "*")
public class DailyDivinationController {
   
    private final DailyDivinationService dailyDivinationService;
    private final JwtUtil jwtUtil;
    private final DailyDivinationRepository dailyDivinationRepository;
    private final JapanPoemRepository japanPoemRepository;
    private final DivinationHistoryRepository divinationHistoryRepository;  
    
    // 手動建構子
    public DailyDivinationController(
        DailyDivinationService dailyDivinationService,
        JwtUtil jwtUtil,
        DailyDivinationRepository dailyDivinationRepository,
        JapanPoemRepository japanPoemRepository,
        DivinationHistoryRepository divinationHistoryRepository 
    ) {
        this.dailyDivinationService = dailyDivinationService;
        this.jwtUtil = jwtUtil;
        this.dailyDivinationRepository = dailyDivinationRepository;
        this.japanPoemRepository = japanPoemRepository;
        this.divinationHistoryRepository = divinationHistoryRepository; 
    }

    // 抽每日一籤 API - 允許訪客使用
    @PostMapping("/draw")
    public ResponseEntity<?> drawDaily(
        @RequestHeader(value = "Authorization", required = false) String authHeader,
        @RequestBody DivinationDTO.DivinationRequest request) {
        try {
            Long userId = null;
            
            // 如果有 token 就取 userId，沒有就用 null（訪客模式）
            if (authHeader != null && !authHeader.isEmpty()) {
                try {
                    String token = authHeader.replace("Bearer ", "");
                    userId = jwtUtil.getUserIdFromToken(token);
                } catch (Exception e) {
                    userId = null;
                }
            }
            
            DivinationDTO.DailyResponse response = dailyDivinationService.drawDailyPoem(
                userId,
                request.getQuestion()
            );
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "抽籤成功",
                response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    // 查詢每日籤歷史 API - 需要登入
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            java.util.List<DivinationDTO.DailyHistoryResponse> history = 
                dailyDivinationService.getDailyHistory(userId);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "查詢成功",
                history
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }

    // 查詢單筆每日籤詳情 - 需要登入
    @GetMapping("/history/{id}")
    public ResponseEntity<?> getDailyById(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            com.divination.backend.model.DivinationHistory history = 
                divinationHistoryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("記錄不存在"));
            
            // 驗證權限
            if (!history.getUserId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new AuthDTO.ApiResponse(false, "無權限"));
            }
            
            // 驗證是日本籤詩
            if (!"japan".equals(history.getType())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthDTO.ApiResponse(false, "類型錯誤"));
            }
            
            // 查詢籤詩
            JapanPoem poem = japanPoemRepository.findById(history.getPoemId())
                    .orElseThrow(() -> new RuntimeException("籤詩不存在"));
            
            // 組裝回應
            DivinationDTO.DailyResponse response = new DivinationDTO.DailyResponse(
                history.getId(),
                convertToPoemData(poem),
                java.time.LocalDate.now(),
                history.getCreatedAt(),
                false,
                history.getQuestion()
            );
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "查詢成功",
                response
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }

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