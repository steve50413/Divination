<script setup>
import { ref,computed } from 'vue'

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

    const response = await fetch('http://localhost:3000/api/divination/avalo', {
      method: 'POST',
      headers,
      body: JSON.stringify({
        question: question.value
      })
    })

    const data = await response.json()

    if(data.success) {
      // 占卜成功，跳轉到結果頁面
      emits('showResult', data.data)
      
      // 如果是訪客（沒有 historyId），提示可以登入保存記錄
      if (!data.data.historyId && !token) {
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
      <!-- 占卜標題 -->
      <div id="titleRow">
       <button id="backup" @click.prevent="$emit('fontpage')">返回</button> 
      <h1 id="title">觀音占卜法</h1>
       </div>
      <div id="main">
      <!-- 圖片 -->
      <div id="firpic">
        <img src="https://i.pinimg.com/736x/06/fc/8b/06fc8b906f764b7c986bd9b37f028fed.jpg" 
        alt="觀音菩薩" >
      </div>

      <div id="Word">
        <p>
        卜卦前，請紀錄問題， 再虔誠誦念祈禱文：<br>
        <br>
        自身觀為大悲觀世音，身白一面二臂持明鏡，<br>
        前方四部護法白度母，精確預測吉凶眾尊主，<br>
        祈為判斷見證而明示。<br>

        三寶尊及三根本，勝尊大悲觀世音，<br>
        白度母及護法眾，具神通之當方神，<br>
        現今此卦說分明，精確預示凶吉尊，<br>
        準確判斷善宣示，願得明確之卦象。<br>
        <br>
        誠念<br>
        <em>嗡、嘛、呢、叭、咪、吽</em><br>
        念三遍、七遍、108遍
        </p>
        <br>
      </div>
      </div>

      <div id="quest" >
        <textarea id="text" v-model="question" placeholder="紀錄您的問題"></textarea>
       </div>

       <div class="DObutton">
      <button id="doit" @click.prevent="doDivina" :disabled="loading">
          {{ loading ? '占卜中...' : '觀想本尊後\n按此卜卦' }}
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
  max-width: 120vw;
  width: 55vw;
  padding: 40px;
  border-radius:16px;
  box-shadow: 0 10px 30px rgba(0, 0,0, 0.2);
  border: 1px solid black;
}

#titleRow {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  position: relative;
}

#backup {
  font-size: 18px;
  font-weight: bold;
  color: #FFD306;
  background-color: blue;
  border: 1px solid black;
  border-radius: 20px;
  padding: 10px;
  cursor: pointer;
  white-space: nowrap;
}

#title {
  font-size: 3rem;
  text-align: center;
  margin: 0;
  color: #333;
  font-weight: bold;
  flex: 1;
}

#main{
  display: flex;
  gap: 30px;
}

#Word{
 flex: 1;
  font-size: 20px;
  margin:center ;
  text-align: center;
}

#Word em{
  font-weight: bold;
  color: red;
}

#firpic{
   display:inline-block;             
}

#firpic img{
  width:  auto;
  height: 65vh;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  gap:30px ;
}

#text{
 border: 1px solid black;
   width: 100%;
  min-height: 120px;
  border-radius: 8px;
  margin-top: 20px;
  padding: 10px;
  font-size: 16px;
}

#bocker button{
  background-color: blue;
}

#doit{
    font-size: 20px;
    font-weight: bold;
    color: #FFD306;
    border: 1px solid black;
    border-radius: 20px ;
    padding: 10px;
    white-space: pre-line;
}

#doit:disabled{
    background-color: #ccc;
    cursor: not-allowed;
}

.DObutton{
  display: flex;
  justify-content: center;
  margin-top: 20px;
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

@media (max-width:768px) {
  #bocker{
    width: 95vw;
    padding: 20px;
  }

  #title{
    font-size: 1.5rem;
  }

  #main {
    flex-direction: column;
    align-items: center;
  }
  
  #first img {
    width: 200px;
  }
}
</style>