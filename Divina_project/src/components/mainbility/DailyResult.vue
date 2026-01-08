<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps(['result'])
const emits = defineEmits(['back', 'again'])

// AI 解籤相關
const aiInterpretation = ref('')
const aiLoading = ref(false)
const showAI = ref(false)

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

// 檢查是否已有快取
onMounted(() => {
  if (props.result?.id) {
    const cacheKey = `ai_daily_${props.result.id}_${props.result.createdAt}`
    const cached = localStorage.getItem(cacheKey)
    
    // 只有在快取內容不是錯誤訊息時才使用
    if (cached && !cached.includes('錯誤') && !cached.includes('無法使用') && !cached.includes('失敗')) {
      aiInterpretation.value = cached
      showAI.value = true
    }
  }
})

// AI 解籤
async function getAIInterpretation() {
  // 防止重複點擊
  if (aiLoading.value || showAI.value) {
    return
  }
  
  // 檢查快取
  const cacheKey = `ai_daily_${props.result.id}`
  const cached = localStorage.getItem(cacheKey)
  
  // 只有在快取內容不是錯誤訊息時才使用
  if (cached && !cached.includes('錯誤') && !cached.includes('無法使用') && !cached.includes('失敗')) {
    aiInterpretation.value = cached
    showAI.value = true
    return
  }
  
  aiLoading.value = true
  showAI.value = true
  
  try {
    const poemContent = formatPoemContent()
    
    const response = await fetch('http://localhost:3000/api/ai/interpret', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        question: props.result.question,
        poemContent: poemContent,
        type: 'japan'
      })
    })
    
    const data = await response.json()
    
    if (data.success) {
      const interpretation = data.data.interpretation
      aiInterpretation.value = interpretation
      
      // ✅ 只在成功時才存入快取
      localStorage.setItem(cacheKey, interpretation)
    } else {
      // ❌ 錯誤訊息不存快取
      aiInterpretation.value = '抱歉，AI 解籤暫時無法使用。'
    }
  } catch (error) {
    console.error('❌ AI 解籤錯誤:', error)
    // ❌ 錯誤訊息不存快取
    aiInterpretation.value = '連線失敗，請稍後再試。'
  } finally {
    aiLoading.value = false
  }
}

// 格式化日本籤詩內容
function formatPoemContent() {
  const poem = props.result.poem
  
  if (!poem) {
    return '無籤詩內容'
  }
  
  let content = ''
  
  if (poem.number) content += `籤號：${poem.number}\n`
  if (poem.fortuneLevel) content += `吉凶：${poem.fortuneLevel}\n`
  if (poem.content) content += `籤詩：${poem.content}\n`
  if (poem.wish) content += `願望：${poem.wish}\n`
  if (poem.illness) content += `疾病：${poem.illness}\n`
  if (poem.waitingPerson) content += `等待之人：${poem.waitingPerson}\n`
  if (poem.lostItem) content += `遺失物：${poem.lostItem}\n`
  if (poem.lifeEvents) content += `生活事件：${poem.lifeEvents}\n`
  
  return content.trim() || '無籤詩內容'
}

</script>

<template>
  <div id="resultContainer" v-if="result">
    <div id="resultBox">
      
      <!-- 返回按鈕 -->
      <button id="backBtn" @click="$emit('back')">返回</button>

      <!-- 標題 -->
      <h1 class="main-title">🎋 每日一籤結果</h1>
      
      <!-- 用戶問題 -->
      <div class="question-section">
        <h2>您的問題：</h2>
        <p class="question-text">{{ result.question }}</p>
      </div>

      <!-- 籤詩資訊 -->
      <div class="poem-header">
        <h2 class="poem-number">{{ result.poem.number }}</h2>
        <div 
          class="fortune-badge" 
          :style="{ backgroundColor: getFortuneLevelColor(result.poem.fortuneLevel) }"
        >
          {{ result.poem.fortuneLevel }}
        </div>
      </div>

      <!-- 籤詩內容 -->
      <div class="poem-content">
        <h3>籤詩：</h3>
        <div class="poem-text">{{ result.poem.content }}</div>
      </div>

      <!-- 詳細解釋 -->
      <div class="detailed-sections">
        
        <div class="section" v-if="result.poem.wish">
          <h3>💫 願望</h3>
          <p>{{ result.poem.wish }}</p>
        </div>

        <div class="section" v-if="result.poem.illness">
          <h3>🏥 疾病</h3>
          <p>{{ result.poem.illness }}</p>
        </div>

        <div class="section" v-if="result.poem.waitingPerson">
          <h3>👤 盼望的人</h3>
          <p>{{ result.poem.waitingPerson }}</p>
        </div>

        <div class="section" v-if="result.poem.lostItem">
          <h3>🔍 遺失物</h3>
          <p>{{ result.poem.lostItem }}</p>
        </div>

        <div class="section life-events" v-if="result.poem.lifeEvents">
          <h3>🏠 生活事件</h3>
          <p>{{ result.poem.lifeEvents }}</p>
        </div>

      </div>

      <!-- AI 解籤結果 -->
      <div v-if="showAI" class="ai-result">
        <div v-if="aiLoading" class="ai-loading">
          <div class="loading-spinner"></div>
          <p>AI 正在為您解讀籤詩...</p>
        </div>
        
        <div v-else class="ai-content">
          <h3>🤖 AI 解讀</h3>
          <div class="ai-text">{{ aiInterpretation }}</div>
          <div class="ai-disclaimer">
            <small>
              ⚠️ AI 解籤僅供參考，不代表絕對準確。<br>
              建議結合自身情況和智慧思考，或尋求專業法師指導。
            </small>
          </div>
        </div>
      </div>

      <!-- 占卜時間 -->
      <div class="timestamp">
        <p>占卜時間：{{ new Date(result.createdAt).toLocaleString('zh-TW') }}</p>
      </div>

      <!-- 按鈕組 -->
      <div class="action-buttons">
        <button class="btn-secondary" @click="$emit('back')">返回主選單</button>
        <button 
          class="btn-ai" 
          @click="getAIInterpretation"
          :disabled="aiLoading || showAI">
          <span v-if="showAI && !aiLoading">✅ 已解籤</span>
          <span v-else-if="aiLoading">🔄 思考中...</span>
          <span v-else>🤖 AI 解籤</span>
        </button>
        <button class="btn-primary" @click="$emit('again')">
          再抽一次
        </button>
      </div>
    </div>
  </div>

  <div v-else id="resultContainer">
    <div id="resultBox">
      <p>載入中...</p>
    </div>
  </div>
</template>

<style scoped>
#resultContainer {
  background-color: #0072E3;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

#resultBox {
  background-color: white;
  width: 90vw;
  max-width: 900px;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  position: relative;
}

#backBtn {
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

#backBtn:hover {
  background-color: #4b5563;
}

.main-title {
  text-align: center;
  font-size: 2.5rem;
  color: #d32f2f;
  margin-bottom: 30px;
  margin-top: 20px;
}

.question-section {
  background-color: #fff8f0;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 30px;
  border-left: 4px solid #d32f2f;
}

.question-section h2 {
  font-size: 1.2rem;
  color: #d32f2f;
  margin-bottom: 10px;
}

.question-text {
  font-size: 1.1rem;
  color: #333;
  line-height: 1.6;
}

.poem-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #eee;
}

.poem-number {
  font-size: 2rem;
  color: #d32f2f;
  margin-bottom: 15px;
}

.fortune-badge {
  display: inline-block;
  color: white;
  padding: 10px 30px;
  border-radius: 25px;
  font-size: 1.3rem;
  font-weight: bold;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.poem-content {
  background-color: #fffef0;
  padding: 25px;
  border-radius: 12px;
  margin-bottom: 30px;
  border: 2px solid #ffd700;
}

.poem-content h3 {
  color: #d32f2f;
  margin-bottom: 15px;
  font-size: 1.3rem;
}

.poem-text {
  font-size: 1.1rem;
  line-height: 1.8;
  color: #333;
  white-space: pre-line;
}

.detailed-sections {
  margin-top: 30px;
}

.section {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  border-left: 4px solid #d32f2f;
}

.section h3 {
  color: #d32f2f;
  margin-bottom: 12px;
  font-size: 1.2rem;
}

.section p {
  color: #333;
  line-height: 1.7;
  font-size: 1rem;
}

.section.life-events {
  background-color: #fff3cd;
  border-left-color: #ffc107;
}

.section.life-events h3 {
  color: #f57c00;
}

/* AI 結果區 */
.ai-result {
  margin-top: 30px;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #fff8f0 0%, #ffe0b2 100%);
  border-radius: 15px;
  padding: 25px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.ai-loading {
  text-align: center;
  padding: 40px 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  margin: 0 auto 20px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #d32f2f;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.ai-content h3 {
  color: #d32f2f;
  margin-bottom: 20px;
  font-size: 1.5rem;
  display: flex;
  align-items: center;
  gap: 10px;
}

.ai-text {
  background: white;
  padding: 20px;
  border-radius: 10px;
  line-height: 1.8;
  color: #333;
  font-size: 1rem;
  white-space: pre-line;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.05);
}

.ai-disclaimer {
  margin-top: 15px;
  padding: 15px;
  background: rgba(255, 193, 7, 0.1);
  border-left: 4px solid #ffc107;
  border-radius: 8px;
}

.ai-disclaimer small {
  color: #856404;
  line-height: 1.6;
}

.timestamp {
  text-align: center;
  color: #666;
  font-size: 0.9rem;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 15px;
  margin-top: 30px;
  flex-wrap: wrap;
}

.btn-secondary, .btn-ai, .btn-primary {
  padding: 12px 30px;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  cursor: pointer;
  font-weight: bold;
  transition: all 0.3s;
}

.btn-secondary {
  background-color: #6b7280;
  color: white;
}

.btn-secondary:hover {
  background-color: #4b5563;
}

.btn-ai {
  background: linear-gradient(135deg, #d32f2f 0%, #b71c1c 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(211, 47, 47, 0.3);
}

.btn-ai:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(211, 47, 47, 0.4);
}

.btn-ai:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-primary {
  background: linear-gradient(135deg, #d32f2f, #b71c1c);
  color: white;
  box-shadow: 0 4px 12px rgba(211, 47, 47, 0.3);
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(211, 47, 47, 0.4);
}

@media (max-width: 768px) {
  #resultBox {
    width: 95vw;
    padding: 20px;
  }

  .main-title {
    font-size: 1.8rem;
  }

  .poem-number {
    font-size: 1.5rem;
  }

  .action-buttons {
    flex-direction: column;
  }

  .btn-primary,
  .btn-secondary,
  .btn-ai {
    width: 100%;
  }
}
</style>