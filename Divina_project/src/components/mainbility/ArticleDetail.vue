<template>
  <div class="detail-container">
    <!-- 載入中 -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>載入中...</p>
    </div>

    <!-- 找不到文章 -->
    <div v-else-if="!article" class="empty-state">
      <div class="empty-icon">📭</div>
      <h3>找不到文章</h3>
      <p>文章可能已被刪除或不存在</p>
      <button class="back-btn-large" @click="$emit('back-to-list')">
        ← 返回列表
      </button>
    </div>

    <!-- 文章內容 -->
    <div v-else class="article-content">
      <!-- 頂部按鈕 -->
      <div class="top-actions">
        <button class="back-btn" @click="$emit('back-to-list')">
          ← 返回列表
        </button>
        <button 
          v-if="canEdit" 
          class="edit-btn" 
          @click="editArticle">
          ✏️ 編輯
        </button>
        <button 
          v-if="canDelete" 
          class="delete-btn" 
          @click="deleteArticle">
          🗑️ 刪除
        </button>
      </div>

      <!-- 文章標題 -->
      <div class="article-header">
        <span class="category-badge">{{ article.category }}</span>
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-meta">
          <span class="author">✍️ {{ article.authorName }}</span>
          <span class="date">📅 {{ formatDate(article.createdAt) }}</span>
          <span class="views">👁 {{ article.viewCount }} 次閱讀</span>
        </div>
      </div>

      <!-- 文章正文 -->
      <div class="article-body">
        <p v-for="(paragraph, index) in paragraphs" :key="index" class="paragraph">
          {{ paragraph }}
        </p>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'ArticleDetail',
  props: ['articleId', 'userInfo'],
  data() {
    return {
      article: null,
      loading: true
    };
  },
  computed: {
    canEdit() {
      if (!this.userInfo || !this.article) return false;
      return this.userInfo.id === this.article.authorId || 
             this.userInfo.role === 'admin';
    },
    canDelete() {
      return this.canEdit;
    },
    paragraphs() {
      if (!this.article || !this.article.content) return [];
      return this.article.content.split('\n').filter(p => p.trim());
    }
  },
  mounted() {
    console.log('========== ArticleDetail 已載入 ==========');
    console.log('接收到的 articleId:', this.articleId);
    console.log('userInfo:', this.userInfo);
    
    if (!this.articleId) {
      console.error('❌ articleId 是 null 或 undefined！');
      alert('文章 ID 錯誤');
      this.$emit('back-to-list');
      return;
    }
    
    this.loadArticle();
  },
  methods: {
    async loadArticle() {
      console.log('🔄 開始載入文章...');
      this.loading = true;
      
      try {
        const response = await axios.get(
          `http://localhost:3000/api/articles/${this.articleId}`
        );
        
        console.log('✅ API 回應:', response.data);
        
        if (response.data.success) {
          this.article = response.data.data;
          console.log('📝 文章載入成功:', this.article);
        } else {
          console.log('❌ API 回應 success=false');
          this.article = null;
          setTimeout(() => {
            alert('文章不存在');
            this.$emit('back-to-list');
          }, 1000);
        }
      } catch (error) {
        console.error('❌ 載入文章失敗:', error);
        console.error('錯誤詳情:', error.response?.data);
        this.article = null;
        setTimeout(() => {
          alert('載入文章失敗');
          this.$emit('back-to-list');
        }, 1000);
      } finally {
        this.loading = false;
      }
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString('zh-TW', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    },
    editArticle() {
      this.$emit('edit-article', this.article);
    },
    async deleteArticle() {
      if (!confirm('確定要刪除這篇文章嗎？')) return;

      try {
        const token = localStorage.getItem('token');
        const response = await axios.delete(
          `http://localhost:3000/api/articles/${this.articleId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`
            }
          }
        );

        if (response.data.success) {
          alert('刪除成功');
          this.$emit('back-to-list');
        }
      } catch (error) {
        console.error('刪除失敗:', error);
        alert('刪除失敗');
      }
    }
  }
};
</script>

<style scoped>
.detail-container {
  min-height: 100vh;
  width: 60vw;
  background: linear-gradient(135deg, #667eea 0%, rgba(255, 255, 255, 0.76) 100%);
  border: 1px solid black;
  border-radius: 15px;
  box-shadow: 0 2px 15px rgba(0,0,0,0.1);
  padding: 20px;
}

/* 載入狀態 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  color: white;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top: 4px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 空狀態 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  text-align: center;
  color: white;
}

.empty-icon {
  font-size: 100px;
  margin-bottom: 30px;
}

.empty-state h3 {
  font-size: 36px;
  margin-bottom: 15px;
}

.empty-state p {
  font-size: 18px;
  margin-bottom: 40px;
  opacity: 0.9;
}

.back-btn-large {
  background: white;
  color: #667eea;
  border: none;
  padding: 15px 40px;
  border-radius: 30px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.back-btn-large:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
}

/* 文章內容 */
.article-content {
  background: white;
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.top-actions {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
}

.back-btn, .edit-btn, .delete-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 20px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.back-btn {
  background: #e0e0e0;
  color: #333;
}

.back-btn:hover {
  background: #d0d0d0;
}

.edit-btn {
  background: #ffa726;
  color: white;
}

.edit-btn:hover {
  background: #fb8c00;
}

.delete-btn {
  background: #ef5350;
  color: white;
}

.delete-btn:hover {
  background: #e53935;
}

.article-header {
  margin-bottom: 40px;
  padding-bottom: 30px;
  border-bottom: 2px solid #f0f0f0;
}

.category-badge {
  display: inline-block;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 20px;
}

.article-title {
  font-size: 42px;
  color: #333;
  margin-bottom: 20px;
  line-height: 1.3;
  font-weight: bold;
}

.article-meta {
  display: flex;
  gap: 25px;
  color: #999;
  font-size: 16px;
}

.author, .date, .views {
  display: flex;
  align-items: center;
  gap: 8px;
}

.article-body {
  font-size: 18px;
  line-height: 2;
  color: #333;
}

.paragraph {
  margin-bottom: 25px;
  text-align: justify;
}

@media (max-width: 768px) {
  .detail-container {
    width: 95vw;
  }

  .article-content {
    padding: 20px;
  }

  .article-title {
    font-size: 28px;
  }

  .article-body {
    font-size: 16px;
  }

  .top-actions {
    flex-wrap: wrap;
  }
}
</style>