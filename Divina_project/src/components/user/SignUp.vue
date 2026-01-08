<script setup>
import { ref } from 'vue'
import { useToast } from '../../lib/useToast'

const emits = defineEmits(['back', 'success'])
const { success, error, info } = useToast()

const formData = ref({
  email: '',
  nickname: '',
  password: '',
  confirmPassword: '',
  verificationCode: ''
})

const loading = ref(false)
const sendingCode = ref(false)
const countdown = ref(0)
const codeSent = ref(false)

// 發送驗證碼
async function sendVerificationCode() {
  if (!formData.value.email) {
    error('請輸入 Email')
    return
  }

  // Email 格式驗證
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(formData.value.email)) {
    error('Email 格式不正確')
    return
  }

  sendingCode.value = true

  try {
    const response = await fetch('http://localhost:3000/api/auth/send-code', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email: formData.value.email })
    })

    const data = await response.json()

    if (data.success) {
      codeSent.value = true
      countdown.value = 60
      startCountdown()
      alert('驗證碼已發送到您的 Email！')
    } else {
      errorMessage.value = data.message || '發送失敗'
    }
  } catch (err) {
    console.error('發送驗證碼錯誤:', err)
    errorMessage.value = '連線失敗，請稍後再試'
  } finally {
    sendingCode.value = false
  }
}

// 倒數計時
function startCountdown() {
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

// 註冊
async function handleSignup() {
  errorMessage.value = ''

  // 驗證欄位
  if (!formData.value.email || !formData.value.nickname || 
      !formData.value.password || !formData.value.verificationCode) {
    errorMessage.value = '請填寫所有欄位'
    return
  }

  if (formData.value.password !== formData.value.confirmPassword) {
    errorMessage.value = '兩次密碼不一致'
    return
  }

  if (formData.value.password.length < 6) {
    errorMessage.value = '密碼至少需要 6 個字元'
    return
  }

  loading.value = true

  try {
    const response = await fetch('http://localhost:3000/api/auth/signup', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email: formData.value.email,
        nickname: formData.value.nickname,
        password: formData.value.password,
        verificationCode: formData.value.verificationCode
      })
    })

    const data = await response.json()

    if (data.success) {
      // 儲存 token 和用戶資訊
      localStorage.setItem('token', data.data.token)
      localStorage.setItem('userId', data.data.userId)
      localStorage.setItem('nickname', data.data.nickname)
      localStorage.setItem('email', data.data.email)
      localStorage.setItem('userRole', data.data.role || 'user')
      
      alert('註冊成功！')
      emits('success')
    } else {
      errorMessage.value = data.message || '註冊失敗'
    }
  } catch (err) {
    console.error('註冊錯誤:', err)
    errorMessage.value = '連線失敗，請稍後再試'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="signup-container">
    <div class="signup-box">
      
      <!-- 返回按鈕 -->
      <button type="button" class="back-btn" @click="$emit('back')">← 返回</button>

      <!-- 標題 -->
      <h1 class="title">🔮 註冊帳號</h1>

      <!-- 錯誤訊息 -->
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <!-- 註冊表單 -->
      <form @submit.prevent="handleSignup" class="signup-form">
        
        <!-- Email + 發送驗證碼 -->
        <div class="form-group">
          <label>Email</label>
          <div class="input-with-button">
            <input 
              type="email" 
              v-model="formData.email" 
              placeholder="請輸入 Email"
              :disabled="codeSent"
              required
            />
            <button 
              type="button" 
              @click="sendVerificationCode"
              :disabled="sendingCode || countdown > 0"
              class="send-code-btn"
            >
              <span v-if="sendingCode">發送中...</span>
              <span v-else-if="countdown > 0">{{ countdown }}秒</span>
              <span v-else>{{ codeSent ? '重新發送' : '發送驗證碼' }}</span>
            </button>
          </div>
        </div>

        <!-- 驗證碼 -->
        <div class="form-group">
          <label>驗證碼</label>
          <input 
            type="text" 
            v-model="formData.verificationCode" 
            placeholder="請輸入 6 位數驗證碼"
            maxlength="6"
            required
          />
          <small v-if="codeSent" class="hint">驗證碼已發送到您的 Email（有效期 5 分鐘）</small>
        </div>

        <!-- 暱稱 -->
        <div class="form-group">
          <label>暱稱</label>
          <input 
            type="text" 
            v-model="formData.nickname" 
            placeholder="請輸入暱稱"
            required
          />
        </div>

        <!-- 密碼 -->
        <div class="form-group">
          <label>密碼</label>
          <input 
            type="password" 
            v-model="formData.password" 
            placeholder="請輸入密碼（至少 6 個字元）"
            required
          />
        </div>

        <!-- 確認密碼 -->
        <div class="form-group">
          <label>確認密碼</label>
          <input 
            type="password" 
            v-model="formData.confirmPassword" 
            placeholder="請再次輸入密碼"
            required
          />
        </div>

        <!-- 註冊按鈕 -->
        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? '註冊中...' : '註冊' }}
        </button>

      </form>

      <!-- 已有帳號 -->
      <p class="login-link">
        已有帳號？<a href="#" @click.prevent="$emit('back')">立即登入</a>
      </p>

    </div>
  </div>
</template>

<style scoped>
.signup-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.signup-box {
  background-color: white;
  width: 90vw;
  max-width: 500px;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  position: relative;
}

.back-btn {
  position: absolute;
  top: 20px;
  left: 20px;
  background: none;
  border: none;
  color: #667eea;
  font-size: 1rem;
  cursor: pointer;
  font-weight: bold;
  transition: color 0.2s;
}

.back-btn:hover {
  color: #764ba2;
}

.title {
  text-align: center;
  font-size: 2rem;
  color: #333;
  margin-bottom: 30px;
  margin-top: 10px;
}

.error-message {
  background-color: #fee;
  color: #c33;
  padding: 12px;
  border-radius: 8px;
  margin-bottom: 20px;
  text-align: center;
  border: 1px solid #fcc;
  position: relative;
  z-index: 1;
}

.signup-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 600;
  color: #555;
  font-size: 0.95rem;
}

.form-group input {
  padding: 12px 15px;
  border: 2px solid #ddd;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: #667eea;
}

.form-group input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.input-with-button {
  display: flex;
  gap: 10px;
}

.input-with-button input {
  flex: 1;
}

.send-code-btn {
  padding: 12px 20px;
  background-color: #10b981;
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: bold;
  cursor: pointer;
  white-space: nowrap;
  transition: background-color 0.2s;
}

.send-code-btn:hover:not(:disabled) {
  background-color: #059669;
}

.send-code-btn:disabled {
  background-color: #9ca3af;
  cursor: not-allowed;
}

.hint {
  color: #10b981;
  font-size: 0.85rem;
}

.submit-btn {
  padding: 14px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: bold;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  margin-top: 10px;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #666;
  position: relative;
  z-index: 10;
}

.login-link a {
  color: #667eea;
  cursor: pointer;
  font-weight: bold;
  text-decoration: none;
  position: relative;
  z-index: 10;
}

.login-link a:hover {
  text-decoration: underline;
}

@media (max-width: 768px) {
  .signup-box {
    padding: 30px 20px;
  }

  .title {
    font-size: 1.6rem;
  }

  .input-with-button {
    flex-direction: column;
  }

  .send-code-btn {
    width: 100%;
  }
}
</style>