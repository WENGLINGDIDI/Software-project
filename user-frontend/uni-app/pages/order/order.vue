<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { API_BASE_URL } from '@/config/index'
import { onLoad } from '@dcloudio/uni-app'

// 修改选项卡配置
const tabs = [
  { type: 'all', name: 'All' },
  { type: 'unpaid', name: 'Unpaid' },
  { type: 'notstarted', name: 'Pending' },
  { type: 'ongoing', name: 'Ongoing' },
  { type: 'completed', name: 'Completed' }
]

const currentTab = ref('all')
const orderList = ref([])


// 模拟订单数据
const mockOrders = [
  {
    "id": 1,
    "user": {
      "id": 1,
      "name": "name1",
      "email": "1550320492@qq.com"
    },
    "scooter": {
      "id": 1,
      "station": {
        "id": 1,
        "name": "Chunxi Road Station",
        "latitude": 30.6586,
        "longitude": 104.0757
      }
    },
    "totalCost": 60.0,
    "status": 3, // 已完成
    "payed": 1,
    "startTime": "2025-03-13T11:04:41.605087",
    "endTime": "2025-03-13T22:04:41.605087"
  },
  {
    "id": 2,
    "user": {
      "id": 1,
      "name": "name1",
      "email": "1550320492@qq.com"
    },
    "scooter": {
      "id": 2,
      "station": {
        "id": 2,
        "name": "Academy Street Station",
        "latitude": 30.6678,
        "longitude": 104.0873
      }
    },
    "totalCost": 80.0,
    "status": 0, // 待支付
    "payed": 0,
    "startTime": "2025-04-13T09:00:00",
    "endTime": "2025-04-13T21:00:00"
  },
  {
    "id": 3,
    "user": {
      "id": 1,
      "name": "name1",
      "email": "1550320492@qq.com"
    },
    "scooter": {
      "id": 3,
      "station": {
        "id": 3,
        "name": "Shuangshui Street Station",
        "latitude": 30.7048,
        "longitude": 104.0731
      }
    },
    "totalCost": 100.0,
    "status": 1, // 进行中
    "payed": 1,
    "startTime": "2025-04-12T14:00:00",
    "endTime": "2025-04-13T14:00:00"
  },
  {
    "id": 4,
    "user": {
      "id": 1,
      "name": "name1",
      "email": "1550320492@qq.com"
    },
    "scooter": {
      "id": 4,
      "station": {
        "id": 4,
        "name": "Tianfu Square Station",
        "latitude": 30.6571,
        "longitude": 104.0657
      }
    },
    "totalCost": 70.0,
    "status": 4, // 未开始状态
    "payed": 1,
    "startTime": "2025-04-15T10:00:00",
    "endTime": "2025-04-15T22:00:00"
  }
]

// 获取订单列表
const fetchOrders = async () => {
  try {
    const userInfo = uni.getStorageSync('userInfo')
    if (!userInfo) {
      uni.showToast({
        title: 'Please login first',
        icon: 'none'
      })
      return
    }

    const userId = JSON.parse(userInfo).id
    try {
      const response = await uni.request({
        url: `${API_BASE_URL}/user/book/${userId}`,
        method: 'GET',
        header: {
          'Content-Type': 'application/json'
        }
      })
      console.log(response);
      if (response.data.statusCode === 200) {
        let filteredOrders = response.data.data
        
        // 根据当前标签页过滤订单
        switch (currentTab.value) {
          case 'unpaid':
            filteredOrders = filteredOrders.filter(order => order.payed === 0)
            break
          case 'notstarted':
            filteredOrders = filteredOrders.filter(order => order.status === 1)
            break
          case 'ongoing':
            filteredOrders = filteredOrders.filter(order => order.status === 2)
            break
          case 'completed':
            filteredOrders = filteredOrders.filter(order => order.status === 3)
            break
        }
        orderList.value = filteredOrders
      } else {
        throw new Error('Failed to get order data')
      }
    } catch (error) {
      console.error('Failed to get order data, using mock data:', error)
      // 使用模拟数据作为后备
      let filteredOrders = mockOrders

      switch (currentTab.value) {
        case 'unpaid':
          filteredOrders = filteredOrders.filter(order => order.payed === 0)
          break
        case 'ongoing':
          filteredOrders = filteredOrders.filter(order => order.status === 2)
          break
        case 'completed':
          filteredOrders = filteredOrders.filter(order => order.status === 3)
          break
        case 'notstarted':
          filteredOrders = filteredOrders.filter(order => order.status === 1)
          break
      }
      orderList.value = filteredOrders
      
      uni.showToast({
        title: 'Failed to get orders, showing local data',
        icon: 'none'
      })
    }
  } catch (error) {
    console.error('Failed to get order list:', error)
    uni.showToast({
      title: 'Failed to get orders',
      icon: 'none'
    })
  }
}

// 切换选项卡
const switchTab = (type) => {
  currentTab.value = type
  fetchOrders()
}

// 获取状态文本
const getStatusText = (status, paid) => {
  const statusMap = {
    0: 'Unpaid',
    1: 'Pending',
    2: 'Ongoing',
    3: 'Completed',
    4: 'Cancelled'
  }
  const statusText = statusMap[status] || 'Unknown'
  // 如果状态是已取消，直接返回 Cancelled
  if (status === 4) {
    return 'Cancelled'
  }
  return paid === 0 ? `${statusText} (Unpaid)` : statusText
}

// 获取状态样式类
const getStatusClass = (status, paid) => {
  const classMap = {
    0: 'status-unpaid',
    1: 'status-notstarted',
    2: 'status-ongoing',
    3: 'status-completed',
    4: 'status-cancelled'
  }
  // 如果状态是已取消，直接返回 cancelled 样式
  if (status === 4) {
    return 'status-cancelled'
  }
  return paid === 0 ? 'status-unpaid' : classMap[status] || ''
}

// 格式化时间
const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
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

// 去支付
const goToPay = (order) => {
  console.log('Preparing to navigate to payment page, order ID:', order.id)
  uni.navigateTo({
    url: `/pages/pay/pay?id=${order.id}`,
    success: (res) => {
      console.log('Navigation successful, passed parameters:', { id: order.id })
    },
    fail: (err) => {
      console.error('Navigation failed:', err)
    }
  })
}

// 查看详情
const viewDetail = (orderId) => {
  uni.navigateTo({
    url: `/pages/order/detail?id=${orderId}`
  })
}

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

// 开发票
const openInvoice = () => {
  uni.showToast({
    title: 'Invoice feature in development',
    icon: 'none'
  })
}

// 还车跳转
const goReturn = (order) => {
  uni.navigateTo({
    url: `/pages/order/detail?id=${order.id}&return=1`,
    success: () => {
      uni.showToast({
        title: 'Please enter station code',
        icon: 'none',
        duration: 2000
      })
    }
  })
}

// 延长订单跳转
const goExtend = (order) => {
  uni.navigateTo({
    url: `/pages/order/detail?id=${order.id}&extend=1`,
    success: () => {
      uni.showToast({
        title: 'Please select extension time',
        icon: 'none',
        duration: 2000
      })
    }
  })
}

// 取消订单
const cancelOrder = async (order) => {
  uni.showModal({
    title: 'Cancel Order',
    content: 'Are you sure you want to cancel this order?',
    confirmText: 'Confirm',
    cancelText: 'Cancel',
    success: async (res) => {
      if (res.confirm) {
        try {
          const response = await uni.request({
            url: `${API_BASE_URL}/user/book/cancel/${order.id}`,
            method: 'DELETE',
            header: {
              'Content-Type': 'application/json'
            }
          })
          if (response.data.statusCode === 200) {
            uni.showToast({
              title: 'Order cancelled',
              icon: 'success'
            })
            // Refresh order list
            await fetchOrders()
          } else {
            throw new Error('Failed to cancel order')
          }
        } catch (error) {
          console.error('Failed to cancel order:', error)
          uni.showToast({
            title: 'Failed to cancel order',
            icon: 'none'
          })
        }
      }
    }
  })
}

onMounted(() => {
  fetchOrders()
  // Listen for refresh event
  uni.$on('refreshOrderList', () => {
    fetchOrders()
  })
})

onUnmounted(() => {
  // Remove event listener
  uni.$off('refreshOrderList')
})

// Add page load handling
onLoad((options) => {
  if (options.type) {
    currentTab.value = options.type
    fetchOrders()
  }
})
</script>

<template>
  <view class="container">
    <!-- 顶部导航栏 -->
    <view class="nav-bar">
      <view class="back-btn" @tap="goBack">
        <text class="iconfont icon-back">×</text>
      </view>
      <text class="title">My Orders</text>
      <text class="right-text" @tap="openInvoice">Invoice</text>
    </view>

    

    <!-- 订单类型选项卡 -->
    <view class="tab-bar">
      <view 
        class="tab-item" 
        v-for="tab in tabs" 
        :key="tab.type"
        :class="{ active: currentTab === tab.type }"
        @tap="switchTab(tab.type)"
      >
        <text>{{ tab.name }}</text>
      </view>
    </view>

    <!-- 订单列表 -->
    <view class="order-list">
      <view class="order-item" v-for="order in orderList" :key="order.id">
        <view class="order-header">
          <view class="left">
            <text class="icon">🛵</text>
            <text class="type">{{ order.scooter.station.name }}</text>
          </view>
          <text class="status" :class="getStatusClass(order.status, order.payed)">
            {{ getStatusText(order.status, order.payed) }}
          </text>
        </view>
        <view class="order-body">
          <view class="time-info">
            <text class="label">Duration:</text>
            <text class="value">{{ formatDuration(order.startTime, order.endTime) }}</text>
          </view>
          <view class="time-info">
            <text class="label">Order Time:</text>
            <text class="value">{{ formatTime(order.startTime) }}</text>
          </view>
        </view>
        <view class="order-footer">
          <text class="price">¥{{ order.totalCost }}</text>
          <view class="btn-group">
            <!-- 已取消订单只显示详情按钮 -->
            <template v-if="order.status === 4">
              <button class="detail-btn" @tap.stop="viewDetail(order.id)">Details</button>
            </template>
            
            <!-- 其他订单显示对应按钮 -->
            <template v-else>
              <!-- 未支付订单显示支付按钮 -->
              <button 
                v-if="order.payed === 0" 
                class="pay-btn" 
                @tap.stop="goToPay(order)"
              >Pay Now</button>
              
              <!-- 未开始订单显示取消按钮 -->
              <button 
                v-if="order.status === 1" 
                class="cancel-btn" 
                @tap.stop="cancelOrder(order)"
              >Cancel</button>
              
              <!-- 详情按钮 -->
              <button class="detail-btn" @tap.stop="viewDetail(order.id)">Details</button>
            </template>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<style>
.container {
  min-height: 100vh;
  background: #f7f8fa;
}

.nav-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 32rpx;
  background: #fff;
  position: sticky;
  top: 0;
  z-index: 100;
}

.back-btn {
  font-size: 40rpx;
  color: #333;
}

.title {
  font-size: 32rpx;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.right-text {
  font-size: 26rpx;
  color: #1890ff;
}

.tab-bar {
  display: flex;
  background: #fff;
  padding: 0 32rpx;
  border-top: 1rpx solid #f0f0f0;
  margin-top: 0;
}

.tab-item {
  font-size: 26rpx;
  font-weight: 500;
  padding: 24rpx 0;
  margin-right: 40rpx;
  position: relative;
  color: #666;
}

.tab-item.active {
  color: #1890ff;
  font-weight: 500;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4rpx;
  background: #1890ff;
  border-radius: 2rpx;
}

.order-list {
  padding: 24rpx;
}

.order-item {
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 24rpx;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.order-header .left {
  display: flex;
  align-items: center;
}

.order-header .icon {
  margin-right: 12rpx;
  font-size: 32rpx;
}

.order-header .type {
  font-size: 28rpx;
  font-weight: 600;
}

.status {
  font-size: 24rpx;
  font-weight: 500;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}

.status-unpaid {
  color: #faad14;
  background: #fffbe6;
}

.status-notstarted {
  color: #722ed1;
  background: #f9f0ff;
}

.status-ongoing {
  color: #1890ff;
  background: #e6f7ff;
}

.status-completed {
  color: #52c41a;
  background: #f6ffed;
}

.status-cancelled {
  color: #999;
  background: #f5f5f5;
}

.order-body {
  padding: 20rpx 0;
  border-top: 1rpx solid #f0f0f0;
  border-bottom: 1rpx solid #f0f0f0;
}

.time-info {
  display: flex;
  margin-bottom: 12rpx;
}

.time-info:last-child {
  margin-bottom: 0;
}

.time-info .label {
  font-size: 26rpx;
  color: #666;
  width: 140rpx;
}

.time-info .value {
  font-size: 26rpx;
  color: #333;
  font-weight: 500;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20rpx;
}

.price {
  font-size: 32rpx;
  font-weight: 600;
  color: #f5222d;
}

.btn-group {
  display: flex;
  gap: 12rpx;
  align-items: center;
  justify-content: flex-end;
}

.pay-btn {
  padding: 10rpx 28rpx;
  border-radius: 24rpx;
  font-size: 24rpx;
  font-weight: 500;
  color: #fff;
  background: #1890ff;
  border: none;
  min-width: 120rpx;
}

.pay-btn:active {
  opacity: 0.8;
}

.detail-btn {
  padding: 10rpx 28rpx;
  border-radius: 24rpx;
  font-size: 24rpx;
  font-weight: 500;
  color: #1890ff;
  background: #fff;
  border: 2rpx solid #1890ff;
  min-width: 120rpx;
}

.detail-btn:active {
  opacity: 0.8;
}

.cancel-btn {
  padding: 10rpx 28rpx;
  border-radius: 24rpx;
  font-size: 24rpx;
  font-weight: 500;
  color: #fff;
  background: #ff4d4f;
  border: none;
  min-width: 120rpx;
}

.cancel-btn:active {
  opacity: 0.8;
}

.map-container {
  width: 100%;
  height: 400rpx;
  margin-bottom: 0;
  box-shadow: none;
}

.map {
  width: 100%;
  height: 100%;
}
</style>