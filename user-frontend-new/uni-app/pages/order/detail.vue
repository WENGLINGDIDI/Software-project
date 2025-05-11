<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { API_BASE_URL } from '@/config/index'

const orderDetail = ref(null)
const mapState = ref({
  latitude: 30.657420,
  longitude: 104.065840,
  markers: []
})
const orderId = ref(null)

// 获取订单详情
const fetchOrderDetail = async (orderId) => {
  try {
    // 首先尝试从本地存储获取用户ID
    const userInfoStr = uni.getStorageSync('userInfo')
    console.log('本地存储的用户信息字符串:', userInfoStr)
    const userInfo = JSON.parse(userInfoStr)
    console.log('解析后的用户信息对象:', userInfo)
    console.log('userInfo的类型:', typeof userInfo)
    console.log('userInfo的所有属性:', Object.keys(userInfo))
    console.log('userInfo.id的值:', userInfo.id)
    console.log('userInfo.id的类型:', typeof userInfo.id)
    
    if (!userInfo || userInfo.id === undefined || userInfo.id === null) {
      throw new Error('User not logged in')
    }
    console.log(orderId)

    // 尝试调用实际API
    const response = await uni.request({
      url: `${API_BASE_URL}/user/book/id/${orderId}`,
      method: 'GET',
      header: {
        'Content-Type': 'application/json'
      }
    })
    console.log('Order ID:', orderId)
    console.log('API response data:', response.data)
    
    if (response.statusCode === 200 && response.data.data) {
      orderDetail.value = response.data.data
      // 更新地图中心点和标记
      if (orderDetail.value?.scooter?.station) {
        const station = orderDetail.value.scooter.station
        mapState.value.latitude = station.latitude
        mapState.value.longitude = station.longitude
        mapState.value.markers = [{
          id: station.id,
          latitude: station.latitude,
          longitude: station.longitude,
          iconPath: '/static/icons/location.png',
          width: 40,
          height: 40,
          callout: {
            content: station.name,
            color: '#000000',
            fontSize: 14,
            borderRadius: 4,
            padding: 5,
            display: 'ALWAYS'
          }
        }]
      }
      return
    }
  } catch (error) {
    console.warn('API request failed, using mock data:', error)
    // 使用模拟数据
    const mockOrders = {
      "1": {
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
        "status": 3,
        "payed": 1,
        "startTime": "2025-03-13T11:04:41.605087",
        "endTime": "2025-03-13T22:04:41.605087"
      }
    }
    
    // 根据 orderId 获取对应的订单数据
    const mockDetail = mockOrders[orderId]
    if (!mockDetail) {
      throw new Error('Order does not exist')
    }
    
    orderDetail.value = mockDetail

    // 更新地图中心点和标记
    if (orderDetail.value?.scooter?.station) {
      const station = orderDetail.value.scooter.station
      mapState.value.latitude = station.latitude
      mapState.value.longitude = station.longitude
      mapState.value.markers = [{
        id: station.id,
        latitude: station.latitude,
        longitude: station.longitude,
        iconPath: '/static/icons/location.png',
        width: 40,
        height: 40,
        callout: {
          content: station.name,
          color: '#000000',
          fontSize: 14,
          borderRadius: 4,
          padding: 5,
          display: 'ALWAYS'
        }
      }]
    }
  }
}

onLoad((options) => {
  if (options.id) {
    fetchOrderDetail(options.id)
    // 如果是从还车按钮跳转来的，自动显示还车弹窗
    if (options.return === '1') {
      // 延迟显示弹窗，等待订单数据加载完成
      setTimeout(() => {
        showReturnDialog()
      }, 500)
    }
  }
})

onMounted(() => {
  if (orderId.value) {
    fetchOrderDetail(orderId.value)
  }
  // 监听刷新事件
  uni.$on('refreshOrderList', () => {
    if (orderId.value) {
      fetchOrderDetail(orderId.value)
    }
  })
})

onUnmounted(() => {
  // 移除事件监听
  uni.$off('refreshOrderList')
})

// 添加格式化时间的函数
const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  return `${date.getFullYear()}.${String(date.getMonth() + 1).padStart(2, '0')}.${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

// 添加计算骑行时长的函数
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

// 返回上一页
const goBack = () => {
  uni.navigateBack({
    delta: 1
  })
}

// 联系客服
const contactService = () => {
  uni.showToast({
    title: 'Connecting to service...',
    icon: 'none'
  })
}

// 报告问题
const reportIssue = () => {
  // 跳转到反馈页面
  uni.navigateTo({
    url: `/pages/feedback/feedback?orderId=${orderDetail.value.id}&scooterId=${orderDetail.value.scooter.id}`,
    success: () => {
      console.log('跳转到反馈页面成功')
    },
    fail: (err) => {
      console.error('跳转失败:', err)
      uni.showToast({
        title: '跳转失败',
        icon: 'none'
      })
    }
  })
}

// 修改获取状态文本的函数
const getStatusText = (status, paid) => {
  const statusMap = {
    0: 'Unpaid',
    1: 'Not Started',
    2: 'Ongoing',
    3: 'Completed',
    4: 'Cancelled'
  }
  const statusText = statusMap[status] || 'Unknown'
  // 如果状态是已取消，直接返回取消状态，不显示未支付
  if (status === 4) {
    return statusText
  }
  return paid === 0 ? `${statusText} (Unpaid)` : statusText
}

// 修改获取状态样式类函数
const getStatusClass = (status, paid) => {
  const classMap = {
    0: 'status-unpaid',
    1: 'status-notstarted',
    2: 'status-ongoing',
    3: 'status-completed',
    4: 'status-cancelled'
  }
  // 如果状态是已取消，直接返回取消状态的样式
  if (status === 4) {
    return classMap[status]
  }
  return paid === 0 ? 'status-unpaid' : classMap[status] || ''
}

// 修改取消订单函数
const cancelOrder = () => {
  uni.showModal({
    title: 'Cancel Order',
    content: 'Are you sure you want to cancel this order?',
    confirmText: 'Confirm',
    cancelText: 'Cancel',
    success: async (res) => {
      if (res.confirm) {
        try {
          const response = await uni.request({
            url: `${API_BASE_URL}/user/book/cancel/${orderDetail.value.id}`,
            method: 'DELETE'
          })

          if (response.statusCode === 200) {
            // 更新订单状态为已取消
            orderDetail.value = response.data.data
            uni.showToast({
              title: 'Order has been cancelled',
              icon: 'success'
            })
            // 延迟返回上一页
            setTimeout(() => {
              uni.navigateBack()
            }, 1500)
          } else {
            throw new Error('Error')
          }
        } catch (error) {
          console.error('Error:', error)
          uni.showToast({
            title: 'Error',
            icon: 'none'
          })
        }
      }
    }
  })
}

// 修改初始值为 false
const showReturnModal = ref(false)
const stationCode = ref('')

// 显示还车弹窗
const showReturnDialog = () => {
	console.log(1);
  showReturnModal.value = true
}

// 关闭还车弹窗
const closeReturnModal = () => {
  stationCode.value = ''
  showReturnModal.value = false
}

// 确认还车
const confirmReturn = async () => {
  if (!stationCode.value) {
    uni.showToast({
      title: 'Please enter station code',
      icon: 'none'
    })
    return
  }

  try {
    const response = await uni.request({
      url: `${API_BASE_URL}/user/book/complete`,
      method: 'POST',
      data: {
        bookId: orderDetail.value.id,
        newStationId: parseInt(stationCode.value)
      },
      header: {
        'Content-Type': 'application/json'
      }
    })

    if (response.data.statusCode === 200) {
      uni.showToast({
        title: 'Success',
        icon: 'success'
      })
      closeReturnModal()
      
      // 刷新当前订单详情
      await fetchOrderDetail(orderDetail.value.id)
      
      // 发送刷新事件
      uni.$emit('refreshOrderList')
      
      // 延迟返回上一页
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    } else {
      throw new Error('Return failed')
    }
  } catch (error) {
    console.error('Return failed:', error)
    uni.showToast({
      title: 'Return failed, please check station code',
      icon: 'none'
    })
  }
}

// 延长订单相关变量
const showExtendModal = ref(false)
const extendHours = ref(1)
const hourOptions = [1, 2, 4, 6, 8, 10, 12, 24]

// 显示延长订单弹窗
const showExtendDialog = () => {
  showExtendModal.value = true
}

// 关闭延长订单弹窗
const closeExtendModal = () => {
  extendHours.value = 1
  showExtendModal.value = false
}

// 确认延长订单
const confirmExtend = async () => {
  try {
    const response = await uni.request({
      url: `${API_BASE_URL}/user/book/extend`,
      method: 'POST',
      data: {
        bookId: orderDetail.value.id,
        extendHours: extendHours.value
      }
    })

    if (response.statusCode === 200) {
      uni.showToast({
        title: 'Success',
        icon: 'success'
      })
      closeExtendModal()
      
      // 刷新当前订单详情
      await fetchOrderDetail(orderDetail.value.id)
      
      // 发送刷新事件
      uni.$emit('refreshOrderList')
    } else {
      throw new Error('Extend order failed')
    }
  } catch (error) {
    console.error('Extend order failed:', error)
    uni.showToast({
      title: 'Extend order failed',
      icon: 'none'
    })
  }
}

// 去支付
const goToPay = () => {
  console.log('Preparing to navigate to payment page, order ID:', orderDetail.value.id)
  uni.navigateTo({
    url: `/pages/pay/pay?id=${orderDetail.value.id}`,
    success: (res) => {
      console.log('Navigation successful, passed parameters:', { id: orderDetail.value.id })
    },
    fail: (err) => {
      console.error('Navigation failed:', err)
    }
  })
}
</script>

<template>
  <view class="container">
    <!-- 顶部导航栏 -->
    <view class="nav-bar">
      <view class="back-btn" @tap.stop="goBack">
        <text class="iconfont icon-back">×</text>
      </view>
      <text class="title">Order Details</text>
    </view>

    <!-- 地图容器 -->
    <view class="map-container">
      <map
        id="map"
        class="map"
        :latitude="mapState.latitude"
        :longitude="mapState.longitude"
        :markers="mapState.markers"
        :scale="16"
        show-location
      ></map>
    </view>

    <!-- 订单信息卡片 -->
    <view class="order-card" v-if="orderDetail">
      <!-- 订单基本信息 -->
      <view class="info-section">
        <view class="info-item">
          <text class="label">Order ID</text>
          <text class="value">{{ orderDetail.id }}</text>
        </view>
        <view class="info-item">
          <text class="label">Status</text>
          <text class="value" :class="getStatusClass(orderDetail.status, orderDetail.payed)">
            {{ getStatusText(orderDetail.status, orderDetail.payed) }}
          </text>
        </view>
        <view class="info-item">
          <text class="label">Station</text>
          <text class="value">{{ orderDetail.scooter.station.name }}</text>
        </view>
        <view class="info-item">
          <text class="label">Duration</text>
          <text class="value">{{ formatDuration(orderDetail.startTime, orderDetail.endTime) }}</text>
        </view>
      </view>

      <!-- 时间信息 -->
      <view class="time-section">
        <view class="time-item">
          <text class="label">Start Time</text>
          <text class="value">{{ formatTime(orderDetail.startTime) }}</text>
        </view>
        <view class="time-item">
          <text class="label">End Time</text>
          <text class="value">{{ formatTime(orderDetail.endTime) }}</text>
        </view>
      </view>

      <!-- 费用信息 -->
      <view class="cost-section">
        <view class="cost-item">
          <text class="label">Cost</text>
          <text class="value price">¥{{ orderDetail.totalCost }}</text>
        </view>
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="bottom-buttons">
      <!-- 进行中订单显示还车和延长按钮 -->
      <template v-if="orderDetail?.status === 2">
        <button class="extend-btn" @tap="showExtendDialog">Extend</button>
        <button class="return-btn" @tap="showReturnDialog">Return</button>
      </template>
      
      <!-- 未支付且未取消的订单显示支付按钮 -->
      <template v-if="orderDetail?.payed === 0 && orderDetail?.status !== 4">
        <button class="pay-btn" @tap="goToPay">Pay Now</button>
      </template>
      
      <!-- 未开始订单显示取消按钮 -->
      <template v-if="orderDetail?.status === 1">
        <button class="cancel-btn" @tap="cancelOrder">Cancel</button>
      </template>
      
      <!-- 所有订单都显示反馈按钮 -->
      <button class="report-btn" @tap="reportIssue">Report</button>
    </view>

    <!-- 自定义弹窗 -->
    <view class="modal" v-if="showReturnModal">
      <view class="modal-mask" @tap="closeReturnModal"></view>
      <view class="return-modal">
        <view class="modal-title">Enter Station Code</view>
        <input 
          type="text" 
          v-model="stationCode" 
          placeholder="Enter station code"
          class="station-input"
        />
        <view class="modal-buttons">
          <button class="modal-btn cancel" @tap="closeReturnModal">Cancel</button>
          <button class="modal-btn confirm" @tap="confirmReturn">Confirm</button>
        </view>
      </view>
    </view>

    <!-- 添加延长订单弹窗 -->
    <view class="modal" v-if="showExtendModal">
      <view class="modal-mask" @tap="closeExtendModal"></view>
      <view class="extend-modal">
        <view class="modal-title">Select Extension Time</view>
        <view class="hours-options">
          <view 
            v-for="hours in hourOptions" 
            :key="hours"
            class="hours-item"
            :class="{ active: extendHours === hours }"
            @tap="extendHours = hours"
          >
            <text>{{ hours }}h</text>
          </view>
        </view>
        <view class="modal-buttons">
          <button class="modal-btn cancel" @tap="closeExtendModal">Cancel</button>
          <button class="modal-btn confirm" @tap="confirmExtend">Confirm</button>
        </view>
      </view>
    </view>
  </view>
</template>

<style>
.container {
  min-height: 100vh;
  background: #f7f8fa;
  padding-bottom: 120rpx;
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
  font-size: 36rpx;
  font-weight: 600;
  letter-spacing: 1px;
  margin-right: 40rpx;
}

.map-container {
  width: 100%;
  height: 400rpx;
  margin-bottom: 24rpx;
}

.map {
  width: 100%;
  height: 100%;
}

.order-card {
  background: #fff;
  border-radius: 16rpx;
  margin: 24rpx;
  padding: 24rpx;
}

.info-section,
.time-section,
.cost-section {
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}
.status-unpaid {
  color: #faad14;
  background: #fffbe6;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}
.status-notstarted {
  color: #722ed1;
  background: #f9f0ff;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}
.status-ongoing {
  color: #1890ff;
  background: #e6f7ff;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}
.status-completed {
  color: #52c41a;
  background: #f6ffed;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}
.status-cancelled {
  color: #999;
  background: #f5f5f5;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}
.cost-section {
  border-bottom: none;
}

.info-item,
.time-item,
.cost-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16rpx;
}

.info-item:last-child,
.time-item:last-child {
  margin-bottom: 0;
}

.info-item .label {
  font-size: 30rpx;
  color: #666;
  font-weight: 500;
}

.info-item .value {
  font-size: 30rpx;
  color: #333;
  font-weight: 600;
}

.time-item .label {
  font-size: 30rpx;
  color: #666;
  font-weight: 500;
}

.time-item .value {
  font-size: 30rpx;
  color: #333;
  font-weight: 500;
}

.cost-item .label {
  font-size: 32rpx;
  font-weight: 500;
}

.price {
  font-size: 40rpx;
  font-weight: 600;
}

.bottom-buttons {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  padding: 24rpx;
  background: #fff;
  box-shadow: 0 -2rpx 8rpx rgba(0,0,0,0.05);
  gap: 12rpx;
}

.pay-btn,
.extend-btn,
.return-btn,
.cancel-btn,
.report-btn {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  border-radius: 40rpx;
  font-size: 30rpx;
  font-weight: 500;
  min-width: 120rpx;
}

.pay-btn {
  background: #1890ff;
  color: #fff;
  border: none;
}

.extend-btn {
  background: #722ed1;
  color: #fff;
  border: none;
}

.return-btn {
  background: #52c41a;
  color: #fff;
  border: none;
}

.cancel-btn {
  background: #ff4d4f;
  color: #fff;
  border: none;
}

.report-btn {
  background: #fff;
  color: #1890ff;
  border: 2rpx solid #1890ff;
}

.pay-btn:active,
.extend-btn:active,
.return-btn:active,
.cancel-btn:active,
.report-btn:active {
  opacity: 0.8;
}

/* 添加还车按钮样式 */
.return-btn {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  border-radius: 40rpx;
  font-size: 30rpx;
  font-weight: 500;
  margin: 0 12rpx;
  background: #52c41a;
  color: #fff;
  border: none;
}

.return-btn:active {
  opacity: 0.8;
}

/* 还车弹窗样式 */
.return-modal {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  width: 560rpx;
}

.modal-title {
  font-size: 34rpx;
  font-weight: 600;
  color: #333;
  text-align: center;
  margin-bottom: 36rpx;
}

.station-input {
  width: 100%;
  height: 92rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 30rpx;
  margin-bottom: 32rpx;
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
  background: #52c41a;
  color: #fff;
}

.modal-btn:active {
  opacity: 0.8;
}

/* 添加遮罩层和弹窗容器样式 */
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

.return-modal {
  position: relative;
  z-index: 1000;
}

.modal-btn.cancel {
  background: #f5f5f5;
  color: #666;
}

.modal-btn.confirm {
  background: #52c41a;
  color: #fff;
}

.modal-btn:active {
  opacity: 0.8;
}

/* 延长按钮样式 */
.extend-btn {
  flex: 1;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  border-radius: 40rpx;
  font-size: 30rpx;
  font-weight: 500;
  margin: 0 12rpx;
  background: #722ed1;
  color: #fff;
  border: none;
}

.extend-btn:active {
  opacity: 0.8;
}

/* 延长订单弹窗样式 */
.extend-modal {
  background: #fff;
  border-radius: 16rpx;
  padding: 32rpx;
  width: 600rpx;
}

.hours-options {
  display: flex;
  flex-wrap: wrap;
  gap: 20rpx;
  margin-bottom: 32rpx;
}

.hours-item {
  width: calc(25% - 15rpx);
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 12rpx;
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
}

.hours-item.active {
  background: #722ed1;
  color: #fff;
}

.hours-item:active {
  opacity: 0.8;
}

/* 确保弹窗内容在遮罩层之上 */
.extend-modal {
  position: relative;
  z-index: 1000;
}
</style> 