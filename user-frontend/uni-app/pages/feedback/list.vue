<template>
  <view class="feedback-list">
    <!-- Navigation Bar -->
    <view class="nav-bar">
      <view class="back-btn" @tap="goBack">
        <text class="iconfont icon-back">×</text>
      </view>
      <text class="title">My Feedback</text>
    </view>

    <!-- Feedback List -->
    <view class="feedback-container">
      <view v-if="feedbackList.length === 0" class="empty-state">
        <text>No feedback records</text>
      </view>
      
      <view v-else class="feedback-items">
        <view v-for="feedback in feedbackList" :key="feedback.id" class="feedback-item">
          <view class="feedback-header">
            <view class="header-left">
              <text class="station-name">{{feedback.book.scooter.station.name}}</text>
              <text class="feedback-time">{{formatTime(feedback.createTime)}}</text>
            </view>
            <text :class="['feedback-status', getStatusClass(feedback.status)]">
              {{getStatusText(feedback.status)}}
            </text>
          </view>
          
          <view class="feedback-content">
            <view class="feedback-message">
              <text class="label">Feedback:</text>
              <text class="content">{{feedback.message}}</text>
            </view>
            
            <view class="feedback-result" v-if="feedback.result">
              <text class="label">Result:</text>
              <text class="content">{{feedback.result}}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { API_BASE_URL } from '@/config/index'

const feedbackList = ref([])

// Get user feedback list
const fetchFeedbackList = async () => {
  try {
    const userInfoStr = uni.getStorageSync('userInfo')
    if (!userInfoStr) {
      uni.showToast({
        title: 'Please login first',
        icon: 'none'
      })
      return
    }

    const userInfo = JSON.parse(userInfoStr)
    const response = await uni.request({
      url: `${API_BASE_URL}/user/feedback/${userInfo.id}`,
      method: 'GET',
      header: {
        'Content-Type': 'application/json'
      }
    })
    
    if (response.data.statusCode === 200) {
      feedbackList.value = response.data.data
    } else {
      throw new Error(response.data.message || 'Failed to get feedback list')
    }
  } catch (error) {
    console.error('Failed to get feedback list:', error)
    uni.showToast({
      title: error.message || 'Failed to get feedback list',
      icon: 'none'
    })
  }
}

// Format time
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// Get status text
const getStatusText = (status) => {
  const statusMap = {
    '未处理': 'Pending',
    '处理中': 'Processing',
    '已处理': 'Resolved'
  }
  return statusMap[status] || status
}

// Get status style class
const getStatusClass = (status) => {
  const statusText = getStatusText(status)
  const classMap = {
    'Pending': 'status-pending',
    'Processing': 'status-processing',
    'Resolved': 'status-resolved'
  }
  return classMap[statusText] || ''
}

// Go back
const goBack = () => {
  uni.navigateBack()
}

onMounted(() => {
  fetchFeedbackList()
})
</script>

<style>
.feedback-list {
  min-height: 100vh;
  background: linear-gradient(180deg, #f0f9ff 0%, #f7f8fa 100%);
}

.nav-bar {
  padding: 40rpx 32rpx;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.95);
  position: relative;
  backdrop-filter: blur(10px);
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.back-btn {
  font-size: 48rpx;
  color: #666;
  width: 48rpx;
  height: 48rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s;
}

.back-btn:active {
  background: rgba(0, 0, 0, 0.05);
}

.title {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-size: 32rpx;
  font-weight: 500;
  color: #333;
}

.feedback-container {
  padding: 24rpx;
}

.empty-state {
  text-align: center;
  padding: 48rpx;
  color: #999;
  font-size: 28rpx;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16rpx;
  margin-top: 24rpx;
}

.feedback-items {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.feedback-item {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20rpx;
  padding: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
  backdrop-filter: blur(10px);
  transition: all 0.3s;
}

.feedback-item:active {
  transform: scale(0.98);
}

.feedback-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20rpx;
  padding-bottom: 16rpx;
  border-bottom: 2rpx solid #f0f0f0;
}

.header-left {
  display: flex;
  flex-direction: column;
  gap: 8rpx;
}

.station-name {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
}

.feedback-time {
  font-size: 24rpx;
  color: #999;
}

.feedback-status {
  font-size: 24rpx;
  padding: 6rpx 20rpx;
  border-radius: 20rpx;
  font-weight: 500;
}

.status-pending {
  color: #faad14;
  background: #fffbe6;
}

.status-processing {
  color: #1890ff;
  background: #e6f7ff;
}

.status-resolved {
  color: #52c41a;
  background: #f6ffed;
}

.feedback-content {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.feedback-message,
.feedback-result {
  display: flex;
  flex-direction: column;
  gap: 12rpx;
}

.label {
  font-size: 26rpx;
  color: #666;
  font-weight: 500;
}

.content {
  font-size: 28rpx;
  color: #333;
  line-height: 1.6;
  padding: 16rpx;
  background: #f8f8f8;
  border-radius: 12rpx;
}

.feedback-result .content {
  background: #f6ffed;
  color: #52c41a;
}
</style> 