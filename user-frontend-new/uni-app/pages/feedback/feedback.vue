<template>
  <view class="container">
    <!-- 顶部导航栏 -->
    <view class="nav-bar">
      <view class="back-btn" @tap="goBack">
        <text class="iconfont icon-back">×</text>
      </view>
      <text class="title">Submit Feedback</text>
    </view>

    <!-- 反馈表单 -->
    <view class="feedback-form">
      <!-- 问题类型选择 -->
      <view class="form-item">
        <text class="label">Issue Type</text>
        <view class="issue-types">
          <view 
            v-for="(type, index) in issueTypes" 
            :key="index"
            class="issue-type-item"
            :class="{ active: selectedIssue === type }"
            @tap="selectIssue(type)"
          >
            <text>{{ type }}</text>
          </view>
        </view>
      </view>

      <view class="form-item">
        <text class="label">Feedback Content</text>
        <textarea
          v-model="message"
          placeholder="Please describe the issue in detail"
          maxlength="500"
          class="feedback-textarea"
        />
        <text class="word-count">{{message.length}}/500</text>
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-section">
      <button class="submit-btn" @tap="submitFeedback">Submit</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { API_BASE_URL } from '@/config/index'
import { onLoad } from '@dcloudio/uni-app'

const message = ref('')
const bookId = ref(null)
const scooterId = ref(null)
const selectedIssue = ref('')

// 问题类型列表
const issueTypes = [
  'Battery Issue',
  'Brake Problem',
  'Tire Problem',
  'Light Issue',
  'Handlebar Problem',
  'Other Issues'
]

// 选择问题类型
const selectIssue = (type) => {
  selectedIssue.value = type
}

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

// 提交反馈
const submitFeedback = async () => {
  if (!selectedIssue.value) {
    uni.showToast({
      title: 'Please select issue type',
      icon: 'none'
    })
    return
  }

  if (!message.value.trim()) {
    uni.showToast({
      title: 'Please enter feedback content',
      icon: 'none'
    })
    return
  }

  try {
    const response = await uni.request({
      url: `${API_BASE_URL}/user/feedback/add`,
      method: 'POST',
      data: {
        bookId: bookId.value,
        message: message.value.trim()
      },
      header: {
        'Content-Type': 'application/json'
      }
    })
    
    console.log('反馈提交响应:', response)
    console.log('响应数据:', response.data)

    if (response.data.statusCode === 200) {
      uni.showToast({
        title: 'Submitted successfully',
        icon: 'success'
      })
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    } else {
      throw new Error(response.data.message || 'Submission failed')
    }
  } catch (error) {
    console.error('Failed to submit feedback:', error)
    console.error('错误详情:', error.response?.data || error.message)
    uni.showToast({
      title: error.response?.data?.message || error.message || 'Submission failed',
      icon: 'none'
    })
  }
}

// 页面加载时获取参数
onLoad((options) => {
  console.log('页面参数:', options)
  if (options.orderId) {
    bookId.value = options.orderId
  }
  if (options.scooterId) {
    scooterId.value = options.scooterId
  }
})
</script>

<style>
.container {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 40rpx;
}

.nav-bar {
  display: flex;
  align-items: center;
  padding: 20rpx 32rpx;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-btn {
  font-size: 40rpx;
  color: #333;
  padding: 10rpx;
}

.title {
  flex: 1;
  text-align: center;
  font-size: 32rpx;
  font-weight: 500;
  margin-right: 40rpx;
}

.feedback-form {
  margin: 24rpx;
  padding: 32rpx;
  background: #fff;
  border-radius: 16rpx;
}

.form-item {
  position: relative;
  margin-bottom: 32rpx;
}

.form-item:last-child {
  margin-bottom: 0;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #666;
  margin-bottom: 12rpx;
}

.issue-types {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
}

.issue-type-item {
  padding: 16rpx 24rpx;
  background: #f5f5f5;
  border-radius: 8rpx;
  font-size: 26rpx;
  color: #666;
}

.issue-type-item.active {
  background: #1890ff;
  color: #fff;
}

.feedback-textarea {
  width: 100%;
  height: 300rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 24rpx;
  font-size: 28rpx;
  color: #333;
}

.word-count {
  position: absolute;
  right: 24rpx;
  bottom: 24rpx;
  font-size: 24rpx;
  color: #999;
}

.submit-section {
  margin: 48rpx 24rpx;
}

.submit-btn {
  width: 100%;
  height: 88rpx;
  background: #1890ff;
  color: #fff;
  border-radius: 44rpx;
  font-size: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.submit-btn:active {
  opacity: 0.8;
}
</style> 