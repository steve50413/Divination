<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  message: String,
  type: {
    type: String,
    default: 'info' // success, error, info, warning
  },
  duration: {
    type: Number,
    default: 3000
  },
  show: Boolean
})

const emits = defineEmits(['close'])

const visible = ref(props.show)

watch(() => props.show, (newVal) => {
  visible.value = newVal
  if (newVal) {
    setTimeout(() => {
      visible.value = false
      emits('close')
    }, props.duration)
  }
})

const getIcon = () => {
  switch (props.type) {
    case 'success': return '✅'
    case 'error': return '❌'
    case 'warning': return '⚠️'
    case 'info': return 'ℹ️'
    default: return 'ℹ️'
  }
}

const close = () => {
  visible.value = false
  emits('close')
}
</script>

<template>
  <Transition name="toast">
    <div v-if="visible" :class="['toast', type]" @click="close">
      <span class="toast-icon">{{ getIcon() }}</span>
      <span class="toast-message">{{ message }}</span>
      <button class="toast-close" @click.stop="close">×</button>
    </div>
  </Transition>
</template>

<style scoped>
.toast {
  position: fixed;
  top: 20px;
  right: 20px;
  min-width: 280px;
  max-width: 400px;
  padding: 16px 20px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  gap: 12px;
  z-index: 9999;
  cursor: pointer;
  transition: all 0.3s ease;
}

.toast:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

.toast-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.toast-message {
  flex: 1;
  color: white;
  font-size: 0.95rem;
  font-weight: 500;
  line-height: 1.4;
}

.toast-close {
  background: none;
  border: none;
  color: white;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background 0.2s;
  flex-shrink: 0;
}

.toast-close:hover {
  background: rgba(255, 255, 255, 0.2);
}

/* 不同類型的背景色 */
.toast.success {
  background: linear-gradient(135deg, #10b981, #059669);
}

.toast.error {
  background: linear-gradient(135deg, #ef4444, #dc2626);
}

.toast.warning {
  background: linear-gradient(135deg, #f59e0b, #d97706);
}

.toast.info {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
}

/* 動畫 */
.toast-enter-active {
  animation: toast-in 0.3s ease-out;
}

.toast-leave-active {
  animation: toast-out 0.3s ease-in;
}

@keyframes toast-in {
  from {
    opacity: 0;
    transform: translateX(100%);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes toast-out {
  from {
    opacity: 1;
    transform: translateX(0);
  }
  to {
    opacity: 0;
    transform: translateX(100%);
  }
}

/* 手機版 */
@media (max-width: 768px) {
  .toast {
    right: 10px;
    left: 10px;
    min-width: auto;
    max-width: none;
  }
}
</style>