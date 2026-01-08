import { ref } from 'vue'

const toastState = ref({
  show: false,
  message: '',
  type: 'info',
  duration: 3000
})

export function useToast() {
  const showToast = (message, type = 'info', duration = 3000) => {
    toastState.value = {
      show: true,
      message,
      type,
      duration
    }
  }

  const success = (message, duration) => {
    showToast(message, 'success', duration)
  }

  const error = (message, duration) => {
    showToast(message, 'error', duration)
  }

  const warning = (message, duration) => {
    showToast(message, 'warning', duration)
  }

  const info = (message, duration) => {
    showToast(message, 'info', duration)
  }

  const close = () => {
    toastState.value.show = false
  }

  return {
    toastState,
    showToast,
    success,
    error,
    warning,
    info,
    close
  }
}