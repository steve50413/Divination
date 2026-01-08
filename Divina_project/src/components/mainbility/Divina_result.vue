<script setup>

import { computed, ref, onMounted } from 'vue'

const props = defineProps(['result'])
const emits = defineEmits(['back', 'again'])

// AI 解籤相關
const aiInterpretation = ref('')
const aiLoading = ref(false)
const showAI = ref(false)

// 判斷是文殊還是觀音
const isManju = computed(() => props.result?.type === 'manju')
const isAvalo = computed(() => props.result?.type === 'avalo')

// 檢查是否已有快取
onMounted(() => {
  if (props.result?.id) {
    const cacheKey = `ai_${props.result.id}`
    const cached = localStorage.getItem(cacheKey)
    
    // 只有在快取內容不是錯誤訊息時才使用
    if (cached && !cached.includes('錯誤') && !cached.includes('無法使用') && !cached.includes('失敗')) {
      aiInterpretation.value = cached
      showAI.value = true
    }
  }
})

async function getAIInterpretation() {
  // 防止重複點擊
  if (aiLoading.value || showAI.value) {
    return
  }
  
  // 檢查快取
  const cacheKey = `ai_${props.result.type}_${props.result.historyId}_${props.result.createdAt}`
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
        type: props.result.type
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
      aiInterpretation.value = '抱歉，AI 解籤暫時無法使用。請稍後再試。'
    }
  } catch (error) {
    console.error('AI 解籤錯誤:', error)
    // ❌ 錯誤訊息不存快取
    aiInterpretation.value = '連線失敗，請稍後再試。'
  } finally {
    aiLoading.value = false
  }
}

function formatPoemContent() {
  const poem = props.result.poem
  
  if (!poem) {
    return '無籤詩內容'
  }
  
  let content = ''
  
  if (poem.title) content += `標題：${poem.title}\n`
  if (poem.number) content += `籤號：${poem.number}\n`
  if (poem.tibetanCode) content += `藏文代碼：${poem.tibetanCode}\n`
  if (poem.direction) content += `方向：${poem.direction}\n`
  if (poem.summary) content += `摘要：${poem.summary}\n`
  if (poem.familyLife) content += `家庭和生命：${poem.familyLife}\n`
  if (poem.strategy) content += `謀略與籌劃：${poem.strategy}\n`
  if (poem.fortune) content += `財運：${poem.fortune}\n`
  if (poem.enemy) content += `怨敵：${poem.enemy}\n`
  if (poem.travel) content += `行旅之人：${poem.travel}\n`
  if (poem.illness) content += `疾病：${poem.illness}\n`
  if (poem.practice) content += `修法：${poem.practice}\n`
  if (poem.lostItem) content += `丟失物：${poem.lostItem}\n`
  if (poem.visitor) content += `來客及成事：${poem.visitor}\n`
  if (poem.otherMatters) content += `其他事項：${poem.otherMatters}\n`
  if (poem.advice) content += `建議：${poem.advice}\n`
  
  return content.trim() || '無籤詩內容'
}

</script>

<template>
  <div id="resultContainer" v-if="result">
    <div id="resultBox">
      <!-- 返回按鈕 -->
      <button id="backBtn" @click="$emit('back')">返回</button>

      <!-- 標題 -->
      <h1 class="main-title">{{ isManju ? '文殊占卜' : '觀音占卜' }}結果</h1>
      
      <!-- 用戶問題 -->
      <div class="question-section">
        <h2>您的問題：</h2>
        <p class="question-text">{{ result.question }}</p>
      </div>

      <!-- 籤詩資訊 -->
      <div class="poem-header">
        <h2 v-if="result.poem?.title">{{ result.poem.title }}</h2>
        <p class="poem-code" v-if="result.poem?.tibetanCode">
          藏文代碼：{{ result.poem.tibetanCode }}
        </p>
        <p class="poem-direction" v-if="result.poem?.direction">
          {{ result.poem.direction }}
        </p>
      </div>

      <!-- 籤詩內容 -->
      <div class="poem-content" v-if="result.poem?.summary">
        <h3>籤詩：</h3>
        <div class="poem-text">{{ result.poem.summary }}</div>
      </div>

      <!-- 文殊籤詩的詳細解釋 -->
      <div v-if="isManju && result.poem" class="detailed-sections">
        
        <div class="section" v-if="result.poem.familyLife">
          <h3>家庭和生命</h3>
          <p>{{ result.poem.familyLife }}</p>
        </div>

        <div class="section" v-if="result.poem.strategy">
          <h3>謀略與籌劃</h3>
          <p>{{ result.poem.strategy }}</p>
        </div>

        <div class="section" v-if="result.poem.fortune">
          <h3>財運</h3>
          <p>{{ result.poem.fortune }}</p>
        </div>

        <div class="section" v-if="result.poem.enemy">
          <h3>怨敵</h3>
          <p>{{ result.poem.enemy }}</p>
        </div>

        <div class="section" v-if="result.poem.travel">
          <h3>行旅之人</h3>
          <p>{{ result.poem.travel }}</p>
        </div>

        <div class="section" v-if="result.poem.illness">
          <h3>疾病</h3>
          <p>{{ result.poem.illness }}</p>
        </div>

        <div class="section" v-if="result.poem.practice">
          <h3>修法</h3>
          <p>{{ result.poem.practice }}</p>
        </div>

        <div class="section" v-if="result.poem.lostItem">
          <h3>丟失物</h3>
          <p>{{ result.poem.lostItem }}</p>
        </div>

        <div class="section" v-if="result.poem.visitor">
          <h3>來客及成事</h3>
          <p>{{ result.poem.visitor }}</p>
        </div>

        <div class="section" v-if="result.poem.otherMatters">
          <h3>其他事項</h3>
          <p>{{ result.poem.otherMatters }}</p>
        </div>

        <div class="section advice" v-if="result.poem.advice">
          <h3>建議</h3>
          <p>{{ result.poem.advice }}</p>
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
        <button class="btn-primary" @click="$emit('again', result.type)">
          再占卜一次
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
}

#backBtn:hover {
  background-color: #4b5563;
}

.main-title {
  text-align: center;
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 30px;
  margin-top: 20px;
}

.question-section {
  background-color: #f0f8ff;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 30px;
  border-left: 4px solid #0066CC;
}

.question-section h2 {
  font-size: 1.2rem;
  color: #0066CC;
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

.poem-header h2 {
  font-size: 2rem;
  color: #d32f2f;
  margin-bottom: 10px;
}

.poem-code {
  color: #666;
  font-size: 0.9rem;
  margin: 5px 0;
}

.poem-direction {
  color: #0066CC;
  font-size: 1.1rem;
  font-weight: bold;
  margin-top: 10px;
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
  border-left: 4px solid #0066CC;
}

.section h3 {
  color: #0066CC;
  margin-bottom: 12px;
  font-size: 1.2rem;
}

.section p {
  color: #333;
  line-height: 1.7;
  font-size: 1rem;
}

.section.advice {
  background-color: #fff3cd;
  border-left-color: #ffa500;
}

.section.advice h3 {
  color: #d32f2f;
}

/* AI 結果區 */
.ai-result {
  margin-top: 30px;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
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
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.ai-content h3 {
  color: #667eea;
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.btn-ai:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.4);
}

.btn-ai:disabled {
  background: #9ca3af;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-primary {
  background-color: #0066CC;
  color: white;
}

.btn-primary:hover {
  background-color: #0052A3;
}

@media (max-width: 768px) {
  #resultBox {
    width: 95vw;
    padding: 20px;
  }

  .main-title {
    font-size: 1.8rem;
  }

  .poem-header h2 {
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