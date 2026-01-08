package com.divination.backend.repository;

import com.divination.backend.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class ArticleRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Article> articleRowMapper = (rs, rowNum) -> {
        Article article = new Article();
        article.setId(rs.getLong("id"));
        article.setTitle(rs.getString("title"));
        article.setContent(rs.getString("content"));
        article.setCategory(rs.getString("category"));
        article.setAuthorId(rs.getLong("author_id"));
        article.setAuthorName(rs.getString("author_name"));
        article.setViewCount(rs.getInt("view_count"));
        article.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        article.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return article;
    };

    // 創建文章
    public Article createArticle(Article article) {
        String sql = "INSERT INTO articles (title, content, category, author_id, author_name) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, article.getTitle());
            ps.setString(2, article.getContent());
            ps.setString(3, article.getCategory());
            ps.setLong(4, article.getAuthorId());
            ps.setString(5, article.getAuthorName());
            return ps;
        }, keyHolder);

        article.setId(keyHolder.getKey().longValue());
        return article;
    }

    // 獲取所有文章列表(按時間倒序)
    public List<Article> findAll() {
        String sql = "SELECT * FROM articles ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, articleRowMapper);
    }

    // 根據ID獲取文章詳情
    public Article findById(Long id) {
        String sql = "SELECT * FROM articles WHERE id = ?";
        List<Article> articles = jdbcTemplate.query(sql, articleRowMapper, id);
        return articles.isEmpty() ? null : articles.get(0);
    }

    // 根據分類獲取文章
    public List<Article> findByCategory(String category) {
        String sql = "SELECT * FROM articles WHERE category = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, articleRowMapper, category);
    }

    // 根據作者ID獲取文章
    public List<Article> findByAuthorId(Long authorId) {
        String sql = "SELECT * FROM articles WHERE author_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, articleRowMapper, authorId);
    }

    // ✅ 更新文章（改成回傳 Article）
    public Article updateArticle(Article article) {
        String sql = "UPDATE articles SET title = ?, content = ?, category = ? WHERE id = ?";
        jdbcTemplate.update(sql, article.getTitle(), article.getContent(), 
                          article.getCategory(), article.getId());
        return findById(article.getId());  // ← 加這行
    }

    // 增加閱讀次數
    public void incrementViewCount(Long id) {
        String sql = "UPDATE articles SET view_count = view_count + 1 WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    // 刪除文章
    public void deleteArticle(Long id) {
        String sql = "DELETE FROM articles WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}