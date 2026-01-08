<script setup>
import { ref, onMounted, watch } from 'vue';
import { CONST as C } from './lib/bility'
import Login from './components/user/Login.vue';
import SignUp from './components/user/SignUp.vue';
import FontPage from './components/mainbility/FontPage.vue';
import Divina_manju from './components/mainbility/Divina_manju.vue';
import Divina_avalo from './components/mainbility/Divina_avalo.vue';
import Divina_result from './components/mainbility/Divina_result.vue';
import Divina_history from './components/mainbility/Divina_history.vue';
import Profile from './components/mainbility/Profile.vue';
import Encyclopedia from './components/mainbility/Encyclopedia.vue';
import NoteEditor from './components/mainbility/NoteEditor.vue';
import Divina_japan from './components/mainbility/Divina_japan.vue';
import DailyResult from './components/mainbility/DailyResult.vue';
import AdminPanel from './components/mainbility/AdminPanel.vue'
import PoemManager from './components/mainbility/PoemManager.vue'
import PoemEditor from './components/mainbility/PoemEditor.vue'
import Statistics from './components/mainbility/Statistics.vue'
import ForgotPassword from './components/mainbility/ForgetPassword.vue'
import Toast from './components/mainbility/Toast.vue'  // ← 加這行
import { useToast } from './lib/useToast'   // ← 加這行
import ArticleList from './components/mainbility/ArticleList.vue'
import ArticleDetail from './components/mainbility/ArticleDetail.vue'
import ArticleForm from './components/mainbility/ArticleForm.vue'


const status = ref(C.STATUS_LOGIN)

const currentArticleId = ref(null)
const editingArticle = ref(null)
const divinationResult = ref(null)
const previousStatus = ref(null)
const editingNoteId = ref(null)
const dailyResult = ref(null)
const editingPoem = ref(null)  // { type, poem }
const currentHistoryId = ref(null)  // ← 加這個
const { toastState } = useToast()  // ← 加這行
const noteData = ref({ note: '', isAccurate: null }) 

onMounted(() => {
  const token = localStorage.getItem('token')
  const savedStatus = localStorage.getItem('currentStatus')
  
  if (token && savedStatus) {
    status.value = savedStatus
  }
  
      window.addEventListener('beforeunload', () => {
      if (status.value == C.STATUS_RESULT || status.value == C.STATUS_DAILY_RESULT) {
        localStorage.setItem('currentStatus', C.STATUS_FONTPAGE)
      }
    })
})

watch(status, (newStatus) => {
  localStorage.setItem('currentStatus', newStatus)
})

const logout = () => {
  localStorage.removeItem('currentStatus')
  localStorage.removeItem('token')
  localStorage.removeItem('userId')
  localStorage.removeItem('nickname')
  localStorage.removeItem('email')
  status.value = C.STATUS_LOGIN
}

const gotoLogin = () => status.value = C.STATUS_LOGIN
const gotoSignup = () => status.value = C.STATUS_SIGNUP
const gotoDivina_Manju = () => status.value = C.STATUS_DIVINA_MANJU
const gotoDivina_Avalo = () => status.value = C.STATUS_DIVINA_AVALO
const gotoFontPage = () => status.value = C.STATUS_FONTPAGE
const gotoHistory = () => status.value = C.STATUS_HISTORY
const gotoDaily = () => status.value = C.STATUS_DAILY
const gotoProfile = () => status.value = C.STATUS_PROFILE
const gotoEncyclopedia = () =>status.value = C.STATUS_ENCYCLOPEDIA
const gotoAdminPanel = () => status.value = C.STATUS_ADMIN_PANEL
const gotoPoemManager = () => status.value = C.STATUS_POEM_MANAGER
const gotoStatistics = () => status.value = C.STATUS_STATISTICS
const gotoForgotPassword = () => status.value = C.STATUS_FORGOT_PASSWORD
const gotoArticleList = () => status.value = C.STATUS_ARTICLE_LIST
const gotoArticleDetail = (id) => {
  console.log('接收到的 article ID:', id);
  
  if (!id) {
    console.error('❌ 文章 ID 是 null！');
    alert('無法載入文章');
    return;  // 不跳轉
  }
  
  currentArticleId.value = id;
  status.value = C.STATUS_ARTICLE_DETAIL;
}
const gotoArticleCreate = () => status.value = C.STATUS_ARTICLE_CREATE
const gotoArticleEdit = (article) => {
  editingArticle.value = article
  status.value = C.STATUS_ARTICLE_EDIT
}
const gotoArticleManager = () => status.value = C.STATUS_ARTICLE_LIST


const getUserInfo = () => {
  const userId = localStorage.getItem('userId')
  const nickname = localStorage.getItem('nickname')
  const role = localStorage.getItem('userRole')
  
  if (!userId) return null
  
  return {
    id: parseInt(userId),
    nickname: nickname,
    role: role
  }
}
//查詢籤詩百顆
const viewPoemFromEncyclopedia = (result) => {
  if (result.type === 'japan') {
    // 日本籤詩用 DailyResult
    dailyResult.value = result
    previousStatus.value = C.STATUS_ENCYCLOPEDIA
    status.value = C.STATUS_DAILY_RESULT
  } else {
    // 文殊/觀音用 DivinationResult
    divinationResult.value = result
    previousStatus.value = C.STATUS_ENCYCLOPEDIA
    status.value = C.STATUS_RESULT
  }
}

// 顯示占卜結果（從占卜頁面來的）
const showResult = (result) => {
  console.log("收到占卜結果:", result)
  divinationResult.value = result
  previousStatus.value = C.STATUS_FONTPAGE
  status.value = C.STATUS_RESULT
}

// 從歷史記錄查看結果
const showResultFromHistory = async (id) => {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch(`http://localhost:3000/api/divination/history/${id}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    const data = await response.json()
    
    if (data.success && data.data) {
      // 確保資料結構正確
     divinationResult.value = data.data  // ← 不是 result.value = result
      previousStatus.value = C.STATUS_HISTORY
      status.value = C.STATUS_RESULT
    } else {
      alert('載入失敗')
    }
  } catch (err) {
    console.error('載入錯誤:', err)
    alert('連線失敗')
  }
}
// 從歷史記錄查看每日一籤結果
const showDailyResultFromHistory = async (id) => {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch(`http://localhost:3000/api/daily/history/${id}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    const data = await response.json()
    
    if (data.success) {
      dailyResult.value = data.data
      previousStatus.value = C.STATUS_HISTORY
      status.value = C.STATUS_DAILY_RESULT
    }
  } catch (err) {
    console.error('載入錯誤:', err)
    alert('載入失敗')
  }
}

// 從結果頁返回
const backFromResult = () => {
  divinationResult.value = null
  
  // 根據來源返回到不同頁面
  if (previousStatus.value === C.STATUS_HISTORY) {
    status.value = C.STATUS_HISTORY  // 返回歷史記錄
  } else if (previousStatus.value === C.STATUS_ENCYCLOPEDIA) {
    status.value = C.STATUS_ENCYCLOPEDIA  // 返回籤詩百科
  } else {
    status.value = C.STATUS_FONTPAGE  // 返回首頁
  }
  
  previousStatus.value = null
}


// ✅ 新增：再占卜一次
const divinationAgain = (type) => {
  divinationResult.value = null
  
  // 根據類型跳到對應的占卜頁面
  if (type === 'manju') {
    status.value = C.STATUS_DIVINA_MANJU
  } else if (type === 'avalo') {
    status.value = C.STATUS_DIVINA_AVALO
  }
  
  previousStatus.value = null
}



const editNote = async (historyId) => {
  currentHistoryId.value = historyId
  
  // 載入現有的筆記（如果有）
  const token = localStorage.getItem('token')
  
  try {
    const response = await fetch(`http://localhost:3000/api/divination/history/${historyId}`, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    const data = await response.json()
    
    if (data.success && data.data) {
      noteData.value = {
        note: data.data.note || '',
        isAccurate: data.data.isAccurate
      }
    }
    
    previousStatus.value = status.value
    status.value = C.STATUS_NOTE_EDITOR
  } catch (err) {
    console.error('載入錯誤:', err)
    previousStatus.value = status.value
    status.value = C.STATUS_NOTE_EDITOR
  }
}

const backFromNoteEditor = () => {
  currentHistoryId.value = null
  noteData.value = { note: '', isAccurate: null }
  status.value =  C.STATUS_HISTORY
}

const noteSaved = async () => {
  currentHistoryId.value = null
  noteData.value = { note: '', isAccurate: null }
  status.value = previousStatus.value || C.STATUS_HISTORY
}

const showDailyResult = (result) => {
  dailyResult.value = result
  previousStatus.value = C.STATUS_DAILY
  status.value = C.STATUS_DAILY_RESULT
}

const backFromDailyResult = () => {
  dailyResult.value = null
  if (previousStatus.value === C.STATUS_DAILY) {
    status.value = C.STATUS_DAILY
  } else {
    status.value = C.STATUS_FONTPAGE
  }
  previousStatus.value = null
}

const dailyAgain = () => {
  dailyResult.value = null
  status.value = C.STATUS_DAILY
}

const editPoem = (data) => {
  editingPoem.value = data
  status.value = C.STATUS_POEM_EDITOR
}

const backToAdminPanel = () => {
  status.value = C.STATUS_ADMIN_PANEL
}

const backToPoemManager = () => {
  editingPoem.value = null
  status.value = C.STATUS_POEM_MANAGER
}

const poemSaved = () => {
  editingPoem.value = null
  status.value = C.STATUS_POEM_MANAGER
}



</script>

<template>
 <Toast 
    :show="toastState.show"
    :message="toastState.message"
    :type="toastState.type"
    :duration="toastState.duration"
    @close="toastState.show = false"
  />

<header>
</header>

  <main class="main">
    
    <section id="userSection"> 

      <Login 
        @fontpage="gotoFontPage" 
        @signup="gotoSignup"
        @forgotPassword="gotoForgotPassword"
        v-if="status==C.STATUS_LOGIN" 
      />
      <SignUp @back="gotoLogin" @success="gotoFontPage" v-if="status==C.STATUS_SIGNUP"/>
      <FontPage 
        @divina_manju="gotoDivina_Manju" 
        @divina_avalo="gotoDivina_Avalo" 
        @logout="logout"
        @showHistory="gotoHistory"
        @showDaily="gotoDaily" 
        @showProfile="gotoProfile"
        @showEncyclopedia="gotoEncyclopedia"
        @showAdmin="gotoAdminPanel"
        @showArticles="gotoArticleList"   
        v-if="status==C.STATUS_FONTPAGE" 
      />

      <ForgotPassword 
        @back="gotoLogin" 
        @success="gotoLogin"
        v-if="status==C.STATUS_FORGOT_PASSWORD" 
      />

            <!-- 文章列表 -->
      <ArticleList 
        :userInfo="getUserInfo()"
        @view-article="gotoArticleDetail"
        @create-article="gotoArticleCreate"
        @back-to-home="gotoFontPage"
        v-if="status==C.STATUS_ARTICLE_LIST"
      />

      <!-- 文章詳情 -->
      <ArticleDetail 
        :articleId="currentArticleId"
        :userInfo="getUserInfo()"
        @back-to-list="gotoArticleList"
        @edit-article="gotoArticleEdit"
        v-if="status==C.STATUS_ARTICLE_DETAIL"
      />

      <!-- 發布文章 -->
      <ArticleForm 
        @cancel="gotoArticleList"
        @submit-success="gotoArticleList"
        v-if="status==C.STATUS_ARTICLE_CREATE"
      />

      <!-- 編輯文章 -->
      <ArticleForm 
        :editArticle="editingArticle"
        @cancel="gotoArticleList"
        @submit-success="gotoArticleList"
        v-if="status==C.STATUS_ARTICLE_EDIT"
      />  

      <Divina_manju @fontpage="gotoFontPage" @showResult="showResult" v-if="status==C.STATUS_DIVINA_MANJU"/>
      <Divina_avalo @fontpage="gotoFontPage" @showResult="showResult" v-if="status==C.STATUS_DIVINA_AVALO"/>
     <Divina_result 
        :result="divinationResult" 
        @back="backFromResult" 
        @again="divinationAgain"
        v-if="status==C.STATUS_RESULT" 
      />
      <Divina_history 
        @back="gotoFontPage" 
        @showResult="showResultFromHistory"
        @showDailyResult="showDailyResultFromHistory"
        @editNote="editNote"
        v-if="status==C.STATUS_HISTORY" 
      />
      <Profile @back="gotoFontPage" v-if="status==C.STATUS_PROFILE" />
      <Divina_japan @fontpage="gotoFontPage" @showResult="showDailyResult" v-if="status==C.STATUS_DAILY"/>
      <DailyResult :result="dailyResult" @back="backFromDailyResult" @again="dailyAgain" v-if="status==C.STATUS_DAILY_RESULT" />
      <Encyclopedia @back="gotoFontPage" @viewPoem="viewPoemFromEncyclopedia" v-if="status==C.STATUS_ENCYCLOPEDIA" />
      <AdminPanel 
        @back="gotoFontPage" 
        @gotoPoems="gotoPoemManager"
        @gotoStats="gotoStatistics"
        @gotoArticles="gotoArticleManager"
        v-if="status==C.STATUS_ADMIN_PANEL" 
      />
      <Statistics 
          @back="backToAdminPanel"
          v-if="status==C.STATUS_STATISTICS" 
        />

      <PoemManager 
        @back="backToAdminPanel" 
        @editPoem="editPoem"
        v-if="status==C.STATUS_POEM_MANAGER" 
      />

      <PoemEditor 
        v-if="status==C.STATUS_POEM_EDITOR"
        :type="editingPoem?.type" 
        :poem="editingPoem?.poem"
        @back="backToPoemManager"
        @saved="poemSaved"
      />


        <NoteEditor 
          v-if="status==C.STATUS_NOTE_EDITOR"
          :historyId="currentHistoryId"
          @close="backFromNoteEditor"
          @saved="noteSaved"
        />
    </section>
  </main>
</template>

<style>
#userSection {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.main{
  background-color: #0072E3;
}
</style>