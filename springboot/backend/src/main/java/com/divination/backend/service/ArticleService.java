package com.divination.backend.service;

import com.divination.backend.model.*;
import com.divination.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    // 創建文章
    public Article createArticle(Article article) {
        return articleRepository.createArticle(article);
    }

    // 獲取所有文章
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    // 獲取文章詳情(並增加閱讀次數)
    public Article getArticleById(Long id) {
        Article article = articleRepository.findById(id);
        if (article != null) {
            articleRepository.incrementViewCount(id);
        }
        return article;
    }

    // 根據分類獲取文章
    public List<Article> getArticlesByCategory(String category) {
        return articleRepository.findByCategory(category);
    }

    // 根據作者獲取文章
    public List<Article> getArticlesByAuthor(Long authorId) {
        return articleRepository.findByAuthorId(authorId);
    }

    // ✅ 增加閱讀次數
    public void incrementViewCount(Long id) {
        articleRepository.incrementViewCount(id);
    }

    // ✅ 更新文章（改成回傳 Article）
    public Article updateArticle(Article article) {
        return articleRepository.updateArticle(article);  // ← 加 return
    }

    // 刪除文章
    public void deleteArticle(Long id) {
        articleRepository.deleteArticle(id);
    }

    // 檢查用戶是否是文章作者
    public boolean isArticleAuthor(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId);
        return article != null && article.getAuthorId().equals(userId);
    }
}