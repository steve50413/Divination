<script setup>
defineProps({
  type: {
    type: String,
    default: 'manju' // manju, avalo, japan
  }
})

const messages = {
  manju: '文殊菩薩正在為您解籤...',
  avalo: '觀音菩薩正在為您解籤...',
  japan: '正在為您抽取今日運勢...'
}
</script>

<template>
  <div class="divination-loading">
    <div class="animation-container">
      <!-- 籤筒動畫 -->
      <div class="fortune-stick-container">
        <div class="fortune-stick stick-1"></div>
        <div class="fortune-stick stick-2"></div>
        <div class="fortune-stick stick-3"></div>
        <div class="fortune-stick stick-4"></div>
        <div class="fortune-stick stick-5"></div>
      </div>
      
      <!-- 光環效果 -->
      <div class="glow-circle"></div>
    </div>
    
    <p class="loading-message">{{ messages[type] }}</p>
    
    <!-- 跳動的點點 -->
    <div class="dots">
      <span class="dot"></span>
      <span class="dot"></span>
      <span class="dot"></span>
    </div>
  </div>
</template>

<style scoped>
.divination-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  min-height: 400px;
}

.animation-container {
  position: relative;
  width: 200px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
}

/* 籤筒動畫 */
.fortune-stick-container {
  position: relative;
  width: 80px;
  height: 120px;
  display: flex;
  gap: 5px;
  justify-content: center;
  align-items: flex-end;
}

.fortune-stick {
  width: 8px;
  background: linear-gradient(to top, #d97706, #f59e0b);
  border-radius: 4px 4px 0 0;
  animation: stick-wave 1.5s ease-in-out infinite;
  box-shadow: 0 2px 8px rgba(217, 119, 6, 0.3);
}

.stick-1 {
  height: 60px;
  animation-delay: 0s;
}

.stick-2 {
  height: 80px;
  animation-delay: 0.1s;
}

.stick-3 {
  height: 100px;
  animation-delay: 0.2s;
}

.stick-4 {
  height: 80px;
  animation-delay: 0.3s;
}

.stick-5 {
  height: 60px;
  animation-delay: 0.4s;
}

@keyframes stick-wave {
  0%, 100% {
    transform: translateY(0) scaleY(1);
    opacity: 0.7;
  }
  50% {
    transform: translateY(-20px) scaleY(1.2);
    opacity: 1;
  }
}

/* 光環效果 */
.glow-circle {
  position: absolute;
  width: 180px;
  height: 180px;
  border-radius: 50%;
  background: radial-gradient(
    circle,
    rgba(102, 126, 234, 0.2) 0%,
    rgba(102, 126, 234, 0.05) 50%,
    transparent 70%
  );
  animation: glow-pulse 2s ease-in-out infinite;
}

@keyframes glow-pulse {
  0%, 100% {
    transform: scale(0.9);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.1);
    opacity: 1;
  }
}

/* 文字訊息 */
.loading-message {
  font-size: 1.2rem;
  color: #667eea;
  font-weight: 600;
  margin: 0 0 20px 0;
  text-align: center;
}

/* 跳動的點點 */
.dots {
  display: flex;
  gap: 8px;
}

.dot {
  width: 8px;
  height: 8px;
  background-color: #667eea;
  border-radius: 50%;
  animation: dot-bounce 1.4s ease-in-out infinite;
}

.dot:nth-child(1) {
  animation-delay: 0s;
}

.dot:nth-child(2) {
  animation-delay: 0.2s;
}

.dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes dot-bounce {
  0%, 80%, 100% {
    transform: translateY(0);
    opacity: 0.5;
  }
  40% {
    transform: translateY(-15px);
    opacity: 1;
  }
}

/* 響應式 */
@media (max-width: 768px) {
  .animation-container {
    width: 150px;
    height: 150px;
  }
  
  .loading-message {
    font-size: 1rem;
  }
}
</style>