<script setup>
import { ref,computed } from 'vue'

const emits = defineEmits(['fontpage', 'showResult'])

const question = ref("")
const loading = ref(false)

// 文殊菩薩圖片陣列
const manjuImages = [
    '/images/manju1.jpg',
    '/images/manju2.jpg'
]

const currentImageIndex = ref(0)

// 切換到下一張圖片
function nextImage() {
  currentImageIndex.value = (currentImageIndex.value + 1) % manjuImages.length
}

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

    const response = await fetch('http://localhost:3000/api/divination/manju', {
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
      <h1 id="title">文殊占卜法</h1>
       </div>
      <div id="main">
      <!-- 圖片 -->
      <div id="firpic" @click="nextImage" title="點擊切換圖片">
        <img :src="manjuImages[currentImageIndex]" 
        alt="文殊菩薩" >
        <div class="image-hint">點擊切換圖片 ({{ currentImageIndex + 1 }}/{{ manjuImages.length }})</div>
      </div>

      <div id="Word">
        <p>
        卜卦前，請紀錄問題<br>
        再虔誠誦念祈禱文：<br>
        皈依文殊師利勇猛智<br>
        三時恆常無遮智慧眼<br>
        祈請文殊勇識垂念我<br>
        三寶三根本及緣起性<br>
        無欺真實之力明取捨<br>
        <br>
        誠念<br>
        <em>文殊智慧咒:嗡阿拉巴渣那迪</em><br>
        念三遍、七遍、108遍
        </p>
        <br>
      </div>
      </div>

      <div id="quest" >
        <textarea id="text" v-model="question" placeholder="紀錄您的問題"></textarea>
       </div>

       <div class="button-container">
      <button id="doit" @click.prevent="doDivina" :disabled="loading">
          {{ loading ? '占卜中...' : '觀想本尊後\n按此卜卦' }}
        </button>
        </div>
        
        <!-- 訪客提示 -->
        <div v-if="!isLoggedIn" class="guest-hint">
          💡 訪客模式：占卜結果不會保存。<router-link to="/login">登入</router-link>後可查看歷史記錄。
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
  border-radius:16px;
  box-shadow: 0 10px 30px rgba(0, 0,0, 0.2);
  border: 1px solid black;
}

#titleRow {
  display: flex;
  align-items: center;
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
  font-size:  20px;
  margin:center ;
  text-align: center;
}

#Word em{
  font-weight: bold;
  color: red;
}

#firpic{
   display:inline-block;
   cursor: pointer;
   position: relative;
   transition: transform 0.2s;
}

#firpic:hover {
  transform: scale(1.02);
}

#firpic:active {
  transform: scale(0.98);
}

#firpic img{
  width:  auto;               
  height: 65vh;               
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  gap:30px ;
  transition: opacity 0.3s;
}

.image-hint {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 14px;
  opacity: 0;
  transition: opacity 0.3s;
  white-space: nowrap;
}

#firpic:hover .image-hint {
  opacity: 1;
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

.button-container {
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
}

.guest-hint a {
  color: #0056b3;
  text-decoration: underline;
  font-weight: bold;
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
  
  #firpic img {
    width: 200px;
  }
}
</style>