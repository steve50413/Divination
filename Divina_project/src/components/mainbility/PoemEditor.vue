<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps(['type', 'poem'])
const emits = defineEmits(['back', 'saved'])

const loading = ref(false)
const formData = ref({
  id: null,
  // 共用欄位
  tibetanCode: '',
  title: '',
  summary: '',
  
  // 文殊專用
  direction: '',
  familyLife: '',
  strategy: '',
  fortune: '',
  enemy: '',
  travel: '',
  illness: '',
  practice: '',
  lostItem: '',
  visitor: '',
  otherMatters: '',
  advice: '',
  
  // 日本籤詩專用
  number: '',
  fortuneLevel: '',
  content: '',
  wish: '',
  waitingPerson: '',
  lifeEvents: ''
})

onMounted(async () => {
  if (props.poem) {
    // 編輯模式 - 載入現有資料
    await loadPoem()
  }
})

// 載入籤詩資料
async function loadPoem() {
  if (props.type === 'manju') {
    formData.value = {
      id: props.poem.id,
      tibetanCode: props.poem.tibetanCode || '',
      direction: props.poem.direction || '',
      title: props.poem.title || '',
      summary: props.poem.summary || '',
      familyLife: props.poem.familyLife || '',
      strategy: props.poem.strategy || '',
      fortune: props.poem.fortune || '',
      enemy: props.poem.enemy || '',
      travel: props.poem.travel || '',
      illness: props.poem.illness || '',
      practice: props.poem.practice || '',
      lostItem: props.poem.lostItem || '',
      visitor: props.poem.visitor || '',
      otherMatters: props.poem.otherMatters || '',
      advice: props.poem.advice || ''
    }
  } else if (props.type === 'avalo') {
    formData.value = {
      id: props.poem.id,
      tibetanCode: props.poem.tibetanCode || '',
      title: props.poem.title || '',
      summary: props.poem.summary || ''
    }
  } else if (props.type === 'japan') {
    formData.value = {
      id: props.poem.id,
      number: props.poem.number || '',
      fortuneLevel: props.poem.fortuneLevel || '',
      content: props.poem.content || '',
      wish: props.poem.wish || '',
      illness: props.poem.illness || '',
      waitingPerson: props.poem.waitingPerson || '',
      lostItem: props.poem.lostItem || '',
      lifeEvents: props.poem.lifeEvents || ''
    }
  }
}

// 儲存籤詩
async function save() {
  const token = localStorage.getItem('token')
  
  if (!token) {
    alert('請先登入')
    return
  }
  
  loading.value = true
  
  try {
    const endpoint = `http://localhost:3000/api/admin/poems/${props.type}`
    
    const response = await fetch(endpoint, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        id: formData.value.id,
        type: props.type,
        ...formData.value
      })
    })
    
    const data = await response.json()
    
    if (data.success) {
      alert(formData.value.id ? '更新成功' : '新增成功')
      emits('saved')
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

// 取得標題文字
function getTitle() {
  if (props.type === 'manju') return '文殊籤詩'
  if (props.type === 'avalo') return '觀音籤詩'
  if (props.type === 'japan') return '日本籤詩'
  return '籤詩'
}
</script>

<template>
  <div class="editor-container">
    <div class="editor-box">
      
      <!-- 返回按鈕 -->
      <button class="back-btn" @click="$emit('back')">返回</button>

      <!-- 標題 -->
      <h1 class="main-title">
        {{ formData.id ? '編輯' : '新增' }} {{ getTitle() }}
      </h1>

      <!-- 表單 -->
      <form @submit.prevent="save" class="form">
        
        <!-- 文殊籤詩欄位 -->
        <template v-if="type === 'manju'">
          <div class="form-group">
            <label>藏文代碼 *</label>
            <input type="text" v-model="formData.tibetanCode" required />
          </div>

          <div class="form-group">
            <label>方位 *</label>
            <input type="text" v-model="formData.direction" required />
          </div>

          <div class="form-group">
            <label>標題 *</label>
            <input type="text" v-model="formData.title" required />
          </div>

          <div class="form-group">
            <label>內容摘要 *</label>
            <textarea v-model="formData.summary" rows="4" required></textarea>
          </div>

          <div class="form-group">
            <label>家庭生活</label>
            <textarea v-model="formData.familyLife" rows="3"></textarea>
          </div>

          <div class="form-group">
            <label>策略</label>
            <textarea v-model="formData.strategy" rows="3"></textarea>
          </div>

          <div class="form-group">
            <label>財運</label>
            <textarea v-model="formData.fortune" rows="3"></textarea>
          </div>

          <div class="form-group">
            <label>敵人</label>
            <textarea v-model="formData.enemy" rows="3"></textarea>
          </div>

          <div class="form-group">
            <label>旅行</label>
            <textarea v-model="formData.travel" rows="3"></textarea>
          </div>

          <div class="form-group">
            <label>疾病</label>
            <textarea v-model="formData.illness" rows="3"></textarea>
          </div>

          <div class="form-group">
            <label>修行</label>
            <textarea v-model="formData.practice" rows="3"></textarea>
          </div>

          <div class="form-group">
            <label>遺失物</label>
            <textarea v-model="formData.lostItem" rows="3"></textarea>
          </div>

          <div class="form-group">
            <label>訪客</label>
            <textarea v-model="formData.visitor" rows="3"></textarea>
          </div>

          <div class="form-group">
            <label>其他事項</label>
            <textarea v-model="formData.otherMatters" rows="3"></textarea>
          </div>

          <div class="form-group">
            <label>建議</label>
            <textarea v-model="formData.advice" rows="3"></textarea>
          </div>
        </template>

        <!-- 觀音籤詩欄位 -->
        <template v-else-if="type === 'avalo'">
          <div class="form-group">
            <label>藏文代碼 *</label>
            <input type="text" v-model="formData.tibetanCode" required />
          </div>

          <div class="form-group">
            <label>標題 *</label>
            <input type="text" v-model="formData.title" required />
          </div>

          <div class="form-group">
            <label>內容摘要 *</label>
            <textarea v-model="formData.summary" rows="8" required></textarea>
          </div>
        </template>

        <!-- 日本籤詩欄位 -->
        <template v-else-if="type === 'japan'">
          <div class="form-group">
            <label>籤號 *</label>
            <input type="text" v-model="formData.number" required placeholder="例：第一" />
          </div>

          <div class="form-group">
            <label>吉凶 *</label>
            <select v-model="formData.fortuneLevel" required>
              <option value="">請選擇</option>
              <option value="大吉">大吉</option>
              <option value="中吉">中吉</option>
              <option value="小吉">小吉</option>
              <option value="吉">吉</option>
              <option value="末吉">末吉</option>
              <option value="末小吉">末小吉</option>
              <option value="半吉">半吉</option>
              <option value="凶">凶</option>
              <option value="大凶">大凶</option>
            </select>
          </div>

          <div class="form-group">
            <label>籤詩內容 *</label>
            <textarea v-model="formData.content" rows="8" required placeholder="包含籤詩原文及解釋"></textarea>
          </div>

          <div class="form-group">
            <label>願望</label>
            <input type="text" v-model="formData.wish" placeholder="例：會充分地實現吧" />
          </div>

          <div class="form-group">
            <label>疾病</label>
            <input type="text" v-model="formData.illness" placeholder="例：會治好吧" />
          </div>

          <div class="form-group">
            <label>盼望的人</label>
            <input type="text" v-model="formData.waitingPerson" placeholder="例：會出現吧" />
          </div>

          <div class="form-group">
            <label>遺失物</label>
            <input type="text" v-model="formData.lostItem" placeholder="例：會找到吧" />
          </div>

          <div class="form-group">
            <label>生活事件</label>
            <textarea v-model="formData.lifeEvents" rows="3" placeholder="蓋新居、搬家、結婚、旅行等"></textarea>
          </div>
        </template>

        <!-- 按鈕 -->
        <div class="form-actions">
          <button type="button" @click="$emit('back')" class="btn-cancel">取消</button>
          <button type="submit" class="btn-save" :disabled="loading">
            {{ loading ? '儲存中...' : '儲存' }}
          </button>
        </div>

      </form>

    </div>
  </div>
</template>

<style scoped>
.editor-container {
  background-color: #0072E3;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.editor-box {
  background-color: white;
  width: 90vw;
  max-width: 900px;
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

.form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group label {
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
  font-size: 1rem;
}

.form-group input,
.form-group textarea,
.form-group select {
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 1rem;
  font-family: inherit;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group textarea:focus,
.form-group select:focus {
  outline: none;
  border-color: #0066CC;
}

.form-group textarea {
  resize: vertical;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 2px solid #e5e7eb;
}

.btn-cancel,
.btn-save {
  padding: 12px 30px;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
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

@media (max-width: 768px) {
  .editor-box {
    width: 95vw;
    padding: 20px;
  }

  .main-title {
    font-size: 1.8rem;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .btn-cancel,
  .btn-save {
    width: 100%;
  }
}
</style>