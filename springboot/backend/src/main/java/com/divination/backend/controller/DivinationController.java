package com.divination.backend.controller;

import com.divination.backend.dto.AuthDTO;
import com.divination.backend.dto.DivinationDTO;
import com.divination.backend.model.DivinationHistory;
import com.divination.backend.model.JapanPoem;
import com.divination.backend.repository.JapanPoemRepository;
import com.divination.backend.security.JwtUtil;
import com.divination.backend.service.DivinationService;
import lombok.RequiredArgsConstructor;

import com.divination.backend.repository.DivinationHistoryRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/divination")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DivinationController {
    private final DivinationHistoryRepository divinationHistoryRepository;
    private final JapanPoemRepository japanPoemRepository;
    private final DivinationService divinationService;
    private final JwtUtil jwtUtil;
    
    // 文殊占卜 API - 允許訪客使用
    @PostMapping("/manju")
    public ResponseEntity<?> manjuDivination(
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
                    // Token 無效，使用訪客模式
                    userId = null;
                }
            }
            
            DivinationDTO.DivinationResponse response = divinationService.doManjuDivination(
                userId, 
                request.getQuestion()
            );
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "占卜成功",
                response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    // 觀音占卜 API - 允許訪客使用
    @PostMapping("/avalo")
    public ResponseEntity<?> avaloDivination(
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
            
            DivinationDTO.DivinationResponse response = divinationService.doAvaloDivination(
                userId, 
                request.getQuestion()
            );
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "占卜成功",
                response
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    // 查詢占卜歷史 API - 需要登入
    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            java.util.List<DivinationDTO.HistoryWithNoteResponse> history = 
                divinationService.getUserHistoryWithNotes(userId);
            
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
    
    // 刪除單筆占卜記錄 API - 需要登入
    @DeleteMapping("/history/{id}")
    public ResponseEntity<?> deleteHistory(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            divinationService.deleteHistory(userId, id);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "刪除成功"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    // 查詢所有文殊籤詩 API - 開放給所有人
    @GetMapping("/poems/manju")
    public ResponseEntity<?> getAllManjuPoems() {
        try {
            java.util.List<DivinationDTO.PoemData> poems = divinationService.getAllManjuPoems();
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "查詢成功",
                poems
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }
    
    // 查詢所有觀音籤詩 API - 開放給所有人
    @GetMapping("/poems/avalo")
    public ResponseEntity<?> getAllAvaloPoems() {
        try {
            java.util.List<DivinationDTO.PoemData> poems = divinationService.getAllAvaloPoems();
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "查詢成功",
                poems
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }

    // 查詢用戶統計 - 需要登入
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            java.util.Map<String, Object> stats = divinationService.getUserStats(userId);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "查詢成功",
                stats
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }

    // 儲存或更新日記 API - 需要登入
    @PostMapping("/note")
    public ResponseEntity<?> saveNote(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody DivinationDTO.NoteRequest request) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            DivinationDTO.NoteResponse note = divinationService.saveNote(userId, request);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "儲存成功",
                note
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }

    // 查詢日記 API - 需要登入
    @GetMapping("/history/{id}")
    public ResponseEntity<?> getHistoryDetail(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            DivinationDTO.DivinationResponse response = divinationService.getHistoryDetail(userId, id);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(true, "查詢成功", response));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }

    // 刪除日記 API - 需要登入
    @DeleteMapping("/note/{historyId}")
    public ResponseEntity<?> deleteNote(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long historyId) {
        try {
            String token = authHeader.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(token);
            
            divinationService.deleteNote(userId, historyId);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "刪除成功"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new AuthDTO.ApiResponse(false, e.getMessage()));
        }
    }

    // 查詢所有日本籤詩 API - 開放給所有人
    @GetMapping("/poems/japan")
    public ResponseEntity<?> getAllJapanPoems() {
        try {
            java.util.List<JapanPoem> poems = japanPoemRepository.findAll();
            
            java.util.List<DivinationDTO.JapanPoemData> poemDataList = poems.stream()
                .map(poem -> new DivinationDTO.JapanPoemData(
                    poem.getId(),
                    poem.getNumber(),
                    poem.getFortuneLevel(),
                    poem.getContent(),
                    poem.getWish(),
                    poem.getIllness(),
                    poem.getWaitingPerson(),
                    poem.getLostItem(),
                    poem.getLifeEvents()
                ))
                .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "查詢成功",
                poemDataList
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthDTO.ApiResponse(false, "查詢失敗"));
        }
    }
}