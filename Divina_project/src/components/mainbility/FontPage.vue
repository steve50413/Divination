<script setup>
import { ref } from 'vue'

const emits = defineEmits(['divina_manju','divina_avalo','logout','showHistory','showDaily','showProfile','showEncyclopedia','showAdmin','showArticles'])

const userRole = ref(localStorage.getItem('userRole'))
const isLoggedIn = ref(!!localStorage.getItem('token'))
</script>

<template>
    <div id="fontPageContainer">
      
      <!-- 導航列 -->
      <nav id="navbar">
        <div class="nav-left">
          <h2>占卜系統</h2>
        </div>
        <div class="nav-right">
          <button @click="$emit('showArticles')" class="nav-btn">📝 文章廣場</button>
          <button @click.prevent="$emit('showEncyclopedia')" class="nav-btn">📚 籤詩百科</button>
          <button @click.prevent="$emit('showHistory')" class="nav-btn">📜 歷史記錄</button>
          <button @click.prevent="$emit('showDaily')" class="nav-btn">🎋 每日一籤</button>
          <button @click.prevent="$emit('showProfile')" class="nav-btn">👤 個人資料</button>
          
          <!-- 登出按鈕：永遠顯示（免登入也可以點，回到登入頁） -->
          <button @click.prevent="$emit('logout')" class="nav-btn logout-btn">登出</button>
          
          <!-- 管理後台：只在已登入且是 admin 時顯示 -->
          <button 
            v-if="isLoggedIn && userRole === 'admin'" 
            @click.prevent="$emit('showAdmin')" 
            class="nav-btn admin-btn"
          >
            ⚙️ 管理後台
          </button>
        </div>
      </nav>

      <!-- 原本的占卜選擇區 -->
      <div class="main-content">
        <h1>選擇占卜方式</h1>
        
        <div id="DivPic">
          <a href="" @click.prevent="$emit('divina_manju')" class="divination-card">
            <img src="../../../public/images/manju1.jpg" alt="文殊菩薩">
            <span>文殊占卜法</span>
          </a>
          
          <a href="" @click.prevent="$emit('divina_avalo')" class="divination-card">
            <img src="https://i.pinimg.com/736x/06/fc/8b/06fc8b906f764b7c986bd9b37f028fed.jpg" alt="觀音菩薩">
            <span>觀音占卜法</span>
          </a>
        </div>
      </div>
    </div>
</template>

<style scoped>
#fontPageContainer {
  background: white;
  min-height: 100vh;
  width: 100%;
  display: flex;
  flex-direction: column;
}

#navbar {
  background: linear-gradient(135deg, #0072E3 0%, #0052A3 100%);
  padding: 12px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.15);
}

.nav-left h2 {
  color: white;
  margin: 0;
  font-size: 1.3rem;
  font-weight: 600;
}

.nav-right {
  display: flex;
  gap: 12px;
  align-items: center;
}

.nav-btn {
  background: rgba(255,255,255,0.15);
  color: white;
  border: 1px solid rgba(255,255,255,0.3);
  padding: 8px 18px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.nav-btn:hover {
  background: rgba(255,255,255,0.25);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
}

.logout-btn {
  background: rgba(220, 38, 38, 0.85);
  border-color: rgba(220, 38, 38, 0.9);
}

.logout-btn:hover {
  background: rgba(185, 28, 28, 0.95);
}

.main-content {
  flex: 1;
  padding: 30px 30px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.main-content h1 {
  text-align: center;
  font-size: 32px;
  margin-bottom: 40px;
  color: #333;
}

#DivPic {
  display: flex;
  gap: 100px;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
}

.divination-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: #333;
  transition: transform 0.3s;
  cursor: pointer;
  padding: 15px;
  border-radius: 12px;
}

.divination-card:hover {
  transform: scale(1.05);
  background: #f5f5f5;
}

.divination-card img {
  width: 30vw;
  height: 80vh;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  margin-bottom: 16px;
}

.divination-card span {
  font-size: 22px;
  font-weight: 600;
}

.admin-btn {
  background-color: #7c3aed !important;
}

.admin-btn:hover {
  background-color: #6d28d9 !important;
}

@media (max-width: 768px) {
  #navbar {
    flex-direction: column;
    gap: 10px;
    padding: 12px 15px;
  }

  .nav-left h2 {
    font-size: 1.1rem;
  }

  .nav-right {
    flex-wrap: wrap;
    justify-content: center;
    gap: 8px;
  }

  .nav-btn {
    padding: 6px 12px;
    font-size: 0.85rem;
  }

  .main-content {
    padding: 20px 15px;
  }

  .main-content h1 {
    font-size: 24px;
    margin-bottom: 25px;
  }

  .divination-card img {
    width: 300px;
    height: 375px;
  }
  
  #DivPic {
    gap: 25px;
  }
}
</style>