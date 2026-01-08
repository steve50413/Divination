<template>
  <div class="article-form-container">
    <div class="form-card">
      <button class="back-btn" @click="goBack">← 返回</button>
      <h1>{{ isEdit ? '✏️ 編輯文章' : '📝 發布新文章' }}</h1>

      <form @submit.prevent="submitArticle">
        <div class="form-group">
          <label>文章標題 *</label>
          <input 
            type="text" 
            v-model="form.title" 
            placeholder="請輸入文章標題"
            required
          />
        </div>

        <div class="form-group">
          <label>文章分類 *</label>
          <select v-model="form.category" required>
            <option value="">請選擇分類</option>
            <option value="心得">心得</option>
            <option value="教學">教學</option>
            <option value="佛學">佛學</option>
            <option value="其他">其他</option>
          </select>
        </div>

        <div class="form-group">
          <label>文章內容 *</label>
          <textarea 
            v-model="form.content" 
            placeholder="請輸入文章內容&#10;&#10;可以按 Enter 鍵換行分段"
            rows="15"
            required
          ></textarea>
          <div class="char-count">{{ form.content.length }} 字</div>
        </div>

        <div class="form-actions">
          <button type="button" class="cancel-btn" @click="goBack">取消</button>
          <button type="submit" class="submit-btn">
            {{ isEdit ? '💾 更新文章' : '🚀 發布文章' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'ArticleForm',
  props: ['editArticle'],
  data() {
    return {
      form: {
        title: '',
        category: '',
        content: ''
      },
      isEdit: false
    };
  },
  mounted() {
    if (this.editArticle) {
      this.isEdit = true;
      this.form.title = this.editArticle.title;
      this.form.category = this.editArticle.category;
      this.form.content = this.editArticle.content;
    }
  },
  methods: {
    async submitArticle() {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          alert('請先登入');
          return;
        }

        let response;
        if (this.isEdit) {
          response = await axios.put(
            `http://localhost:3000/api/articles/${this.editArticle.id}`,
            this.form,
            { headers: { Authorization: `Bearer ${token}` } }
          );
        } else {
          response = await axios.post(
            'http://localhost:3000/api/articles/create',
            this.form,
            { headers: { Authorization: `Bearer ${token}` } }
          );
        }

        if (response.data.success) {
          alert(this.isEdit ? '文章更新成功!' : '文章發布成功!');
          this.$emit('submit-success');
        }
      } catch (error) {
        console.error('提交失敗:', error);
        alert('提交失敗,請重試');
      }
    },
    goBack() {
      this.$emit('cancel');
    }
  }
};
</script>

<style scoped>
.article-form-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 30px 20px;
}

.back-btn {
  background: transparent;
  border: 2px solid #667eea;
  color: #667eea;
  padding: 10px 25px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 16px;
  margin-bottom: 30px;
  transition: all 0.3s;
}

.back-btn:hover {
  background: #667eea;
  color: white;
}

.form-card {
  background: white;
  border-radius: 15px;
  width:55vw;
  padding: 40px;
  box-shadow: 0 2px 15px rgba(0,0,0,0.1);
}

.form-card h1 {
  font-size: 32px;
  color: #333;
  margin-bottom: 40px;
  text-align: center;
}

.form-group {
  margin-bottom: 30px;
}

.form-group label {
  display: block;
  margin-bottom: 10px;
  color: #333;
  font-weight: bold;
  font-size: 16px;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 15px;
  border: 2px solid #e0e0e0;
  border-radius: 10px;
  font-size: 16px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #667eea;
}

.form-group textarea {
  resize: vertical;
  font-family: inherit;
  line-height: 1.6;
}

.char-count {
  text-align: right;
  color: #999;
  font-size: 14px;
  margin-top: 5px;
}

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin-top: 40px;
}

.cancel-btn,
.submit-btn {
  padding: 15px 40px;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-btn {
  background: #f5f5f5;
  color: #666;
}

.cancel-btn:hover {
  background: #e0e0e0;
}

.submit-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.submit-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}
</style>