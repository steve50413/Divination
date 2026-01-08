<script setup>
import { ref, onMounted } from 'vue'

const emits = defineEmits(['back', 'showResult', 'showDailyResult', 'editNote'])

const history = ref([])
const loading = ref(true)
const error = ref('')
const searchQuery = ref('')
const filterType = ref('all')  // 類型篩選：all, manju, avalo, japan

// 載入歷史記錄
async function fetchHistory() {
  const token = localStorage.getItem('token')
  
  if (!token) {
    error.value = '請先登入'
    loading.value = false
    return
  }

  try {
    const response = await fetch('http://localhost:3000/api/divination/history', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })

    const data = await response.json()

    if (data.success) {
      history.value = data.data
    } else {
      error.value = data.message || '載入失敗'
    }
  } catch (err) {
    console.error('載入錯誤:', err)
    error.value = '連線失敗'
  } finally {
    loading.value = false
  }
}

// 搜尋過濾 + 類型篩選
function filteredHistory() {
  let filtered = history.value
  
  // 按類型篩選
  if (filterType.value !== 'all') {
    filtered = filtered.filter(item => item.type === filterType.value)
  }
  
  // 搜尋篩選
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase()
    filtered = filtered.filter(item => 
      item.question.toLowerCase().includes(query) ||
      item.title?.toLowerCase().includes(query) ||
      item.summary?.toLowerCase().includes(query)
    )
  }
  
  return filtered
}

// 查看詳細結果
function viewDetail(id) {
  const item = history.value.find(h => h.id === id)
  
  if (item && item.type === 'japan') {
    emits('showDailyResult', id)  // 每日一籤用 DailyResult
  } else {
    emits('showResult', id)  // 文殊/觀音用 DivinationResult
  }
}

// 刪除記錄
async function deleteRecord(id) {
  const token = localStorage.getItem('token')
  
  try {
    const response = await fetch(`http://localhost:3000/api/divination/history/${id}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })

    const data = await response.json()

    if (data.success) {
      await fetchHistory()
    } else {
      alert(data.message || '刪除失敗')
    }
  } catch (err) {
    console.error('刪除錯誤:', err)
    alert('連線失敗')
  }
}

// 編輯日記
function editNote(historyId) {
  emits('editNote', historyId)
}

// 取得類型標籤文字
function getTypeLabel(type) {
  const labels = {
    'manju': '文殊占卜',
    'avalo': '觀音占卜',
    'japan': '每日一籤'
  }
  return labels[type] || type
}

// 取得類型標籤顏色
function getTypeColor(type) {
  const colors = {
    'manju': '#0066CC',
    'avalo': '#10b981',
    'japan': '#d32f2f'
  }
  return colors[type] || '#666'
}

onMounted(() => {
  fetchHistory()
})
</script>

<template>
  <div class="history-container">
    <div class="history-box">
      
      <!-- 返回按鈕 -->
      <button class="back-btn" @click="$emit('back')">返回</button>

      <!-- 標題 -->
      <h1 class="main-title">📜 占卜歷史記錄</h1>

      <!-- Tab 切換 -->
      <div class="filter-tabs">
        <button 
          :class="{ active: filterType === 'all' }" 
          @click="filterType = 'all'"
        >
          全部
        </button>
        <button 
          :class="{ active: filterType === 'manju' }" 
          @click="filterType = 'manju'"
        >
          文殊占卜
        </button>
        <button 
          :class="{ active: filterType === 'avalo' }" 
          @click="filterType = 'avalo'"
        >
          觀音占卜
        </button>
        <button 
          :class="{ active: filterType === 'japan' }" 
          @click="filterType = 'japan'"
        >
          每日一籤
        </button>
      </div>

      <!-- 搜尋欄 -->
      <div class="search-box" v-if="!loading && !error && history.length > 0">
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="🔍 搜尋問題、籤詩標題或內容..."
        />
      </div>

      <!-- 載入中 -->
      <div v-if="loading" class="loading">
        <p>載入中...</p>
      </div>

      <!-- 錯誤訊息 -->
      <div v-else-if="error" class="error">
        <p>{{ error }}</p>
      </div>

      <!-- 無記錄 -->
      <div v-else-if="filteredHistory().length === 0" class="empty">
        <p v-if="filterType === 'all' && !searchQuery">尚未有任何占卜記錄</p>
        <p v-else-if="filterType !== 'all' && !searchQuery">尚未有「{{ getTypeLabel(filterType) }}」的記錄</p>
        <p v-else>找不到符合的記錄</p>
      </div>

      <!-- 歷史記錄列表 -->
      <div v-else class="history-list">
        <div v-for="item in filteredHistory()" :key="item.id" class="history-item">
          
          <!-- 類型標籤 -->
          <div class="type-badge" :style="{ backgroundColor: getTypeColor(item.type) }">
            {{ getTypeLabel(item.type) }}
          </div>

          <!-- 問題 -->
          <div class="question">
            <strong>問題：</strong>{{ item.question }}
          </div>

          <!-- 籤詩資訊 -->
          <div class="poem-info">
            <span class="poem-title">{{ item.title }}</span>
            <span class="poem-summary">{{ item.summary?.substring(0, 50) }}...</span>
          </div>

          <!-- 時間 -->
          <div class="timestamp">
            {{ new Date(item.createdAt).toLocaleString('zh-TW') }}
          </div>

          <!-- 日記預覽區 -->
          <div v-if="item.hasNote" class="note-preview">
            <div class="note-badge">
              <span v-if="item.isAccurate === true" class="accurate-badge">✓ 準確</span>
              <span v-else-if="item.isAccurate === false" class="inaccurate-badge">✗ 不準</span>
              <span v-else class="no-rating-badge">📝 有日記</span>
            </div>
            <p class="note-text">{{ item.notePreview }}</p>
          </div>

          <!-- 操作按鈕 -->
          <div class="action-buttons">
            <button @click="viewDetail(item.id)" class="btn-view">
              👁️ 查看結果
            </button>
            <button @click="editNote(item.id)" class="btn-note">
              {{ item.hasNote ? '✏️ 編輯日記' : '📝 寫日記' }}
            </button>
            <button @click="deleteRecord(item.id)" class="btn-delete">
              🗑️ 刪除
            </button>
          </div>

        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
.history-container {
  background-color: #0072E3;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.history-box {
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
  margin-bottom: 30px;
  margin-top: 20px;
}

/* Tab 切換 */
.filter-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  border-bottom: 2px solid #e5e7eb;
  padding-bottom: 10px;
}

.filter-tabs button {
  background: none;
  border: none;
  padding: 10px 20px;
  font-size: 1rem;
  cursor: pointer;
  color: #6b7280;
  font-weight: 500;
  transition: all 0.3s;
  border-bottom: 3px solid transparent;
}

.filter-tabs button.active {
  color: #0066CC;
  border-bottom-color: #0066CC;
}

.filter-tabs button:hover {
  color: #0066CC;
}

/* 搜尋欄 */
.search-box {
  margin-bottom: 25px;
}

.search-box input {
  width: 100%;
  padding: 12px 20px;
  border: 2px solid #e5e7eb;
  border-radius: 10px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.search-box input:focus {
  outline: none;
  border-color: #0066CC;
}

.loading, .error, .empty {
  text-align: center;
  padding: 60px 20px;
  color: #666;
  font-size: 1.1rem;
}

.error {
  color: #ef4444;
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.history-item {
  background-color: #f9fafb;
  padding: 20px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  transition: box-shadow 0.2s;
}

.history-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.type-badge {
  display: inline-block;
  color: white;
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  margin-bottom: 12px;
}

.question {
  font-size: 1.1rem;
  color: #333;
  margin-bottom: 12px;
  line-height: 1.6;
}

.poem-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 12px;
}

.poem-title {
  font-weight: 600;
  color: #0066CC;
  font-size: 1rem;
}

.poem-summary {
  color: #666;
  font-size: 0.9rem;
}

.timestamp {
  color: #999;
  font-size: 0.85rem;
  margin-bottom: 12px;
}

/* 日記預覽 */
.note-preview {
  background-color: #fef3c7;
  padding: 12px;
  border-radius: 8px;
  margin-top: 12px;
  margin-bottom: 12px;
}

.note-badge {
  margin-bottom: 8px;
}

.accurate-badge {
  background-color: #10b981;
  color: white;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
}

.inaccurate-badge {
  background-color: #ef4444;
  color: white;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
}

.no-rating-badge {
  background-color: #6b7280;
  color: white;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
}

.note-text {
  color: #78350f;
  font-size: 0.9rem;
  line-height: 1.5;
  margin: 0;
}

.action-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.btn-view, .btn-note, .btn-delete {
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s;
}

.btn-view {
  background-color: #0066CC;
  color: white;
}

.btn-view:hover {
  background-color: #0052A3;
}

.btn-note {
  background-color: #f59e0b;
  color: white;
}

.btn-note:hover {
  background-color: #d97706;
}

.btn-delete {
  background-color: #ef4444;
  color: white;
}

.btn-delete:hover {
  background-color: #dc2626;
}

@media (max-width: 768px) {
  .history-box {
    width: 95vw;
    padding: 20px;
  }

  .main-title {
    font-size: 1.8rem;
  }

  .filter-tabs {
    flex-wrap: wrap;
  }

  .action-buttons {
    flex-direction: column;
  }

  .btn-view, .btn-note, .btn-delete {
    width: 100%;
  }
}
</style>