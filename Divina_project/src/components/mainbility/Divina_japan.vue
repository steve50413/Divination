<script setup>
import { ref , computed } from 'vue'

const emits = defineEmits(['fontpage', 'showResult'])

const question = ref("")
const loading = ref(false)

const isLoggedIn = computed(() => {
  if (typeof window !== 'undefined') {
    return !!localStorage.getItem('token')
  }
  return false
})

async function doDivina(){
  if(!question.value.trim()){
    alert("請輸入您的問題")
    return
  }

  // 取得 token（可能為空，訪客模式）

// ✅ 正確
const token = (typeof window !== 'undefined' && localStorage.getItem('token')) || ''
  loading.value = true

  try {
    // 準備 headers
    const headers = {
      'Content-Type': 'application/json'
    }
    
    // 有 token 才帶上（會員模式）
    if (token) {
      headers['Authorization'] = `Bearer ${token}`
    }

    const response = await fetch('http://localhost:3000/api/daily/draw', {
      method: 'POST',
      headers,
      body: JSON.stringify({
        question: question.value
      })
    })

    const data = await response.json()

    if(data.success) {
      emits('showResult', data.data)
      
      // 如果是訪客（沒有 id），提示可以登入保存記錄
      if (!data.data.id && !token) {
        console.log('訪客模式：結果不會保存')
      }
    } else {
      alert(data.message || "占卜失敗")
    }
  } catch (error) {
    console.error('占卜錯誤:', error)
    alert("連線失敗，請確認後端是否啟動")
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div id="container">
    <div id="bocker">
      
      <div id="titleRow">
        <button id="backup" @click.prevent="$emit('fontpage')">返回</button> 
        <h1 id="title">每日一籤</h1>
      </div>

      <div id="main">
        
        <!-- 圖片 -->
        <div id="firpic">
          <img src="https://res-1.cloudinary.com/jnto/image/upload/w_2064,h_1300,c_fill,f_auto,fl_lossy,q_auto/v1675339920/tokyo/Tokyo_s_id19_18" 
            alt="日本神社" >
        </div>

        <div id="Word">
          <p>
            每日一籤源自日本神社的御神籤傳統<br><br>
            
            在抽籤前，請先：<br>
            1. 靜心思考您的問題<br>
            2. 雙手合十，虔誠祈禱<br>
            3. 在心中默念您的願望<br><br>
            
            籤詩會為您指引方向<br>
            帶來今日的啟示與建議<br><br>
            
            <strong style="color: #d32f2f;">
              ※ 每次抽籤都會儲存記錄<br>
              可隨時抽取，不限次數
            </strong>
          </p>
        </div>
      </div>

      <!-- 問題輸入區 -->
      <div id="quest">
        <textarea 
          id="text" 
          v-model="question" 
          placeholder="請輸入您想詢問的問題..."
        ></textarea>
      </div>

      <!-- 抽籤按鈕 -->
      <div class="button-container">
        <button id="doit" @click.prevent="doDivina" :disabled="loading">
          {{ loading ? '抽籤中...' : '🎋 抽籤' }}
        </button>
      </div>

      <!-- 訪客提示 -->
      <div v-if="!isLoggedIn" class="guest-hint">
        💡 訪客模式：占卜結果不會保存，登入後可查看歷史記錄
      </div>

    </div>
  </div>
</template>

<style scoped>
#container{
  background-color: #0072E3;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

#bocker{
  background-color: white;
  min-height: 50vh;
  width: 90vw;
  max-width: 900px;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  border: 1px solid #e5e7eb;
}

#titleRow {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  position: relative;
}

#backup {
  font-size: 16px;
  font-weight: bold;
  color: white;
  background: linear-gradient(135deg, #d32f2f, #b71c1c);
  border: none;
  border-radius: 8px;
  padding: 10px 20px;
  cursor: pointer;
  transition: all 0.3s;
}

#backup:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(211, 47, 47, 0.3);
}

#title {
  font-size: 2.5rem;
  text-align: center;
  margin: 0;
  color: #d32f2f;
  font-weight: bold;
  flex: 1;
}

#main {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 25px;
  margin-bottom: 30px;
}

#firpic {
  width: 100%;
  text-align: center;
}

#firpic img {
  width: 100%;
  height: auto;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

#Word {
  width: 100%;
  font-size: 18px;
  text-align: center;
  padding: 0 20px;
}

#Word p {
  line-height: 1.8;
  color: #333;
}

#quest {
  margin-bottom: 20px;
}

#text {
  border: 2px solid #e5e7eb;
  width: 100%;
  min-height: 120px;
  border-radius: 8px;
  padding: 15px;
  font-size: 16px;
  font-family: inherit;
  resize: vertical;
  transition: border-color 0.3s;
}

#text:focus {
  outline: none;
  border-color: #d32f2f;
}

.button-container {
  display: flex;
  justify-content: center;
}

#doit {
  font-size: 20px;
  font-weight: bold;
  color: white;
  background: linear-gradient(135deg, #d32f2f, #b71c1c);
  border: none;
  border-radius: 12px;
  padding: 15px 50px;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(211, 47, 47, 0.3);
}

#doit:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(211, 47, 47, 0.4);
}

#doit:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.guest-hint {
  text-align: center;
  margin-top: 20px;
  padding: 10px;
  background-color: #fff3cd;
  border: 1px solid #ffc107;
  border-radius: 8px;
  color: #856404;
  font-size: 14px;
}

@media (max-width: 768px) {
  #bocker {
    width: 95vw;
    padding: 20px;
  }

  #title {
    font-size: 1.8rem;
  }

  #main {
    flex-direction: column;
    align-items: center;
  }

  #firpic img {
    width: 100%;
    height: auto;
    max-height: 300px;
  }

  #Word {
    font-size: 16px;
  }
}
</style>