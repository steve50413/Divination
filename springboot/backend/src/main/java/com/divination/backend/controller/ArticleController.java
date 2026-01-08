package com.divination.backend.controller;

import com.divination.backend.model.Article;
import com.divination.backend.model.User;
import com.divination.backend.service.ArticleService;
import com.divination.backend.repository.UserRepository;
import com.divination.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ 創建文章
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createArticle(
            @RequestHeader("Authorization") String token,
            @RequestBody Article article) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 驗證 token
            String jwt = token.replace("Bearer ", "");
            
            if (!jwtUtil.validateToken(jwt)) {
                response.put("success", false);
                response.put("message", "請先登入");
                return ResponseEntity.status(401).body(response);
            }

            // 從 token 取得 userId
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            // 查詢用戶
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用戶不存在"));

            // 設置作者信息
            article.setAuthorId(user.getId());
            article.setAuthorName(user.getNickname());

            // 創建文章
            Article createdArticle = articleService.createArticle(article);
            
            response.put("success", true);
            response.put("message", "文章發布成功");
            response.put("data", createdArticle);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "發布失敗: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ✅ 獲取所有文章列表
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getArticleList() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            List<Article> articles = articleService.getAllArticles();
            response.put("success", true);
            response.put("data", articles);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取文章列表失敗: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ✅ 獲取單篇文章詳情
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getArticleById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Article article = articleService.getArticleById(id);
            
            if (article == null) {
                response.put("success", false);
                response.put("message", "文章不存在");
                return ResponseEntity.status(404).body(response);
            }
            
            // 增加閱讀次數
            articleService.incrementViewCount(id);
            
            response.put("success", true);
            response.put("data", article);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取文章失敗: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ✅ 獲取我的文章
    @GetMapping("/my")
    public ResponseEntity<Map<String, Object>> getMyArticles(
            @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String jwt = token.replace("Bearer ", "");
            
            if (!jwtUtil.validateToken(jwt)) {
                response.put("success", false);
                response.put("message", "請先登入");
                return ResponseEntity.status(401).body(response);
            }

            Long userId = jwtUtil.getUserIdFromToken(jwt);
            List<Article> articles = articleService.getArticlesByAuthor(userId);
            
            response.put("success", true);
            response.put("data", articles);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "獲取文章失敗: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ✅ 更新文章
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateArticle(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody Article article) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String jwt = token.replace("Bearer ", "");
            
            if (!jwtUtil.validateToken(jwt)) {
                response.put("success", false);
                response.put("message", "請先登入");
                return ResponseEntity.status(401).body(response);
            }

            Long userId = jwtUtil.getUserIdFromToken(jwt);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用戶不存在"));

            // 檢查是否是作者或管理員
            boolean isAuthor = articleService.isArticleAuthor(id, userId);
            boolean isAdmin = "admin".equals(user.getRole());
            
            if (!isAuthor && !isAdmin) {
                response.put("success", false);
                response.put("message", "無權限修改此文章");
                return ResponseEntity.status(403).body(response);
            }

            article.setId(id);
            Article updatedArticle = articleService.updateArticle(article);
            
            response.put("success", true);
            response.put("message", "文章更新成功");
            response.put("data", updatedArticle);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "更新失敗: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ✅ 刪除文章
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteArticle(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String jwt = token.replace("Bearer ", "");
            
            if (!jwtUtil.validateToken(jwt)) {
                response.put("success", false);
                response.put("message", "請先登入");
                return ResponseEntity.status(401).body(response);
            }

            Long userId = jwtUtil.getUserIdFromToken(jwt);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用戶不存在"));

            // 檢查是否是作者或管理員
            boolean isAuthor = articleService.isArticleAuthor(id, userId);
            boolean isAdmin = "admin".equals(user.getRole());
            
            if (!isAuthor && !isAdmin) {
                response.put("success", false);
                response.put("message", "無權限刪除此文章");
                return ResponseEntity.status(403).body(response);
            }

            articleService.deleteArticle(id);
            
            response.put("success", true);
            response.put("message", "文章刪除成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "刪除失敗: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}