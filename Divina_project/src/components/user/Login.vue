<script setup>
import { ref } from 'vue'
import { useToast } from '../../lib/useToast'

const emits = defineEmits(['fontpage', 'signup', 'forgotPassword'])
const { success, error } = useToast()

const email = ref('')
const password = ref('')
const loading = ref(false)

async function login() {
  if (!email.value || !password.value) {
    error('請填寫所有欄位')
    return
  }

  loading.value = true

  try {
    const response = await fetch('http://localhost:3000/api/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email: email.value,
        password: password.value
      })
    })

    const data = await response.json()

    if (data.success) {
      localStorage.setItem('token', data.data.token)
      localStorage.setItem('userId', data.data.userId)
      localStorage.setItem('nickname', data.data.nickname)
      localStorage.setItem('email', data.data.email)
      localStorage.setItem('userRole', data.data.role || 'user')
      
      success('登入成功！')
      setTimeout(() => {
        emits('fontpage')
      }, 500)
    } else {
      error(data.message || '登入失敗')
    }
  } catch (err) {
    console.error('登入錯誤:', err)
    error('連線失敗，請稍後再試')
  } finally {
    loading.value = false
  }
}
</script>

<template>
    <section id="loginSection">
        <h1>登入</h1>

        <form id="loginForm">
        <div class="field">
            <label>
                <h3>Email</h3>
                <input type="email" v-model="email" placeholder="請輸入 Email">
            </label>
        </div>

        <div class="field">
            <label>
            <h3>密碼</h3>
            <input type="password" v-model="password" placeholder="請輸入密碼"/>
            </label>
        </div>

        <div id="subu">
            <button @click.prevent="login" :disabled="loading">
                {{ loading ? '登入中...' : '登入' }}
            </button>
            
            <!-- 忘記密碼連結 -->
            <p class="forgot-password-link">
              <a href="#" @click.prevent="$emit('forgotPassword')">忘記密碼？</a>
            </p>
            
            <div class="singin-text">還沒有帳號嗎？
            <a @click.prevent="$emit('signup')" href="#" class="signUpLink">註冊</a>一個吧！</div>
            <a @click.prevent="$emit('fontpage')" href="#" class="skip">免登入進入</a>
          </div>
        </form>
    </section>
</template>

<style scoped>
#loginSection {
    background-color: white;
    padding: 40px;
    border-radius: 16px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
    max-width: 400px;
    width: 90%;
}

h1 {
    text-align: center;
    color: #333;
    margin-bottom: 30px;
}

.field {
    margin-bottom: 20px;
}

.field h3 {
    margin-bottom: 8px;
    color: #555;
    font-size: 1rem;
}

.field input {
    width: 100%;
    padding: 12px;
    border: 2px solid #ddd;
    border-radius: 8px;
    font-size: 1rem;
    box-sizing: border-box;
}

.field input:focus {
    outline: none;
    border-color: #667eea;
}

#subu {
    display: flex;
    flex-direction: column;
    gap: 15px;
    margin-top: 25px;
}

#subu button {
    width: 100%;
    padding: 14px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border: none;
    border-radius: 8px;
    font-size: 1.1rem;
    font-weight: bold;
    cursor: pointer;
    transition: transform 0.2s;
}

#subu button:hover:not(:disabled) {
    transform: translateY(-2px);
}

#subu button:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.forgot-password-link {
    text-align: center;
    margin: 0;
}

.forgot-password-link a {
    color: #667eea;
    text-decoration: none;
    font-size: 0.9rem;
    font-weight: 600;
}

.forgot-password-link a:hover {
    text-decoration: underline;
}

.singin-text {
    text-align: center;
    color: #666;
    font-size: 0.95rem;
}

.signUpLink {
    color: #667eea;
    text-decoration: none;
    font-weight: bold;
}

.signUpLink:hover {
    text-decoration: underline;
}

.skip {
    text-align: center;
    color: #999;
    text-decoration: none;
    font-size: 0.9rem;
    display: block;
}

.skip:hover {
    color: #667eea;
}
</style>