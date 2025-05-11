<template>
  <view class="pay-container">
    <!-- Top Navigation -->
    <view class="nav-bar">
      <view class="back-btn" @tap="goBack">
        <text class="iconfont icon-back">×</text>
      </view>
      <text class="title">Payment</text>
    </view>

    <!-- Order Information -->
    <view class="order-info">
      <view class="amount">
        <text class="label">Amount</text>
        <text class="value">¥{{amount}}</text>
      </view>
      <view class="detail-item">
        <text class="label">Duration</text>
        <text class="value">{{duration}}</text>
      </view>
      <view class="detail-item">
        <text class="label">Pickup Time</text>
        <text class="value">{{pickupTime}}</text>
      </view>
      <view class="detail-item">
        <text class="label">Return Time</text>
        <text class="value">{{returnTime}}</text>
      </view>
    </view>

    <!-- Payment Methods -->
    <view class="payment-methods">
      <text class="section-title">Select Payment Method</text>
      <view class="method-list">
        <view class="method-item" :class="{'selected': selectedMethod === 'card'}" @tap="selectMethod('card')">
          <view class="method-info">
            <image class="method-icon" src="/static/icons/card.png"></image>
            <text class="method-name">Bank Card</text>
          </view>
          <view class="card-info" v-if="userCard && userCard.cardNumber">
            <text class="card-number">**** **** **** {{userCard.cardNumber.slice(-4)}}</text>
            <text class="bank-name">{{userCard.bankName}}</text>
          </view>
          <text class="bind-card" v-else @tap="goToBindCard">Bind Card</text>
        </view>
      </view>
    </view>

    <!-- Payment Button -->
    <view class="bottom-bar">
      <button class="pay-btn" :disabled="!canPay" @tap="showPasswordInput">
        {{payButtonText}}
      </button>
    </view>

    <!-- 密码输入弹窗 -->
    <view class="modal" v-if="showPasswordModal">
      <view class="modal-mask" @tap="closePasswordModal"></view>
      <view class="password-modal">
        <view class="modal-title">Enter Payment Password</view>
        <view class="password-input">
          <view class="password-dots">
            <view 
              v-for="(dot, index) in 6" 
              :key="index"
              class="dot"
              :class="{ 'filled': password.length > index }"
            ></view>
          </view>
        </view>
        <!-- 数字键盘 -->
        <view class="keyboard">
          <view class="keyboard-row" v-for="(row, rowIndex) in keyboard" :key="rowIndex">
            <view 
              class="key" 
              v-for="(key, keyIndex) in row" 
              :key="keyIndex"
              @tap="handleKeyPress(key)"
            >
              <text>{{ key }}</text>
            </view>
          </view>
          <view class="keyboard-row">
            <view class="key delete" @tap="handleDelete">
              <text>Delete</text>
            </view>
            <view class="key zero" @tap="handleKeyPress('0')">
              <text>0</text>
            </view>
            <view class="key confirm" @tap="handlePay">
              <text>Confirm</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { API_BASE_URL } from '@/config/index'

const amount = ref(0)
const duration = ref('')
const pickupTime = ref('')
const returnTime = ref('')
const scooterId = ref('')
const selectedMethod = ref('card')
const userCard = ref(null)
const hireOptionId = ref(1)
const orderDetail = ref(null)
const showPasswordModal = ref(false)
const password = ref('')

const keyboard = [
  ['1', '2', '3'],
  ['4', '5', '6'],
  ['7', '8', '9']
]

// 计算属性：是否可以支付
const canPay = computed(() => {
  return selectedMethod.value === 'card' && userCard.value
})

// 计算属性：支付按钮文本
const payButtonText = computed(() => {
  if (!userCard.value) return 'Please bind a bank card'
  return `Confirm Payment ¥${amount.value}`
})

// 选择支付方式
const selectMethod = (method) => {
  selectedMethod.value = method
}

// 去绑定银行卡
const goToBindCard = () => {
  uni.navigateTo({
    url: '/pages/card/card'
  })
}

// 格式化时间为指定格式
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  
  // 尝试解析ISO格式的时间字符串
  const date = new Date(timeStr)
  if (!isNaN(date.getTime())) {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hours}:${minutes}`
  }
  
  // 如果无法解析ISO格式，则尝试解析其他格式
  const parts = timeStr.split(' ')
  if (parts.length >= 2) {
    const [datePart, timePart] = parts
    const [year, month, day] = datePart.split('-')
    const [hours, minutes] = timePart.split(':')
    return `${year}-${month}-${day} ${hours}:${minutes}`
  }
  
  return timeStr
}

// 获取订单详情
const fetchOrderDetail = async (orderId) => {
  console.log('Start fetching order details, order ID:', orderId)
  try {
    const userInfoStr = uni.getStorageSync('userInfo')
    console.log('User info string:', userInfoStr)
    if (!userInfoStr) {
      uni.showToast({
        title: 'Please login first',
        icon: 'none'
      })
      return
    }

    const userInfo = JSON.parse(userInfoStr)
    console.log('Parsed user info:', userInfo)
    
    const response = await uni.request({
      url: `${API_BASE_URL}/user/book/id/${orderId}`,
      method: 'GET',
      header: {
        'Content-Type': 'application/json'
      }
    })
    console.log('API response:', response.data)
    
    if (response.data.statusCode === 200 && response.data.data) {
      orderDetail.value = response.data.data
      console.log('Order details:', orderDetail.value)
      
      // 更新显示的数据
      amount.value = orderDetail.value.totalCost
      duration.value = formatDuration(orderDetail.value.startTime, orderDetail.value.endTime)
      pickupTime.value = formatTime(orderDetail.value.startTime)
      returnTime.value = formatTime(orderDetail.value.endTime)
      scooterId.value = orderDetail.value.scooter.id
      
      console.log('Updated display data:', {
        amount: amount.value,
        duration: duration.value,
        pickupTime: pickupTime.value,
        returnTime: returnTime.value,
        scooterId: scooterId.value
      })
      
      // 根据租车时长设置hireOptionId
      if (duration.value.includes('周')) {
        hireOptionId.value = 4 // 一周
      } else if (duration.value.includes('天')) {
        hireOptionId.value = 3 // 一天
      } else if (duration.value.includes('小时')) {
        const hours = parseInt(duration.value)
        if (hours === 1) {
          hireOptionId.value = 1 // 1小时
        } else if (hours === 4) {
          hireOptionId.value = 2 // 4小时
        }
      }
      console.log('Set hireOptionId:', hireOptionId.value)
    } else {
      throw new Error('Failed to get order details')
    }
  } catch (error) {
    console.error('Failed to get order details:', error)
    uni.showToast({
      title: 'Failed to get order details',
      icon: 'none'
    })
  }
}

// 计算骑行时长
const formatDuration = (startTime, endTime) => {
  const start = new Date(startTime)
  const end = new Date(endTime)
  const diff = end - start
  const minutes = Math.floor(diff / 1000 / 60)
  if (minutes < 60) {
    return `${minutes} minutes`
  }
  const hours = Math.floor(minutes / 60)
  const remainMinutes = minutes % 60
  return `${hours} hours ${remainMinutes} minutes`
}

// 显示密码输入弹窗
const showPasswordInput = () => {
  password.value = ''
  showPasswordModal.value = true
}

// 关闭密码输入弹窗
const closePasswordModal = () => {
  password.value = ''
  showPasswordModal.value = false
}

// 处理按键输入
const handleKeyPress = (key) => {
  if (password.value.length < 6) {
    password.value += key
  }
}

// 处理删除
const handleDelete = () => {
  if (password.value.length > 0) {
    password.value = password.value.slice(0, -1)
  }
}

// 处理支付
const handlePay = async () => {
  if (!orderDetail.value) {
    uni.showToast({
      title: 'Incomplete order information',
      icon: 'none'
    })
    return
  }

  if (password.value.length !== 6) {
    uni.showToast({
      title: 'Please enter 6-digit password',
      icon: 'none'
    })
    return
  }

  try {
    const response = await uni.request({
      url: `${API_BASE_URL}/user/book/pay/${orderDetail.value.id}`,
      method: 'POST',
      data: {
        password: password.value
      },
      header: {
        'Content-Type': 'application/json'
      }
    })

    if (response.data.statusCode === 200) {
      uni.showToast({
        title: 'Payment successful',
        icon: 'success'
      })
      
      // 关闭密码弹窗
      closePasswordModal()
      
      // 发送刷新事件
      uni.$emit('refreshOrderList')
      
      // 延迟跳转到订单列表页
      setTimeout(() => {
        uni.switchTab({
          url: '/pages/order/order',
          success: () => {
            // 触发订单页面的刷新
            uni.$emit('refreshOrderList')
            // 延迟执行刷新，确保页面已经加载完成
            setTimeout(() => {
              const currentPage = getCurrentPages()[getCurrentPages().length - 1]
              if (currentPage && currentPage.$vm && currentPage.$vm.refreshPage) {
                currentPage.$vm.refreshPage()
              }
            }, 100)
          },
          fail: () => {
            uni.reLaunch({
              url: '/pages/order/order'
            })
          }
        })
      }, 1500)
    } else {
      throw new Error(response.data.message || 'Payment failed')
    }
  } catch (error) {
    console.error('Payment failed:', error)
    uni.showToast({
      title: error.message || 'Payment failed',
      icon: 'none'
    })
  }
}

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

// 刷新页面
const refreshPage = () => {
  // 获取用户银行卡信息
  try {
    const userInfoStr = uni.getStorageSync('userInfo')
    if (userInfoStr) {
      const userInfo = JSON.parse(userInfoStr)
      if (userInfo.card) {
        userCard.value = userInfo.card
        console.log('用户银行卡信息已更新:', userCard.value)
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 页面显示时触发
onShow(() => {
  refreshPage()
})

onLoad((options) => {
  console.log('支付页面加载，参数:', options)
  // 获取订单ID并获取订单详情
  if (options.id) {
    console.log('有订单ID，开始获取订单详情')
    fetchOrderDetail(options.id)
  } else {
    console.log('没有订单ID，使用传入的参数')
    // 如果没有订单ID，使用传入的参数
    amount.value = parseFloat(options.amount)
    duration.value = options.duration
    pickupTime.value = decodeURIComponent(options.pickupTime)
    returnTime.value = decodeURIComponent(options.returnTime)
    scooterId.value = options.scooterId
  }
})
</script>

<style>
.pay-container {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 120rpx;
}

.nav-bar {
  padding: 40rpx 32rpx;
  display: flex;
  align-items: center;
  background: #fff;
  position: relative;
}

.back-btn {
  font-size: 48rpx;
  color: #999;
}

.title {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  font-size: 32rpx;
  font-weight: 500;
}

.order-info {
  margin: 24rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
}

.amount {
  text-align: center;
  margin-bottom: 32rpx;
}

.amount .label {
  font-size: 28rpx;
  color: #666;
  display: block;
  margin-bottom: 16rpx;
}

.amount .value {
  font-size: 48rpx;
  color: #ff4d4f;
  font-weight: bold;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 2rpx solid #f5f5f5;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-item .label {
  font-size: 28rpx;
  color: #666;
}

.detail-item .value {
  font-size: 28rpx;
  color: #333;
}

.payment-methods {
  margin: 24rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
}

.section-title {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 24rpx;
}

.method-list {
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.method-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border: 2rpx solid #f5f5f5;
  border-radius: 16rpx;
  transition: all 0.3s;
}

.method-item.selected {
  border-color: #1890ff;
  background: #e6f7ff;
}

.method-info {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.method-icon {
  width: 48rpx;
  height: 48rpx;
}

.method-name {
  font-size: 28rpx;
  color: #333;
}

.card-info {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.card-number {
  font-size: 24rpx;
  color: #666;
}

.bank-name {
  font-size: 24rpx;
  color: #999;
}

.bind-card {
  font-size: 28rpx;
  color: #1890ff;
}

.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 24rpx;
  box-shadow: 0 -4rpx 12rpx rgba(0,0,0,0.05);
}

.pay-btn {
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

.pay-btn[disabled] {
  background: #d9d9d9;
}

/* 密码输入弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 999;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
}

.password-modal {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  width: 600rpx;
  position: relative;
  z-index: 1000;
}

.modal-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #333;
  text-align: center;
  margin-bottom: 36rpx;
}

.password-input {
  position: relative;
  margin-bottom: 32rpx;
}

.password-dots {
  display: flex;
  justify-content: space-between;
  padding: 0 40rpx;
}

.dot {
  width: 20rpx;
  height: 20rpx;
  border-radius: 50%;
  background: #f5f5f5;
  border: 2rpx solid #ddd;
}

.dot.filled {
  background: #333;
  border-color: #333;
}

.hidden-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  z-index: -1;
}

.modal-buttons {
  display: flex;
  gap: 24rpx;
}

.modal-btn {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  border-radius: 40rpx;
  font-size: 30rpx;
  font-weight: 500;
}

.modal-btn.cancel {
  background: #f5f5f5;
  color: #666;
}

.modal-btn.confirm {
  background: #1890ff;
  color: #fff;
}

.modal-btn:active {
  opacity: 0.8;
}

/* 数字键盘样式 */
.keyboard {
  margin-top: 32rpx;
}

.keyboard-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.key {
  width: 180rpx;
  height: 100rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  color: #333;
}

.key:active {
  background: #e6e6e6;
}

.key.delete {
  background: #f5f5f5;
  color: #666;
  font-size: 28rpx;
}

.key.zero {
  width: 180rpx;
}

.key.confirm {
  background: #1890ff;
  color: #fff;
  font-size: 28rpx;
}

.key.confirm:active {
  background: #40a9ff;
}
</style> 