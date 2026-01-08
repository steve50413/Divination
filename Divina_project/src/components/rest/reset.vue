<script setup>
// 這個元件不需要任何 JavaScript 邏輯
// 只是用來載入全域的 CSS Reset 樣式
</script>

<template>
  <!-- 不需要渲染任何內容 -->
</template>

<style>
/* CSS Reset - 重置瀏覽器預設樣式 */

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  height: 100%;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  line-height: 1.6;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 移除列表樣式 */
ul, ol {
  list-style: none;
}

/* 移除連結底線 */
a {
  text-decoration: none;
  color: inherit;
}

/* 圖片相關 */
img {
  max-width: 100%;
  height: auto;
  display: block;
}

/* 按鈕重置 */
button {
  border: none;
  background: none;
  cursor: pointer;
  font-family: inherit;
}

/* 輸入框重置 */
input, textarea, select {
  font-family: inherit;
  font-size: inherit;
  border: none;
  outline: none;
}

/* 表格重置 */
table {
  border-collapse: collapse;
  border-spacing: 0;
}

/* 標題重置 */
h1, h2, h3, h4, h5, h6 {
  font-weight: normal;
  font-size: inherit;
}
</style>