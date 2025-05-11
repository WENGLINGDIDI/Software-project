<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { API_BASE_URL } from '@/config/index'

const orderDetail = ref(null)
const mapState = ref({
  latitude: 30.657420,
  longitude: 104.065840,
  markers: []
})

// 获取订单详情
const fetchOrderDetail = async (orderId) => {
  try {
    // 模拟订单数据集合
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
            "name": "春熙路街道站",
            "latitude": 30.6586,
            "longitude": 104.0757
          }
        },
        "totalCost": 60.0,
        "status": 3,
        "payed": 1,
        "startTime": "2025-03-13T11:04:41.605087",
        "endTime": "2025-03-13T22:04:41.605087"
      },
      "2": {
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
            "name": "书院街街道站",
            "latitude": 30.6678,
            "longitude": 104.0873
          }
        },
        "totalCost": 80.0,
        "status": 0,
        "payed": 0,
        "startTime": "2025-04-13T09:00:00",
        "endTime": "2025-04-13T21:00:00"
      },
      "3": {
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
            "name": "双水碾街道站",
            "latitude": 30.7048,
            "longitude": 104.0731
          }
        },
        "totalCost": 100.0,
        "status": 1,
        "payed": 1,
        "startTime": "2025-04-12T14:00:00",
        "endTime": "2025-04-13T14:00:00"
      },
       "4": {
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
        "name": "天府广场站",
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
    }
    
    // 根据 orderId 获取对应的订单数据
    const mockDetail = mockOrders[orderId]
    if (!mockDetail) {
      throw new Error('订单不存在')
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

    // 实际API调用（后续替换）
    /*const response = await uni.request({
      url: `${API_BASE_URL}/order/${orderId}`,
      method: 'GET'
    })
    if (response.statusCode === 200) {
      orderDetail.value = response.data
      // 更新地图中心点和标记
      // ... 同上
    }*/
  } catch (error) {
    console.error('获取订单详情失败:', error)
    uni.showToast({
      title: '获取订单详情失败',
      icon: 'none'
    })
  }
}

onLoad((options) => {
  if (options.id) {
    fetchOrderDetail(options.id)
  }
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
    return `${minutes}分钟`
  }
  const hours = Math.floor(minutes / 60)
  const remainMinutes = minutes % 60
  return `${hours}小时${remainMinutes}分钟`
}

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

// 联系客服
const contactService = () => {
  uni.showToast({
    title: '正在接入客服...',
    icon: 'none'
  })
}
// 添加获取状态文本的函数
const getStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '进行中',
    2: '已取消',
    3: '已完成',
    4: '未开始'
  }
  return statusMap[status] || '未知状态'
}
// 报告问题
const reportIssue = () => {
  uni.showToast({
    title: '问题反馈功能开发中',
    icon: 'none'
  })
}

// 添加获取状态样式类函数
const getStatusClass = (status) => {
  const classMap = {
    0: 'status-unpaid',
    1: 'status-ongoing',
    2: 'status-cancelled',
    3: 'status-completed',
    4: 'status-notstarted'
  }
  return classMap[status] || ''
}

// 修改取消订单函数
const cancelOrder = () => {
  uni.showModal({
    title: '提示',
    content: '确定要取消该订单吗？',
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
              title: '订单已取消',
              icon: 'success'
            })
            // 延迟返回上一页
            setTimeout(() => {
              uni.navigateBack()
            }, 1500)
          } else {
            throw new Error('取消订单失败')
          }
        } catch (error) {
          console.error('取消订单失败:', error)
          uni.showToast({
            title: '取消订单失败',
            icon: 'none'
          })
        }
      }
    }
  })
}
</script>

<template>
  <view class="container">
    <!-- 顶部导航栏 -->
    <view class="nav-bar">
      <view class="back-btn" @tap="goBack">
        <text class="iconfont icon-back">×</text>
      </view>
      <text class="title">订单详情</text>
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
          <text class="label">订单编号</text>
          <text class="value">{{ orderDetail.id }}</text>
        </view>
        <view class="info-item">
          <text class="label">订单状态</text>
          <text class="value" :class="getStatusClass(orderDetail.status)">
            {{ getStatusText(orderDetail.status) }}
          </text>
        </view>
        <view class="info-item">
          <text class="label">站点名称</text>
          <text class="value">{{ orderDetail.scooter.station.name }}</text>
        </view>
        <view class="info-item">
          <text class="label">骑行时长</text>
          <text class="value">{{ formatDuration(orderDetail.startTime, orderDetail.endTime) }}</text>
        </view>
      </view>

      <!-- 时间信息 -->
      <view class="time-section">
        <view class="time-item">
          <text class="label">开始时间</text>
          <text class="value">{{ formatTime(orderDetail.startTime) }}</text>
        </view>
        <view class="time-item">
          <text class="label">结束时间</text>
          <text class="value">{{ formatTime(orderDetail.endTime) }}</text>
        </view>
      </view>

      <!-- 费用信息 -->
      <view class="cost-section">
        <view class="cost-item">
          <text class="label">骑行费用</text>
          <text class="value price">¥{{ orderDetail.totalCost }}</text>
        </view>
      </view>
    </view>

    <!-- 底部按钮 -->
    <view class="bottom-buttons">
      <!-- 待支付或未开始状态显示取消订单按钮 -->
      <template v-if="orderDetail.status === 0 || orderDetail.status === 4">
        <button class="cancel-btn" @tap="cancelOrder">取消订单</button>
        <button class="service-btn" @tap="contactService">联系客服</button>
      </template>
      <!-- 其他状态显示原有按钮 -->
      <template v-else>
        <button class="service-btn" @tap="contactService">联系客服</button>
        <button class="report-btn" @tap="reportIssue">报告问题</button>
      </template>
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
  font-size: 32rpx;
  font-weight: 500;
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
}
.status-ongoing { 
  color: #1890ff;
}
.status-cancelled { 
  color: #999;
}
.status-completed { 
  color: #52c41a;
}
.status-notstarted { 
  color: #722ed1;
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

.label {
  color: #666;
  font-size: 28rpx;
}

.value {
  color: #333;
  font-size: 28rpx;
}

.price {
  color: #f5222d;
  font-size: 32rpx;
  font-weight: 500;
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
  gap: 16rpx;
}

.service-btn,
.report-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  border-radius: 40rpx;
  font-size: 28rpx;
  margin: 0 12rpx;
}

.service-btn {
  background: #1890ff;
  color: #fff;
}

.report-btn {
  background: #fff;
  color: #1890ff;
  border: 2rpx solid #1890ff;
}

.service-btn:active,
.report-btn:active {
  opacity: 0.8;
}

.cancel-btn {
  flex: 1;
  height: 80rpx;
  line-height: 80rpx;
  text-align: center;
  border-radius: 40rpx;
  font-size: 28rpx;
  margin: 0 12rpx;
  background: #ff4d4f;
  color: #fff;
  border: none;
}

.cancel-btn:active {
  opacity: 0.8;
}
</style> 