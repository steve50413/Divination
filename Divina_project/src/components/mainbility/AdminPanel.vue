<script setup>
const emits = defineEmits(['back', 'gotoPoems', 'gotoArticles', 'gotoStats'])

// 檢查是否為管理員（從 localStorage 讀取）
const userRole = localStorage.getItem('userRole')

if (userRole !== 'admin') {
  alert('您不是管理員！')
  emits('back')
}
</script>

<template>
  <div class="admin-container">
    <div class="admin-box">
      
      <!-- 返回按鈕 -->
      <button class="back-btn" @click="$emit('back')">返回</button>

      <!-- 標題 -->
      <h1 class="main-title">⚙️ 管理後台</h1>

      <!-- 功能卡片 -->
      <div class="cards-grid">
        
        <!-- 籤詩管理 -->
        <div class="admin-card" @click="$emit('gotoPoems')">
          <div class="card-icon">📝</div>
          <h2>籤詩管理</h2>
          <p>新增、編輯、刪除籤詩</p>
        </div>

        <!-- 文章管理 -->
        <div class="admin-card" @click="$emit('gotoArticles')">
        <div class="card-icon">📝</div>
        <h3>文章管理</h3>
        <p>管理所有知識文章</p>
       </div>

        <!-- 數據統計 -->
        <div class="admin-card" @click="$emit('gotoStats')">
          <div class="card-icon">📊</div>
          <h2>數據統計</h2>
          <p>查看系統使用數據</p>
        </div>

      </div>

    </div>
  </div>
</template>

<style scoped>
.admin-container {
  background-color: #0072E3;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.admin-box {
  background-color: white;
  width: 90vw;
  max-width: 1000px;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  position: relative;
}

.back-btn {
  position: absolute;
  top: 20px;
  left: 20px;
  background-color: #6b7280;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s;
}

.back-btn:hover {
  background-color: #4b5563;
}

.main-title {
  text-align: center;
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 50px;
  margin-top: 20px;
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.admin-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 30px;
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
  color: white;
  position: relative;
}

.admin-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(102, 126, 234, 0.4);
}

.admin-card.disabled {
  background: linear-gradient(135deg, #bbb 0%, #999 100%);
  cursor: not-allowed;
  opacity: 0.7;
}

.admin-card.disabled:hover {
  transform: none;
  box-shadow: none;
}

.card-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.admin-card h2 {
  font-size: 1.5rem;
  margin-bottom: 10px;
  color: white;
}

.admin-card p {
  font-size: 1rem;
  opacity: 0.9;
  line-height: 1.5;
}

.coming-soon {
  position: absolute;
  top: 15px;
  right: 15px;
  background-color: rgba(255, 255, 255, 0.3);
  padding: 5px 12px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 600;
}

@media (max-width: 768px) {
  .admin-box {
    width: 95vw;
    padding: 20px;
  }

  .main-title {
    font-size: 1.8rem;
  }

  .cards-grid {
    grid-template-columns: 1fr;
  }
}
</style>