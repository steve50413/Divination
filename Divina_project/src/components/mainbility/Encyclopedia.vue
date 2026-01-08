<script setup>
import { ref, onMounted, computed } from 'vue'

const emits = defineEmits(['back', 'viewPoem'])

const activeTab = ref('manju')  // 'manju', 'avalo', 'japan'
const manjuPoems = ref([])
const avaloPoems = ref([])
const japanPoems = ref([])
const loading = ref(true)
const error = ref('')
const searchQuery = ref('')

onMounted(async () => {
  await loadPoems()
})

// 載入所有籤詩
async function loadPoems() {
  loading.value = true
  
  try {
    // 載入文殊籤詩
    const manjuResponse = await fetch('http://localhost:3000/api/divination/poems/manju')
    const manjuData = await manjuResponse.json()
    
    // 載入觀音籤詩
    const avaloResponse = await fetch('http://localhost:3000/api/divination/poems/avalo')
    const avaloData = await avaloResponse.json()
    
    // 載入日本籤詩
    const japanResponse = await fetch('http://localhost:3000/api/divination/poems/japan')
    const japanData = await japanResponse.json()
    
    if (manjuData.success) {
      manjuPoems.value = manjuData.data
    }
    
    if (avaloData.success) {
      avaloPoems.value = avaloData.data
    }
    
    if (japanData.success) {
      japanPoems.value = japanData.data
    }
    
  } catch (err) {
    console.error('載入籤詩錯誤:', err)
    error.value = '載入失敗'
  } finally {
    loading.value = false
  }
}

// 當前顯示的籤詩列表
const currentPoems = computed(() => {
  if (activeTab.value === 'manju') return manjuPoems.value
  if (activeTab.value === 'avalo') return avaloPoems.value
  if (activeTab.value === 'japan') return japanPoems.value
  return []
})

// 搜尋篩選
const filteredPoems = computed(() => {
  if (!searchQuery.value.trim()) {
    return currentPoems.value
  }
  
  const query = searchQuery.value.toLowerCase()
  
  return currentPoems.value.filter(poem => {
    // 文殊、觀音籤詩
    if (activeTab.value !== 'japan') {
      return (
        poem.title?.toLowerCase().includes(query) ||
        poem.summary?.toLowerCase().includes(query) ||
        poem.tibetanCode?.toLowerCase().includes(query)
      )
    }
    // 日本籤詩
    else {
      return (
        poem.number?.toLowerCase().includes(query) ||
        poem.fortuneLevel?.toLowerCase().includes(query) ||
        poem.content?.toLowerCase().includes(query)
      )
    }
  })
})

// 查看籤詩詳情
function viewPoemDetail(poem) {
  const type = activeTab.value
  
  // 組裝成跟占卜結果一樣的格式
  let result
  
  if (type === 'japan') {
    // 日本籤詩格式
    result = {
      id: null,
      type: 'japan',
      question: '（來自籤詩百科）',
      poem: {
        id: poem.id,
        number: poem.number,
        fortuneLevel: poem.fortuneLevel,
        content: poem.content,
        wish: poem.wish,
        illness: poem.illness,
        waitingPerson: poem.waitingPerson,
        lostItem: poem.lostItem,
        lifeEvents: poem.lifeEvents
      },
      createdAt: new Date().toISOString()
    }
  } else {
    // 文殊、觀音籤詩格式
    result = {
      id: null,
      type: type,
      question: '（來自籤詩百科）',
      poem: poem,
      createdAt: new Date().toISOString()
    }
  }
  
  emits('viewPoem', result)
}
</script>

<template>
  <div class="encyclopedia-container">
    <div class="encyclopedia-box">
      
      <!-- 返回按鈕 -->
      <button class="back-btn" @click="$emit('back')">返回</button>

      <!-- 標題 -->
      <h1 class="main-title">📚 籤詩百科</h1>

      <!-- Tab 切換 -->
      <div class="tabs">
        <button 
          :class="{ active: activeTab === 'manju' }" 
          @click="activeTab = 'manju'"
        >
          文殊籤詩
        </button>
        <button 
          :class="{ active: activeTab === 'avalo' }" 
          @click="activeTab = 'avalo'"
        >
          觀音籤詩
        </button>
        <button 
          :class="{ active: activeTab === 'japan' }" 
          @click="activeTab = 'japan'"
        >
          日本籤詩
        </button>
      </div>

      <!-- 搜尋欄 -->
      <div class="search-box" v-if="!loading && !error">
        <input 
          type="text" 
          v-model="searchQuery" 
          :placeholder="activeTab === 'japan' 
            ? '🔍 搜尋籤號、吉凶或內容...' 
            : '🔍 搜尋標題、內容或藏文代碼...'"
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

      <!-- 籤詩列表 -->
      <div v-else class="poems-grid">
        
        <!-- 無結果 -->
        <div v-if="filteredPoems.length === 0" class="empty">
          <p>找不到符合的籤詩</p>
        </div>

        <!-- 日本籤詩卡片 -->
        <div 
          v-else-if="activeTab === 'japan'"
          v-for="poem in filteredPoems" 
          :key="poem.id" 
          class="poem-card japan-card"
          @click="viewPoemDetail(poem)"
        >
          <div class="card-header">
            <h3 class="poem-number">{{ poem.number }}</h3>
            <span 
              class="fortune-badge" 
              :style="{ 
                backgroundColor: getFortuneLevelColor(poem.fortuneLevel) 
              }"
            >
              {{ poem.fortuneLevel }}
            </span>
          </div>
          <div class="card-content">
            <p>{{ poem.content?.substring(0, 80) }}...</p>
          </div>
        </div>

        <!-- 文殊、觀音籤詩卡片 -->
        <div 
          v-else
          v-for="poem in filteredPoems" 
          :key="poem.id || `poem-${index}`"  
          class="poem-card"
          @click="viewPoemDetail(poem)"
        >
          <div class="card-header">
            <h3>{{ poem.title || poem.direction }}</h3>
            <span class="tibetan-code">{{ poem.tibetanCode }}</span>
          </div>
          <div class="card-content">
            <p>{{ poem.summary?.substring(0, 100) }}...</p>
          </div>
        </div>

      </div>

    </div>
  </div>
</template>

<script>
// 吉凶等級的顏色
function getFortuneLevelColor(level) {
  const colorMap = {
    '大吉': '#d32f2f',
    '中吉': '#f57c00',
    '小吉': '#fbc02d',
    '吉': '#388e3c',
    '末吉': '#0288d1',
    '末小吉': '#0288d1',
    '半吉': '#7b1fa2',
    '凶': '#5d4037',
    '大凶': '#212121'
  }
  return colorMap[level] || '#666'
}
</script>

<style scoped>
.encyclopedia-container {
  background-color: #0072E3;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.encyclopedia-box {
  background-color: white;
  width: 90vw;
  max-width: 1200px;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  position: relative;
  max-height: 90vh;
  overflow-y: auto;
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
.tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  border-bottom: 2px solid #e5e7eb;
  padding-bottom: 10px;
}

.tabs button {
  background: none;
  border: none;
  padding: 12px 24px;
  font-size: 1.1rem;
  cursor: pointer;
  color: #6b7280;
  font-weight: 600;
  transition: all 0.3s;
  border-bottom: 3px solid transparent;
}

.tabs button.active {
  color: #0066CC;
  border-bottom-color: #0066CC;
}

.tabs button:hover {
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

/* 籤詩網格 */
.poems-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.poem-card {
  background-color: #f9fafb;
  padding: 20px;
  border-radius: 12px;
  border: 2px solid #e5e7eb;
  cursor: pointer;
  transition: all 0.3s;
}

.poem-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  border-color: #0066CC;
}

.japan-card:hover {
  border-color: #d32f2f;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.card-header h3 {
  font-size: 1.2rem;
  color: #0066CC;
  margin: 0;
}

.japan-card .card-header h3 {
  color: #d32f2f;
}

.poem-number {
  font-size: 1.3rem;
  font-weight: bold;
}

.tibetan-code {
  background-color: #e5e7eb;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.85rem;
  color: #666;
}

.fortune-badge {
  color: white;
  padding: 6px 14px;
  border-radius: 15px;
  font-size: 0.9rem;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.card-content p {
  color: #666;
  line-height: 1.6;
  font-size: 0.95rem;
  margin: 0;
}

@media (max-width: 768px) {
  .encyclopedia-box {
    width: 95vw;
    padding: 20px;
  }

  .main-title {
    font-size: 1.8rem;
  }

  .tabs {
    flex-wrap: wrap;
  }

  .poems-grid {
    grid-template-columns: 1fr;
  }
}
</style>