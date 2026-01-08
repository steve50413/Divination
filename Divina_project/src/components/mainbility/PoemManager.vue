<script setup>
import { ref, computed, onMounted } from 'vue'

const emits = defineEmits(['back', 'editPoem'])

const activeTab = ref('manju')  // 'manju', 'avalo', 'japan'
const manjuPoems = ref([])
const avaloPoems = ref([])
const japanPoems = ref([])
const loading = ref(true)
const searchQuery = ref('')

onMounted(async () => {
  await loadAllPoems()
})

// 載入所有籤詩
async function loadAllPoems() {
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
    
    if (manjuData.success) manjuPoems.value = manjuData.data
    if (avaloData.success) avaloPoems.value = avaloData.data
    if (japanData.success) japanPoems.value = japanData.data
    
  } catch (err) {
    console.error('載入錯誤:', err)
    alert('載入失敗')
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
    if (activeTab.value === 'japan') {
      return (
        poem.number?.toLowerCase().includes(query) ||
        poem.content?.toLowerCase().includes(query)
      )
    } else {
      return (
        poem.title?.toLowerCase().includes(query) ||
        poem.tibetanCode?.toLowerCase().includes(query) ||
        poem.summary?.toLowerCase().includes(query)
      )
    }
  })
})

// 新增籤詩
function addPoem() {
  emits('editPoem', { type: activeTab.value, poem: null })
}

// 編輯籤詩
function editPoem(poem) {
  emits('editPoem', { type: activeTab.value, poem: poem })
}

// 刪除籤詩
async function deletePoem(id) {
  if (!confirm('確定要刪除這首籤詩嗎？')) {
    return
  }
  
  const token = localStorage.getItem('token')
  const endpoint = `http://localhost:3000/api/admin/poems/${activeTab.value}/${id}`
  
  try {
    const response = await fetch(endpoint, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    const data = await response.json()
    
    if (data.success) {
      alert('刪除成功')
      await loadAllPoems()
    } else {
      alert(data.message || '刪除失敗')
    }
  } catch (err) {
    console.error('刪除錯誤:', err)
    alert('連線失敗')
  }
}
</script>

<template>
  <div class="manager-container">
    <div class="manager-box">
      
      <!-- 返回按鈕 -->
      <button class="back-btn" @click="$emit('back')">返回</button>

      <!-- 標題 -->
      <h1 class="main-title">📝 籤詩管理</h1>

      <!-- Tab 切換 -->
      <div class="tabs">
        <button 
          :class="{ active: activeTab === 'manju' }" 
          @click="activeTab = 'manju'"
        >
          文殊籤詩 ({{ manjuPoems.length }})
        </button>
        <button 
          :class="{ active: activeTab === 'avalo' }" 
          @click="activeTab = 'avalo'"
        >
          觀音籤詩 ({{ avaloPoems.length }})
        </button>
        <button 
          :class="{ active: activeTab === 'japan' }" 
          @click="activeTab = 'japan'"
        >
          日本籤詩 ({{ japanPoems.length }})
        </button>
      </div>

      <!-- 操作區 -->
      <div class="actions">
        <input 
          type="text" 
          v-model="searchQuery" 
          placeholder="🔍 搜尋籤詩..."
          class="search-input"
        />
        <button @click="addPoem" class="add-btn">➕ 新增籤詩</button>
      </div>

      <!-- 載入中 -->
      <div v-if="loading" class="loading">載入中...</div>

      <!-- 籤詩列表 -->
      <div v-else class="poems-table">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th v-if="activeTab === 'japan'">籤號</th>
              <th v-else>藏文代碼</th>
              <th>標題</th>
              <th>內容預覽</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="poem in filteredPoems" :key="poem.id">
              <td>{{ poem.id }}</td>
              <td v-if="activeTab === 'japan'">{{ poem.number }}</td>
              <td v-else>{{ poem.tibetanCode }}</td>
              <td>
                <span v-if="activeTab === 'japan'">{{ poem.fortuneLevel }}</span>
                <span v-else>{{ poem.title || poem.direction }}</span>
              </td>
              <td class="preview">
                <span v-if="activeTab === 'japan'">{{ poem.content?.substring(0, 50) }}...</span>
                <span v-else>{{ poem.summary?.substring(0, 50) }}...</span>
              </td>
              <td class="actions-cell">
                <button @click="editPoem(poem)" class="edit-btn">編輯</button>
                <button @click="deletePoem(poem.id)" class="delete-btn">刪除</button>
              </td>
            </tr>
          </tbody>
        </table>

        <!-- 無資料 -->
        <div v-if="filteredPoems.length === 0" class="empty">
          <p>沒有找到籤詩</p>
        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
.manager-container {
  background-color: #0072E3;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.manager-box {
  background-color: white;
  width: 95vw;
  max-width: 1400px;
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

.actions {
  display: flex;
  gap: 15px;
  margin-bottom: 25px;
}

.search-input {
  flex: 1;
  padding: 12px 20px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 1rem;
}

.search-input:focus {
  outline: none;
  border-color: #0066CC;
}

.add-btn {
  background-color: #10b981;
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
  white-space: nowrap;
}

.add-btn:hover {
  background-color: #059669;
}

.loading {
  text-align: center;
  padding: 60px;
  color: #666;
  font-size: 1.2rem;
}

.poems-table {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

thead {
  background-color: #f9fafb;
}

th {
  padding: 15px;
  text-align: left;
  font-weight: 600;
  color: #374151;
  border-bottom: 2px solid #e5e7eb;
}

td {
  padding: 15px;
  border-bottom: 1px solid #e5e7eb;
}

.preview {
  max-width: 300px;
  color: #666;
  font-size: 0.9rem;
}

.actions-cell {
  display: flex;
  gap: 10px;
}

.edit-btn, .delete-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s;
}

.edit-btn {
  background-color: #3b82f6;
  color: white;
}

.edit-btn:hover {
  background-color: #2563eb;
}

.delete-btn {
  background-color: #ef4444;
  color: white;
}

.delete-btn:hover {
  background-color: #dc2626;
}

.empty {
  text-align: center;
  padding: 60px 20px;
  color: #999;
  font-size: 1.1rem;
}

@media (max-width: 768px) {
  .manager-box {
    padding: 20px;
  }

  .main-title {
    font-size: 1.8rem;
  }

  .tabs {
    flex-wrap: wrap;
  }

  .actions {
    flex-direction: column;
  }

  table {
    font-size: 0.85rem;
  }

  th, td {
    padding: 10px;
  }

  .preview {
    max-width: 150px;
  }

  .actions-cell {
    flex-direction: column;
  }
}
</style>