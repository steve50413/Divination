<script setup>
import { ref } from 'vue'
import { useToast } from '../../lib/useToast'

const emits = defineEmits(['back', 'success'])
const { success, error, info } = useToast()

const step = ref(1) // 1: 輸入Email, 2: 輸入驗證碼和新密碼

const formData = ref({
  email: '',
  verificationCode: '',
  newPassword: '',
  confirmPassword: ''
})

const loading = ref(false)
const sendingCode = ref(false)
const countdown = ref(0)

// 發送驗證碼
async function sendVerificationCode() {
  if (!formData.value.email) {
    error('請輸入 Email')
    return
  }

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(formData.value.email)) {
    error('Email 格式不正確')
    return
  }

  sendingCode.value = true

  try {
    const response = await fetch('http://localhost:3000/api/auth/forgot-password/send-code', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ email: formData.value.email })
    })

    const data = await response.json()

    if (data.success) {
      step.value = 2
      countdown.value = 60
      startCountdown()
      info('驗證碼已發送到您的 Email！')
    } else {
      error(data.message || '發送失敗')
    }
  } catch (err) {
    console.error('發送驗證碼錯誤:', err)
    error('連線失敗，請稍後再試')
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

// 重設密碼
async function handleResetPassword() {
  if (!formData.value.verificationCode || !formData.value.newPassword) {
    error('請填寫所有欄位')
    return
  }

  if (formData.value.newPassword !== formData.value.confirmPassword) {
    error('兩次密碼不一致')
    return
  }

  if (formData.value.newPassword.length < 6) {
    error('密碼至少需要 6 個字元')
    return
  }

  loading.value = true

  try {
    const response = await fetch('http://localhost:3000/api/auth/forgot-password/reset', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email: formData.value.email,
        verificationCode: formData.value.verificationCode,
        newPassword: formData.value.newPassword
      })
    })

    const data = await response.json()

    if (data.success) {
      success('密碼重設成功！請使用新密碼登入')
      setTimeout(() => {
        emits('success')
      }, 1000)
    } else {
      error(data.message || '重設失敗')
    }
  } catch (err) {
    console.error('重設密碼錯誤:', err)
    error('連線失敗，請稍後再試')
  } finally {
    loading.value = false
  }
}

// 重新發送驗證碼
function resendCode() {
  step.value = 1
  formData.value.verificationCode = ''
  formData.value.newPassword = ''
  formData.value.confirmPassword = ''
}
</script>

<template>
  <div class="forgot-password-container">
    <div class="forgot-password-box">
      
      <!-- 返回按鈕 -->
      <button type="button" class="back-btn" @click="$emit('back')">← 返回</button>

      <!-- 標題 -->
      <h1 class="title">🔑 忘記密碼</h1>

      <!-- 步驟 1：輸入 Email -->
      <form v-if="step === 1" @submit.prevent="sendVerificationCode" class="form">
        
        <p class="hint-text">請輸入您的註冊 Email，我們將發送驗證碼給您</p>

        <div class="form-group">
          <label>Email</label>
          <input 
            type="email" 
            v-model="formData.email" 
            placeholder="請輸入 Email"
            required
          />
        </div>

        <button type="submit" class="submit-btn" :disabled="sendingCode">
          {{ sendingCode ? '發送中...' : '發送驗證碼' }}
        </button>

      </form>

      <!-- 步驟 2：輸入驗證碼和新密碼 -->
      <form v-if="step === 2" @submit.prevent="handleResetPassword" class="form">
        
        <p class="hint-text">驗證碼已發送到 <strong>{{ formData.email }}</strong></p>

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
          <small class="hint">驗證碼有效期 5 分鐘</small>
        </div>

        <!-- 新密碼 -->
        <div class="form-group">
          <label>新密碼</label>
          <input 
            type="password" 
            v-model="formData.newPassword" 
            placeholder="請輸入新密碼（至少 6 個字元）"
            required
          />
        </div>

        <!-- 確認密碼 -->
        <div class="form-group">
          <label>確認密碼</label>
          <input 
            type="password" 
            v-model="formData.confirmPassword" 
            placeholder="請再次輸入新密碼"
            required
          />
        </div>

        <button type="submit" class="submit-btn" :disabled="loading">
          {{ loading ? '重設中...' : '重設密碼' }}
        </button>

        <!-- 重新發送 -->
        <button 
          type="button" 
          @click="resendCode"
          class="resend-btn"
          :disabled="countdown > 0"
        >
          <span v-if="countdown > 0">{{ countdown }} 秒後可重新發送</span>
          <span v-else>重新發送驗證碼</span>
        </button>

      </form>

    </div>
  </div>
</template>

<style scoped>
.forgot-password-container {
  
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.forgot-password-box {
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

.hint-text {
  text-align: center;
  color: #666;
  margin-bottom: 25px;
  line-height: 1.6;
}

.hint-text strong {
  color: #667eea;
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

.form {
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

.resend-btn {
  padding: 12px;
  background: white;
  color: #667eea;
  border: 2px solid #667eea;
  border-radius: 8px;
  font-size: 0.95rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.resend-btn:hover:not(:disabled) {
  background: #667eea;
  color: white;
}

.resend-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@media (max-width: 768px) {
  .forgot-password-box {
    padding: 30px 20px;
  }

  .title {
    font-size: 1.6rem;
  }
}
</style>