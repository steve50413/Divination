package com.divination.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
public class Aiservice {
    
    @Value("${openrouter.api.key:}")
    private String apiKey;
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public Aiservice() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    public String interpretPoem(String question, String poemContent, String type) {
        System.out.println("===== 開始 AI 解籤 =====");
        System.out.println("Question: " + question);
        System.out.println("Type: " + type);
        System.out.println("Content length: " + poemContent.length());

        if (apiKey == null || apiKey.isEmpty()) {
            System.out.println("❌ API Key 未設定");
            return "AI 解籤功能需要設定 API Key...";
        }
        System.out.println("✅ API Key 已設定");
        
        try {
            String prompt = buildPrompt(question, poemContent, type);
            System.out.println("✅ Prompt 建立完成");
            
            // 建立符合 OpenRouter 規格的請求 Body
            Map<String, Object> requestMap = new HashMap<>();
          requestMap.put("model", "anthropic/claude-3.5-sonnet-20241022");
            
            // Messages
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);
            requestMap.put("messages", List.of(message));
            
            // 加入必要參數
            requestMap.put("max_tokens", 500);
            requestMap.put("temperature", 0.7);
            
            String requestBody = objectMapper.writeValueAsString(requestMap);
            System.out.println("✅ Request Body: " + requestBody);
            
            // 建立 HTTP 請求
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .timeout(Duration.ofSeconds(60))
                    .build();
            
            System.out.println("🌐 發送請求到 OpenRouter...");
            HttpResponse<String> response = httpClient.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("✅ 狀態碼: " + response.statusCode());
            System.out.println("✅ 回應內容: " + response.body());

            if (response.statusCode() == 200) {
                return parseResponse(response.body());
            } else {
                System.out.println("❌ API 錯誤回應: " + response.body());
                return "AI 解籤暫時無法使用，請稍後再試。\n錯誤代碼：" + response.statusCode();
            }
            
        } catch (Exception e) {
            System.out.println("❌ 例外錯誤: " + e.getClass().getName());
            System.out.println("❌ 錯誤訊息: " + e.getMessage());
            e.printStackTrace();
            return "AI 解籤發生錯誤：" + e.getMessage();
        }
    }
    
    private String buildPrompt(String question, String poemContent, String type) {
        String poemType = switch (type) {
            case "manju" -> "文殊菩薩籤詩";
            case "avalo" -> "觀音菩薩籤詩";
            case "japan" -> "日本神社籤詩";
            default -> "籤詩";
        };
        
        return String.format("""
            你是一位精通佛教籤詩的解籤大師，具有深厚的佛學造詣和人生智慧。
            
            【重要原則】
            1. 用溫和、易懂的白話文解讀
            2. 語氣溫暖、正面、有智慧
            3. 給予實用且正面的建議
            4. 不做絕對性預測，提醒籤詩是參考
            5. 鼓勵用戶用自己的智慧思考
            6. 字數控制在 200-300 字
            
            【用戶資訊】
            問題：%s
            抽到的籤詩：%s
            籤詩類型：%s
            
            請針對用戶的問題，解讀這支籤詩的含義，並給予實用的建議。
            記得保持謙虛，說明這只是參考，最終決定仍需自己判斷。
            """, question, poemContent, poemType);
    }
    
    private String parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode choices = root.path("choices");
            
            if (choices.isArray() && choices.size() > 0) {
                JsonNode message = choices.get(0).path("message");
                String content = message.path("content").asText();
                
                if (content != null && !content.isEmpty()) {
                    return content;
                }
            }
            
            System.out.println("❌ 無法從回應中提取內容");
            return "無法解析 AI 回應";
        } catch (Exception e) {
            System.out.println("❌ 解析錯誤: " + e.getMessage());
            e.printStackTrace();
            return "AI 解籤發生錯誤：" + e.getMessage();
        }
    }
}