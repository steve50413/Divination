<script setup>
import { ref, onMounted, nextTick } from 'vue'
import {
  Chart,
  LineController,
  DoughnutController,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js'

Chart.register(
  LineController,
  DoughnutController,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
  Filler
)

const emits = defineEmits(['back'])

const loading = ref(true)
const overview = ref(null)
const distribution = ref(null)
const trend = ref([])
const trendDays = ref(7)

let trendChart = null
let distributionChart = null

onMounted(async () => {
  await loadStatistics()
})

async function loadStatistics() {
  loading.value = true
  const token = localStorage.getItem('token')
  
  try {
    const overviewRes = await fetch('http://localhost:3000/api/statistics/overview', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const overviewData = await overviewRes.json()
    if (overviewData.success) overview.value = overviewData.data
    
    const distRes = await fetch('http://localhost:3000/api/statistics/type-distribution', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const distData = await distRes.json()
    if (distData.success) {
      distribution.value = distData.data
      
      // 等待 DOM 更新後繪製圖表
      await nextTick()
      setTimeout(() => {
        createDistributionChart()
      }, 100)
    }
    
    await loadTrend()
    
  } catch (err) {
    console.error('載入統計錯誤:', err)
    alert('載入失敗')
  } finally {
    loading.value = false
  }
}

async function loadTrend() {
  const token = localStorage.getItem('token')
  
  try {
    const trendRes = await fetch(
      `http://localhost:3000/api/statistics/trend?days=${trendDays.value}`,
      { headers: { 'Authorization': `Bearer ${token}` } }
    )
    const trendData = await trendRes.json()
    if (trendData.success) {
      trend.value = trendData.data
      
      // 等待 DOM 更新後繪製圖表
      await nextTick()
      setTimeout(() => {
        createTrendChart()
      }, 100)
    }
  } catch (err) {
    console.error('載入趨勢錯誤:', err)
  }
}

async function changeDays(days) {
  trendDays.value = days
  await loadTrend()
}

function createTrendChart() {
  const canvas = document.getElementById('trendChart')
  if (!canvas) {
    console.error('找不到 trendChart canvas')
    return
  }
  
  if (trendChart) {
    trendChart.destroy()
  }
  
  const ctx = canvas.getContext('2d')
  
  trendChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: trend.value.map(item => {
        const date = new Date(item.date)
        return `${date.getMonth() + 1}/${date.getDate()}`
      }),
      datasets: [{
        label: '占卜次數',
        data: trend.value.map(item => item.count),
        borderColor: '#0066CC',
        backgroundColor: 'rgba(0, 102, 204, 0.1)',
        fill: true,
        tension: 0.4,
        pointRadius: 5,
        pointHoverRadius: 7,
        pointBackgroundColor: '#0066CC',
        pointBorderColor: '#fff',
        pointBorderWidth: 2
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          padding: 12,
          titleFont: {
            size: 14
          },
          bodyFont: {
            size: 13
          },
          callbacks: {
            label: function(context) {
              return `占卜次數: ${context.parsed.y} 次`
            }
          }
        }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            precision: 0
          },
          grid: {
            color: 'rgba(0, 0, 0, 0.05)'
          }
        },
        x: {
          grid: {
            display: false
          }
        }
      },
      animation: {
        duration: 1000,
        easing: 'easeInOutQuart'
      }
    }
  })
}

function createDistributionChart() {
  const canvas = document.getElementById('distributionChart')
  if (!canvas) {
    console.error('找不到 distributionChart canvas')
    return
  }
  
  if (distributionChart) {
    distributionChart.destroy()
  }
  
  const ctx = canvas.getContext('2d')
  
  distributionChart = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: ['文殊占卜', '觀音占卜', '每日一籤'],
      datasets: [{
        data: [
          distribution.value.manjuCount,
          distribution.value.avaloCount,
          distribution.value.japanCount
        ],
        backgroundColor: [
          '#0066CC',
          '#10b981',
          '#d32f2f'
        ],
        borderWidth: 0,
        hoverOffset: 10
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          padding: 12,
          callbacks: {
            label: function(context) {
              const label = context.label || ''
              const value = context.parsed || 0
              const total = context.dataset.data.reduce((a, b) => a + b, 0)
              const percentage = ((value / total) * 100).toFixed(1)
              return `${label}: ${value} 次 (${percentage}%)`
            }
          }
        }
      },
      animation: {
        animateRotate: true,
        animateScale: true,
        duration: 1000
      }
    }
  })
}
</script>

<template>
  <div class="statistics-container">
    <div class="statistics-box">
      
      <button class="back-btn" @click="$emit('back')">返回</button>

      <h1 class="main-title">📊 數據統計</h1>

      <div v-if="loading" class="loading">
        <p>載入中...</p>
      </div>

      <div v-else class="statistics-content">
        
        <!-- 總覽卡片 -->
        <div class="overview-cards">
          <div class="stat-card blue">
            <div class="stat-icon">👥</div>
            <div class="stat-info">
              <h3>總用戶數</h3>
              <p class="stat-number">{{ overview?.totalUsers || 0 }}</p>
            </div>
          </div>

          <div class="stat-card green">
            <div class="stat-icon">🎯</div>
            <div class="stat-info">
              <h3>總占卜次數</h3>
              <p class="stat-number">{{ overview?.totalDivinations || 0 }}</p>
            </div>
          </div>

          <div class="stat-card orange">
            <div class="stat-icon">📅</div>
            <div class="stat-info">
              <h3>今日占卜數</h3>
              <p class="stat-number">{{ overview?.todayDivinations || 0 }}</p>
            </div>
          </div>
        </div>

        <!-- 類型分布 -->
        <div class="section">
          <h2 class="section-title">占卜類型分布</h2>
          <div class="distribution-container">
            
            <!-- Chart.js 甜甜圈圖 -->
            <div class="chart-container">
              <canvas id="distributionChart"></canvas>
            </div>

            <!-- 圖例 -->
            <div class="distribution-legend">
              <div class="legend-item">
                <div class="legend-color" style="background: #0066CC;"></div>
                <div class="legend-text">
                  <span class="legend-name">文殊占卜</span>
                  <span class="legend-value">
                    {{ distribution?.manjuCount || 0 }} 次 
                    ({{ distribution?.manjuPercentage || 0 }}%)
                  </span>
                </div>
              </div>

              <div class="legend-item">
                <div class="legend-color" style="background: #10b981;"></div>
                <div class="legend-text">
                  <span class="legend-name">觀音占卜</span>
                  <span class="legend-value">
                    {{ distribution?.avaloCount || 0 }} 次 
                    ({{ distribution?.avaloPercentage || 0 }}%)
                  </span>
                </div>
              </div>

              <div class="legend-item">
                <div class="legend-color" style="background: #d32f2f;"></div>
                <div class="legend-text">
                  <span class="legend-name">每日一籤</span>
                  <span class="legend-value">
                    {{ distribution?.japanCount || 0 }} 次 
                    ({{ distribution?.japanPercentage || 0 }}%)
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 趨勢圖表 -->
        <div class="section">
          <div class="section-header">
            <h2 class="section-title">占卜趨勢</h2>
            <div class="trend-tabs">
              <button 
                :class="{ active: trendDays === 7 }" 
                @click="changeDays(7)"
              >
                最近7天
              </button>
              <button 
                :class="{ active: trendDays === 30 }" 
                @click="changeDays(30)"
              >
                最近30天
              </button>
            </div>
          </div>

          <!-- Chart.js 折線圖 -->
          <div class="chart-wrapper">
            <canvas id="trendChart"></canvas>
          </div>
        </div>

      </div>

    </div>
  </div>
</template>

<style scoped>
.statistics-container {
  background-color: #0072E3;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.statistics-box {
  background-color: white;
  width: 95vw;
  max-width: 1400px;
  padding: 40px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  position: relative;
  max-height: 90vh;
  overflow-y: auto;
}

.back-btn {
  position: absolute;
  top: 20px;
  left: 20px;
  background-color: #6b7280;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: bold;
  transition: background-color 0.2s;
}

.back-btn:hover {
  background-color: #4b5563;
}

.main-title {
  text-align: center;
  font-size: 2.5rem;
  color: #333;
  margin-bottom: 40px;
  margin-top: 20px;
}

.loading {
  text-align: center;
  padding: 60px;
  color: #666;
  font-size: 1.2rem;
}

.statistics-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 25px;
  border-radius: 12px;
  color: white;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-card.blue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card.green {
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
}

.stat-card.orange {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
}

.stat-icon {
  font-size: 3rem;
}

.stat-info h3 {
  font-size: 0.9rem;
  opacity: 0.9;
  margin-bottom: 8px;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: bold;
  margin: 0;
}

.section {
  background-color: #f9fafb;
  padding: 25px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 20px;
}

.distribution-container {
  display: flex;
  align-items: center;
  gap: 40px;
  flex-wrap: wrap;
  justify-content: center;
}

.chart-container {
  width: 250px;
  height: 250px;
}

.distribution-legend {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.legend-color {
  width: 24px;
  height: 24px;
  border-radius: 4px;
}

.legend-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.legend-name {
  font-weight: 600;
  color: #333;
  font-size: 1rem;
}

.legend-value {
  color: #666;
  font-size: 0.9rem;
}

.trend-tabs {
  display: flex;
  gap: 10px;
}

.trend-tabs button {
  background: white;
  border: 2px solid #e5e7eb;
  padding: 8px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.trend-tabs button.active {
  background-color: #0066CC;
  color: white;
  border-color: #0066CC;
}

.trend-tabs button:hover {
  border-color: #0066CC;
}

.chart-wrapper {
  height: 300px;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .statistics-box {
    padding: 20px;
  }

  .main-title {
    font-size: 1.8rem;
  }

  .overview-cards {
    grid-template-columns: 1fr;
  }

  .distribution-container {
    flex-direction: column;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
}
</style>