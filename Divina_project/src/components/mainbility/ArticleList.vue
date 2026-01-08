<template>
  <div class="page-container">
    <div class="content-wrapper">
      <!-- 頂部導航 -->
      <div class="top-nav">
        <button class="back-btn" @click="goBack">
          <span>← 返回首頁</span>
        </button>
        <button class="create-btn" @click="goToCreate" v-if="userInfo">
          <span>✏️ 發布文章</span>
        </button>
      </div>

      <!-- 標題 -->
      <div class="page-header">
        <h1>📝 文章廣場</h1>
        <p class="subtitle">分享您的占卜心得與佛學智慧</p>
      </div>

      <!-- 分類篩選 -->
      <div class="category-filter">
        <button 
          :class="['filter-btn', { active: selectedCategory === '' }]"
          @click="filterByCategory('')">
          全部
        </button>
        <button 
          :class="['filter-btn', { active: selectedCategory === '心得' }]"
          @click="filterByCategory('心得')">
          💭 心得
        </button>
        <button 
          :class="['filter-btn', { active: selectedCategory === '教學' }]"
          @click="filterByCategory('教學')">
          📚 教學
        </button>
        <button 
          :class="['filter-btn', { active: selectedCategory === '佛學' }]"
          @click="filterByCategory('佛學')">
          🙏 佛學
        </button>
        <button 
          :class="['filter-btn', { active: selectedCategory === '其他' }]"
          @click="filterByCategory('其他')">
          📌 其他
        </button>
      </div>

      <!-- 文章列表 -->
      <div class="articles-grid">
        <div class="article-card" v-for="article in filteredArticles" :key="article.id" @click="viewDetail(article.id)">
          <div class="card-header">
            <span class="category-badge">{{ article.category }}</span>
            <span class="view-count">👁 {{ article.viewCount }}</span>
          </div>
          
          <h2 class="article-title">{{ article.title }}</h2>
          
          <p class="article-preview">{{ getPreview(article.content) }}</p>
          
          <div class="card-footer">
            <div class="author-info">
              <span class="author-icon">✍️</span>
              <span class="author-name">{{ article.authorName }}</span>
            </div>
            <span class="date">{{ formatDate(article.createdAt) }}</span>
          </div>
        </div>

        <!-- 空狀態 -->
        <div class="empty-state" v-if="filteredArticles.length === 0">
          <div class="empty-icon">📭</div>
          <h3>還沒有文章</h3>
          <p>成為第一位分享心得的人吧！</p>
          <button class="create-btn-large" @click="goToCreate" v-if="userInfo">
            立即發布
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'ArticleList',
  props: ['userInfo'],
  data() {
    return {
      articles: [],
      selectedCategory: ''
    };
  },
  computed: {
    filteredArticles() {
      if (this.selectedCategory === '') {
        return this.articles;
      }
      return this.articles.filter(a => a.category === this.selectedCategory);
    }
  },
  mounted() {
    console.log('========== ArticleList.vue 已載入 ==========');
    console.log('userInfo:', this.userInfo);
    this.loadArticles();
  },
  methods: {
    async loadArticles() {
      console.log('🔄 開始載入文章...');
      console.log('請求 URL:', 'http://localhost:3000/api/articles/list');
      
      try {
        const response = await axios.get('http://localhost:3000/api/articles/list');
        console.log('✅ API 回應:', response.data);
        
        if (response.data.success) {
          this.articles = response.data.data;
          console.log('📝 文章數量:', this.articles.length);
          console.log('文章內容:', this.articles);
        } else {
          console.log('❌ API 回應 success=false');
        }
      } catch (error) {
        console.error('❌ 載入文章失敗:', error);
        console.error('錯誤詳情:', error.response?.data);
        alert('載入文章失敗');
      }
    },
    filterByCategory(category) {
      this.selectedCategory = category;
    },
    getPreview(content) {
      return content.length > 120 ? content.substring(0, 120) + '...' : content;
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return date.toLocaleDateString('zh-TW', { month: 'long', day: 'numeric' });
    },
    viewDetail(articleId) {
      console.log('👆 點擊文章，ID:', articleId);
      this.$emit('view-article', articleId);
    },
    goToCreate() {
      this.$emit('create-article');
    },
    goBack() {
      this.$emit('back-to-home');
    }
  }
};
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  width: 60vw;
  background: linear-gradient(135deg, #667eea 0%, rgba(255, 255, 255, 0.76) 100%);
  border: 1px solid black;
  border-radius: 15px;
  box-shadow: 0 2px 15px rgba(0,0,0,0.1);
  padding: 20px;
}

.content-wrapper {
  max-width: 1200px;
  margin: 0 auto;
}

.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.back-btn {
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid white;
  color: white;
  padding: 12px 24px;
  border-radius: 25px;
  cursor: pointer;
  font-size: 16px;
  transition: all 0.3s;
  backdrop-filter: blur(10px);
}

.back-btn:hover {
  background: white;
  color: #667eea;
}

.create-btn {
  background: white;
  color: #667eea;
  border: none;
  padding: 12px 30px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
}

.page-header {
  text-align: center;
  color: white;
  margin-bottom: 40px;
}

.page-header h1 {
  font-size: 48px;
  margin-bottom: 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.subtitle {
  font-size: 18px;
  opacity: 0.9;
}

.category-filter {
  display: flex;
  gap: 15px;
  margin-bottom: 40px;
  flex-wrap: wrap;
  justify-content: center;
}

.filter-btn {
  padding: 10px 25px;
  border: 2px solid rgba(255, 255, 255, 0.5);
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 16px;
  backdrop-filter: blur(10px);
}

.filter-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: white;
}

.filter-btn.active {
  background: white;
  color: #667eea;
  border-color: white;
  font-weight: bold;
}

.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 25px;
  margin-bottom: 40px;
}

.article-card {
  background: white;
  border-radius: 20px;
  padding: 30px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.article-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.category-badge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 6px 18px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
}

.view-count {
  color: #999;
  font-size: 14px;
}

.article-title {
  font-size: 24px;
  color: #333;
  margin-bottom: 15px;
  line-height: 1.4;
  font-weight: bold;
  min-height: 66px;
  word-break: break-all;     
  overflow: hidden;           
}

.article-preview {
  color: #666;
  line-height: 1.8;
  margin-bottom: 20px;
  min-height: 72px;
  word-break: break-all;
  overflow: hidden; 
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.author-icon {
  font-size: 18px;
}

.author-name {
  color: #667eea;
  font-weight: bold;
}

.date {
  color: #999;
  font-size: 14px;
}

.empty-state {
  grid-column: 1 / -1;
  text-align: center;
  padding: 80px 20px;
  background: white;
  border-radius: 20px;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 28px;
  color: #333;
  margin-bottom: 10px;
}

.empty-state p {
  color: #999;
  font-size: 16px;
  margin-bottom: 30px;
}

.create-btn-large {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 15px 40px;
  border-radius: 30px;
  font-size: 18px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s;
}

.create-btn-large:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 20px rgba(102, 126, 234, 0.4);
}

@media (max-width: 768px) {
  .articles-grid {
    grid-template-columns: 1fr;
  }
  
  .page-header h1 {
    font-size: 36px;
  }
  
  .top-nav {
    flex-direction: column;
    gap: 15px;
  }
}
</style>