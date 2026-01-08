<script setup>
import { ref, onMounted } from 'vue'

const emits = defineEmits(['back'])

// 用戶資料
const userInfo = ref({
  nickname: '',
  email: '',
  createdAt: ''
})

// 占卜統計
const stats = ref({
  totalCount: 0,
  manjuCount: 0,
  avaloCount: 0,
  japanCount: 0  // ← 加這個
})

const loading = ref(true)
const error = ref('')

onMounted(async () => {
  await loadUserData()
})

// 載入用戶資料
async function loadUserData() {
  const token = localStorage.getItem('token')
  const nickname = localStorage.getItem('nickname')
  const email = localStorage.getItem('email')
  
  if (!token) {
    error.value = '請先登入'
    loading.value = false
    return
  }

  // 從 localStorage 取得基本資料
  userInfo.value.nickname = nickname
  userInfo.value.email = email

  // 取得占卜統計
  try {
    const response = await fetch('http://localhost:3000/api/divination/history', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })

    const data = await response.json()

    if (data.success) {
      const history = data.data
      stats.value.totalCount = history.length
      stats.value.manjuCount = history.filter(h => h.type === 'manju').length
      stats.value.avaloCount = history.filter(h => h.type === 'avalo').length
      stats.value.japanCount = history.filter(h => h.type === 'japan').length  // ← 加這個
    }
  } catch (err) {
    console.error('載入統計錯誤:', err)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div id="profileContainer">
    <div id="profileBox">
      
      <!-- 返回按鈕 -->
      <button id="backBtn" @click="$emit('back')">← 返回</button>

      <!-- 標題 -->
      <h1 class="main-title">👤 個人資料</h1>

      <!-- 載入中 -->
      <div v-if="loading" class="loading">
        <p>載入中...</p>
      </div>

      <!-- 錯誤訊息 -->
      <div v-else-if="error" class="error-message">
        <p>{{ error }}</p>
        <button @click="$emit('back')" class="btn-primary">返回主頁</button>
      </div>

      <!-- 個人資料內容 -->
      <div v-else class="profile-content">
        
        <!-- 基本資料卡片 -->
        <div class="info-card">
          <h2>📋 基本資料</h2>
          <div class="info-item">
            <span class="label">暱稱：</span>
            <span class="value">{{ userInfo.nickname }}</span>
          </div>
          <div class="info-item">
            <span class="label">Email：</span>
            <span class="value">{{ userInfo.email }}</span>
          </div>
        </div>

        <!-- 占卜統計卡片 -->
        <div class="stats-card">
          <h2>📊 占卜統計</h2>
          
          <div class="stats-grid">
            <!-- 總次數 -->
            <div class="stat-box total">
              <div class="stat-icon">🔮</div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.totalCount }}</div>
                <div class="stat-label">總占卜次數</div>
              </div>
            </div>

            <!-- 文殊占卜 -->
            <div class="stat-box manju">
              <div class="stat-icon">📿</div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.manjuCount }}</div>
                <div class="stat-label">文殊占卜</div>
              </div>
            </div>

            <!-- 觀音占卜 -->
            <div class="stat-box avalo">
              <div class="stat-icon">🙏</div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.avaloCount }}</div>
                <div class="stat-label">觀音占卜</div>
              </div>
            </div>

            <!-- 每日一籤 -->
            <div class="stat-box japan">
              <div class="stat-icon">🎋</div>
              <div class="stat-info">
                <div class="stat-number">{{ stats.japanCount }}</div>
                <div class="stat-label">每日一籤</div>
              </div>
            </div>
          </div>

          <!-- 比例顯示 -->
          <div class="ratio-display" v-if="stats.totalCount > 0">
            <div class="ratio-bar">
              <div 
                class="ratio-manju" 
                :style="{ width: (stats.manjuCount / stats.totalCount * 100) + '%' }"
              ></div>
              <div 
                class="ratio-avalo" 
                :style="{ width: (stats.avaloCount / stats.totalCount * 100) + '%' }"
              ></div>
              <div 
                class="ratio-japan" 
                :style="{ width: (stats.japanCount / stats.totalCount * 100) + '%' }"
              ></div>
            </div>
            <div class="ratio-labels">
              <span class="ratio-label manju-label">
                文殊 {{ Math.round(stats.manjuCount / stats.totalCount * 100) }}%
              </span>
              <span class="ratio-label avalo-label">
                觀音 {{ Math.round(stats.avaloCount / stats.totalCount * 100) }}%
              </span>
              <span class="ratio-label japan-label">
                每日一籤 {{ Math.round(stats.japanCount / stats.totalCount * 100) }}%
              </span>
            </div>
          </div>
        </div>

        <!-- 按鈕區 -->
        <div class="action-buttons">
          <button @click="$emit('back')" class="btn-back">返回主頁</button>
        </div>

      </div>
    </div>
  </div>
</template>

<style scoped>
#profileContainer {
  background-color: #0072E3;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

#profileBox {
  background-color: white;
  width: 90vw;
  max-width: 800px;
  min-height: 70vh;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  position: relative;
}

#backBtn {
  position: absolute;
  top: 20px;
  left: 20px;
  background-color: #0066CC;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  font-size: 1rem;
  transition: background-color 0.2s;
}

#backBtn:hover {
  background-color: #0052A3;
}

.main-title {
  text-align: center;
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 40px;
  margin-top: 20px;
}

.loading {
  text-align: center;
  padding: 60px 20px;
  font-size: 1.2rem;
  color: #666;
}

.error-message {
  text-align: center;
  padding: 60px 20px;
}

.error-message p {
  color: #dc2626;
  font-size: 1.2rem;
  margin-bottom: 20px;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

/* 基本資料卡片 */
.info-card {
  background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
  padding: 25px;
  border-radius: 12px;
  border-left: 4px solid #0066CC;
}

.info-card h2 {
  font-size: 1.5rem;
  color: #0066CC;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid rgba(0, 102, 204, 0.1);
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  font-weight: 600;
  color: #374151;
  min-width: 100px;
}

.info-item .value {
  color: #1f2937;
  font-size: 1.05rem;
}

/* 統計卡片 */
.stats-card {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  padding: 25px;
  border-radius: 12px;
  border-left: 4px solid #f59e0b;
}

.stats-card h2 {
  font-size: 1.5rem;
  color: #d97706;
  margin-bottom: 25px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 15px;
  margin-bottom: 25px;
}

.stat-box {
  background: white;
  padding: 20px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.stat-box:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-box.total {
  border-left: 4px solid #8b5cf6;
}

.stat-box.manju {
  border-left: 4px solid #f59e0b;
}

.stat-box.avalo {
  border-left: 4px solid #8b5cf6;
}

.stat-box.japan {
  border-left: 4px solid #dc2626;
}

.stat-icon {
  font-size: 2rem;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: #1f2937;
}

.stat-label {
  font-size: 0.9rem;
  color: #6b7280;
  margin-top: 5px;
}

/* 比例條 */
.ratio-display {
  margin-top: 20px;
}

.ratio-bar {
  display: flex;
  height: 30px;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.ratio-manju {
  background: linear-gradient(90deg, #f59e0b, #d97706);
  transition: width 0.5s ease;
}

.ratio-avalo {
  background: linear-gradient(90deg, #8b5cf6, #7c3aed);
  transition: width 0.5s ease;
}

.ratio-japan {
  background: linear-gradient(90deg, #dc2626, #b91c1c);
  transition: width 0.5s ease;
}

.ratio-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  padding: 0 10px;
}

.ratio-label {
  font-size: 0.9rem;
  font-weight: 600;
}

.manju-label {
  color: #d97706;
}

.avalo-label {
  color: #7c3aed;
}

.japan-label {
  color: #dc2626;
}

/* 按鈕區 */
.action-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
}

.btn-primary,
.btn-back {
  background-color: #0066CC;
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 8px;
  font-size: 1.1rem;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s;
}

.btn-primary:hover,
.btn-back:hover {
  background-color: #0052A3;
}

/* 響應式設計 */
@media (max-width: 768px) {
  #profileBox {
    width: 95vw;
    padding: 20px;
  }

  .main-title {
    font-size: 1.8rem;
    margin-top: 40px;
  }

  #backBtn {
    top: 10px;
    left: 10px;
    padding: 8px 15px;
    font-size: 0.9rem;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
}
</style>