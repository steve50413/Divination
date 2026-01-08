<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps(['historyId'])
const emits = defineEmits(['close', 'saved'])

const note = ref('')
const isAccurate = ref(null)  // null, true, false
const loading = ref(false)
const loadingData = ref(true)

onMounted(async () => {
  await loadNote()
})

// 載入現有日記
async function loadNote() {
  const token = localStorage.getItem('token')
  
  try {
    const response = await fetch(
  `http://localhost:3000/api/divination/history/${props.historyId}`,  // ← 改這行
  {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  }
)
    

    const data = await response.json()

    if (data.success) {
      note.value = data.data.note || ''
      isAccurate.value = data.data.isAccurate
    }
  } catch (err) {
    // 沒有日記是正常的，不需要報錯
    console.log('尚未有日記')
  } finally {
    loadingData.value = false
  }
}

// 儲存日記
async function saveNote() {
  const token = localStorage.getItem('token')
  loading.value = true
  
  try {
    const response = await fetch('http://localhost:3000/api/divination/note', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        historyId: props.historyId,
        note: note.value,
        isAccurate: isAccurate.value
      })
    })

    const data = await response.json()

    if (data.success) {
      emits('saved')
      emits('close')
    } else {
      alert(data.message || '儲存失敗')
    }
  } catch (err) {
    console.error('儲存錯誤:', err)
    alert('連線失敗')
  } finally {
    loading.value = false
  }
}

// 刪除日記
async function deleteNote() {
  if (!confirm('確定要刪除這篇日記嗎？')) {
    return
  }

  const token = localStorage.getItem('token')
  loading.value = true
  
  try {
    const response = await fetch(`http://localhost:3000/api/divination/note/${props.historyId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })

    const data = await response.json()

    if (data.success) {
      emits('saved')
      emits('close')
    } else {
      alert(data.message || '刪除失敗')
    }
  } catch (err) {
    console.error('刪除錯誤:', err)
    alert('連線失敗')
  } finally {
    loading.value = false
  }
}


</script>

<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-content">
      
      <!-- 標題 -->
      <div class="modal-header">
        <h2>📝 占卜日記</h2>
        <button class="close-btn" @click="$emit('close')">✕</button>
      </div>

      <!-- 載入中 -->
      <div v-if="loadingData" class="loading">
        <p>載入中...</p>
      </div>

      <!-- 編輯區 -->
      <div v-else class="modal-body">
        
        <!-- 準確度評價 -->
        <div class="rating-section">
          <h3>這次占卜準確嗎？</h3>
          <div class="rating-buttons">
            <button 
              class="rating-btn accurate"
              :class="{ active: isAccurate === true }"
              @click="isAccurate = true"
            >
              ✓ 準確
            </button>
            <button 
              class="rating-btn neutral"
              :class="{ active: isAccurate === null }"
              @click="isAccurate = null"
            >
              − 不確定
            </button>
            <button 
              class="rating-btn inaccurate"
              :class="{ active: isAccurate === false }"
              @click="isAccurate = false"
            >
              ✗ 不準
            </button>
          </div>
        </div>

        <!-- 日記內容 -->
        <div class="note-section">
          <h3>寫下你的心得或後續發展：</h3>
          <textarea 
            v-model="note" 
            placeholder="記錄這次占卜的心得、想法，或後續發生的事情..."
            rows="8"
          ></textarea>
        </div>

        <!-- 按鈕區 -->
        <div class="button-group">
          <button @click="deleteNote" class="btn-delete-note" v-if="note || isAccurate !== null">
            刪除日記
          </button>
          <button @click="$emit('close')" class="btn-cancel">
            取消
          </button>
          <button @click="saveNote" class="btn-save" :disabled="loading">
            {{ loading ? '儲存中...' : '儲存' }}
          </button>
        </div>

      </div>

    </div>
  </div>
</template>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background-color: white;
  width: 90%;
  max-width: 600px;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h2 {
  font-size: 1.5rem;
  color: #333;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #6b7280;
  cursor: pointer;
  padding: 5px 10px;
}

.close-btn:hover {
  color: #333;
}

.modal-body {
  padding: 25px;
}

.loading {
  text-align: center;
  padding: 40px 20px;
  color: #666;
}

/* 準確度評價 */
.rating-section {
  margin-bottom: 25px;
}

.rating-section h3 {
  font-size: 1.1rem;
  color: #333;
  margin-bottom: 12px;
}

.rating-buttons {
  display: flex;
  gap: 10px;
}

.rating-btn {
  flex: 1;
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  background-color: white;
  cursor: pointer;
  font-weight: 600;
  font-size: 1rem;
  transition: all 0.3s;
}

.rating-btn.accurate {
  color: #10b981;
}

.rating-btn.accurate.active {
  background-color: #10b981;
  color: white;
  border-color: #10b981;
}

.rating-btn.inaccurate {
  color: #ef4444;
}

.rating-btn.inaccurate.active {
  background-color: #ef4444;
  color: white;
  border-color: #ef4444;
}

.rating-btn.neutral {
  color: #6b7280;
}

.rating-btn.neutral.active {
  background-color: #6b7280;
  color: white;
  border-color: #6b7280;
}

/* 日記內容 */
.note-section h3 {
  font-size: 1.1rem;
  color: #333;
  margin-bottom: 12px;
}

.note-section textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 1rem;
  font-family: inherit;
  resize: vertical;
  transition: border-color 0.3s;
}

.note-section textarea:focus {
  outline: none;
  border-color: #0066CC;
}

/* 按鈕區 */
.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 25px;
}

.btn-cancel,
.btn-save,
.btn-delete-note {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-cancel {
  background-color: #e5e7eb;
  color: #6b7280;
}

.btn-cancel:hover {
  background-color: #d1d5db;
}

.btn-save {
  background-color: #0066CC;
  color: white;
}

.btn-save:hover {
  background-color: #0052A3;
}

.btn-save:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.btn-delete-note {
  background-color: #ef4444;
  color: white;
  margin-right: auto;
}

.btn-delete-note:hover {
  background-color: #dc2626;
}

@media (max-width: 768px) {
  .rating-buttons {
    flex-direction: column;
  }

  .button-group {
    flex-direction: column-reverse;
  }

  .btn-delete-note {
    margin-right: 0;
  }
}
</style>