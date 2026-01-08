package com.divination.backend.controller;

import com.divination.backend.dto.AuthDTO;
import com.divination.backend.service.Aiservice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class Aicontroller {
    
    private final Aiservice aiService;
    
    public Aicontroller(Aiservice aiService) {
        this.aiService = aiService;
    }
    
    /**
     * AI 解籤 API
     * 
     * 請求格式：
     * {
     *   "question": "用戶的問題",
     *   "poemContent": "籤詩內容",
     *   "type": "manju/avalo/japan"
     * }
     */
    @PostMapping("/interpret")
    public ResponseEntity<?> interpretPoem(@RequestBody Map<String, String> request) {
        try{
            System.out.println("收到的請求：" + request);

            String question = request.get("question");
            String poemContent = request.get("poemContent");
            String type = request.getOrDefault("type", "manju");
            
            // 驗證參數
            if (question == null || question.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthDTO.ApiResponse(false, "問題不能為空"));
            }
            
            if (poemContent == null || poemContent.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new AuthDTO.ApiResponse(false, "籤詩內容不能為空"));
            }
            
            // 呼叫 AI 服務
            String interpretation = aiService.interpretPoem(question, poemContent, type);
            
            return ResponseEntity.ok(new AuthDTO.ApiResponse(
                true,
                "解籤成功",
                Map.of("interpretation", interpretation)
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthDTO.ApiResponse(false, "AI 解籤失敗：" + e.getMessage()));
        }
    }
}