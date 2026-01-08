<script setup>
defineProps({
  size: {
    type: String,
    default: 'medium' // small, medium, large
  },
  text: {
    type: String,
    default: ''
  }
})
</script>

<template>
  <div class="loading-container">
    <div :class="['spinner', size]">
      <div class="spinner-circle"></div>
    </div>
    <p v-if="text" class="loading-text">{{ text }}</p>
  </div>
</template>

<style scoped>
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 15px;
}

.spinner {
  display: inline-block;
  position: relative;
}

.spinner.small {
  width: 30px;
  height: 30px;
}

.spinner.medium {
  width: 50px;
  height: 50px;
}

.spinner.large {
  width: 80px;
  height: 80px;
}

.spinner-circle {
  width: 100%;
  height: 100%;
  border: 4px solid rgba(102, 126, 234, 0.1);
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.spinner.small .spinner-circle {
  border-width: 3px;
}

.spinner.large .spinner-circle {
  border-width: 5px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  color: #667eea;
  font-size: 1rem;
  font-weight: 600;
  margin: 0;
}
</style>