<template>
  <view class="container">
    <!-- 顶部用户信息 -->
    <view class="user-header">
      <view class="user-info" v-if="isLogin">
        <image class="avatar" :src="userInfo.avatar || '/static/images/husky-avatar.png'" mode="aspectFill"></image>
        <view class="info">
          <text class="name">{{userInfo.name || userInfo.email.split('@')[0]}}</text>
          
        </view>
      </view>
      <view class="user-info" v-else @tap="goLogin">
        <image class="avatar" src="/static/images/husky-avatar.png" mode="aspectFill"></image>
        <view class="info">
          <text class="login-text">点击登录/注册</text>
        </view>
      </view>
      
      <!-- 顶部功能按钮 -->
      <view class="header-btns">
        <view class="btn-item" @tap="goService">
          <view class="icon-wrapper">
            <image src="/static/icons/headset.png" mode="aspectFit" class="icon-img"></image>
          </view>
          <text>客服</text>
        </view>
        <view class="btn-item" @tap="goMessage">
          <view class="icon-wrapper">
            <image src="/static/icons/message.png" mode="aspectFit" class="icon-img"></image>
          </view>
          <text>消息</text>
        </view>
        <view class="btn-item" @tap="goSetting">
          <view class="icon-wrapper">
            <image src="/static/icons/setting.png" mode="aspectFit" class="icon-img"></image>
          </view>
          <text>设置</text>
        </view>
      </view>
    </view>

    <!-- 订单卡片 -->
   <!-- 订单卡片 -->
   <view class="order-card">
      <view class="order-types">
        <view class="type-item" @tap="goOrderList('all')">
          <view class="icon-wrapper">
            <image src="/static/icons/order-all.png" mode="aspectFit" class="icon-img"></image>
          </view>
          <text>全部订单</text>
        </view>
        <view class="type-item" @tap="goOrderList('unpaid')">
          <view class="icon-wrapper">
            <image src="/static/icons/order-unpaid.png" mode="aspectFit" class="icon-img"></image>
          </view>
          <text>待支付</text>
        </view>
        <view class="type-item" @tap="goOrderList('notstarted')">
          <view class="icon-wrapper">
            <image src="/static/icons/notstart.png" mode="aspectFit" class="icon-img"></image>
          </view>
          <text>未开始</text>
        </view>
        <view class="type-item" @tap="goOrderList('ongoing')">
          <view class="icon-wrapper">
            <image src="/static/icons/order-ongoing.png" mode="aspectFit" class="icon-img"></image>
          </view>
          <text>进行中</text>
        </view>
        <view class="type-item" @tap="goOrderList('completed')">
          <view class="icon-wrapper">
            <image src="/static/icons/order-completed.png" mode="aspectFit" class="icon-img"></image>
          </view>
          <text>已完成</text>
        </view>
      </view>
    </view>
<!-- 在订单卡片和出行数据之间添加银行卡区域 -->
  <view class="card-section">
    <view class="card-item" @tap="goCard">
      <view class="card-left">
        <image src="/static/icons/card.png" mode="aspectFit" class="card-icon"></image>
        <text class="card-title">我的银行卡</text>
      </view>
      <view class="card-right">
        <text class="card-desc">管理支付方式</text>
        <text class="iconfont icon-right">></text>
      </view>
    </view>
  </view>
    <!-- 骑行数据 -->
    <view class="stats-card">
      <text class="stats-title">出行数据</text>
      <view class="stats-list">
        <view class="stats-item">
          <text class="value">{{rideData.totalDistance || '-'}}</text>
          <text class="unit">km</text>
          <text class="label">总里程</text>
        </view>
        <view class="stats-item">
          <text class="value">{{rideData.totalRides || '-'}}</text>
          <text class="unit">次</text>
          <text class="label">出行次数</text>
        </view>
        <view class="stats-item">
          <text class="value">{{rideData.cardMiles || '-'}}</text>
          <text class="unit">千卡</text>
          <text class="label">消耗卡路里</text>
        </view>
        <view class="stats-item">
          <text class="value">{{rideData.carbonSaved || '-'}}</text>
          <text class="unit">kg</text>
          <text class="label">减少碳排放</text>
        </view>
      </view>
    </view>

    <!-- 退出按钮 -->
    <view class="logout-section" v-if="isLogin">
      <button class="logout-btn" @tap="handleLogout">退出登录</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'

// 用户信息
const userInfo = ref(null)
const isLogin = computed(() => !!userInfo.value)

// 骑行数据
const rideData = ref({
  totalDistance: 0,
  totalRides: 0,
  cardMiles: 0,
  carbonSaved: 0
})

// 初始化
onMounted(() => {
  checkLogin()
})

// 页面显示时检查登录状态
onShow(() => {
  checkLogin()
})

// 检查登录状态
const checkLogin = () => {
  try {
    const data = uni.getStorageSync('userInfo')
    if (data) {
      userInfo.value = JSON.parse(data)
      getRideData()
    }
  } catch (err) {
    console.error('获取用户信息失败:', err)
  }
}

// 获取骑行数据
const getRideData = () => {
  // TODO: 调用后端接口获取骑行数据
  rideData.value = {
    totalDistance: 0,
    totalRides: 0,
    cardMiles: 0,
    carbonSaved: 0
  }
}

// 格式化手机号/邮箱
const formatPhone = (email) => {
  if (!email) return ''
  return email.replace(/(.{3}).*(@.*)/, '$1***$2')
}

// 页面跳转
const goLogin = () => {
  uni.navigateTo({ url: '/pages/login/login' })
}

const goService = () => {
  uni.showToast({
    title: '正在接入客服',
    icon: 'none'
  })
}

const goMessage = () => {
  if (!isLogin.value) {
    return goLogin()
  }
  uni.navigateTo({ url: '/pages/message/message' })
}

const goSetting = () => {
  if (!isLogin.value) {
    return goLogin()
  }
  uni.navigateTo({ url: '/pages/setting/setting' })
}

const goAllOrders = () => {
  if (!isLogin.value) {
    return goLogin()
  }
  goOrderList('all')
}

const goOrderList = (type) => {
  if (!isLogin.value) {
    return goLogin()
  }
  uni.navigateTo({ url: `/pages/order/order?type=${type}` })
}

const goCard = () => {
  if (!isLogin.value) {
    return goLogin()
  }
  uni.navigateTo({ url: '/pages/card/card' })
}

// 退出登录
const handleLogout = () => {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        uni.removeStorageSync('userInfo')
        userInfo.value = null
        uni.showToast({
          title: '已退出登录',
          icon: 'success'
        })
      }
    }
  })
}

// 修改选项卡配置
const tabs = [
  { type: 'all', name: '全部' },
  { type: 'unpaid', name: '待支付' },
  { type: 'notstarted', name: '未开始' },
  { type: 'ongoing', name: '进行中' },
  { type: 'completed', name: '已完成' }
]

// 修改状态文本函数
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

// 修改状态样式类函数
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

// 在 fetchOrders 函数中添加未开始状态的过滤
const fetchOrders = async () => {
  try {
    // ... 其他代码保持不变
    switch (currentTab.value) {
      case 'unpaid':
        filteredOrders = mockOrders.filter(order => order.status === 0)
        break
      case 'notstarted':
        filteredOrders = mockOrders.filter(order => order.status === 4)
        break
      case 'ongoing':
        filteredOrders = mockOrders.filter(order => order.status === 1)
        break
      case 'completed':
        filteredOrders = mockOrders.filter(order => order.status === 3)
        break
    }
    // ... 其他代码保持不变
  } catch (error) {
    // ... 错误处理
  }
}
</script>

<style>
page {
  background: linear-gradient(180deg, #e6f7ff 0%, #f7f8fa 100%);
}

.container {
  min-height: 100vh;
  padding-bottom: 120rpx;
  padding-top: 60rpx;
}

/* 用户信息区域 */
.user-header {
  background: #ffffff;
  padding: 40rpx 32rpx 20rpx;
  color: #333;
  position: relative;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.05);
  margin: 0 24rpx;
  border-radius: 24rpx;
}

.user-header::after {
  display: none;
}

.user-info {
  display: flex;
  align-items: center;
  position: relative;
}

.avatar {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  margin-right: 24rpx;
  border: 4rpx solid #fff;
}

.info {
  flex: 1;
}

.name {
  font-size: 32rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 4rpx;
}

.phone {
  font-size: 26rpx;
  color: #999;
}

.login-text {
  font-size: 32rpx;
  color: #333;
  font-weight: 500;
}

/* 顶部按钮 */
.header-btns {
  position: absolute;
  top: 40rpx;
  right: 32rpx;
  display: flex;
  gap: 40rpx;
  z-index: 2;
}

.btn-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.icon-wrapper {
  width: 64rpx;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 4rpx;
  transition: all 0.3s;
}

.btn-item:active .icon-wrapper {
  transform: scale(0.95);
}

.btn-item text {
  font-size: 24rpx;
  color: #666;
}

/* 订单卡片 */
.order-card {
  margin: 24rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 24rpx;
  padding: 32rpx 20rpx;
  box-shadow: 0 8rpx 24rpx rgba(24,144,255,0.1);
  position: relative;
  z-index: 2;
  backdrop-filter: blur(10px);
}

.order-types {
  display: flex;
  justify-content: space-around;
}

.type-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: all 0.3s;
}

.type-item:active {
  transform: scale(0.95);
}

.type-item .icon-wrapper {
  width: 80rpx;
  height: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
  color: #666;
  transition: all 0.3s;
}

.type-item:active .icon-wrapper {
  color: #333;
}

.type-item text {
  font-size: 26rpx;
  color: #333;
}

/* 骑行数据 */
.stats-card {
  margin: 24rpx;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 24rpx;
  padding: 32rpx;
  box-shadow: 0 8rpx 24rpx rgba(24,144,255,0.1);
  backdrop-filter: blur(10px);
}

.stats-title {
  font-size: 30rpx;
  font-weight: 500;
  color: #333;
  margin-bottom: 32rpx;
  position: relative;
  padding-left: 24rpx;
}

.stats-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 6rpx;
  height: 28rpx;
  background: #1890ff;
  border-radius: 6rpx;
}

.stats-list {
  display: flex;
  justify-content: space-between;
}

.stats-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}

.stats-item::after {
  content: '';
  position: absolute;
  right: -25%;
  top: 20%;
  bottom: 20%;
  width: 1rpx;
  background: #eee;
}

.stats-item:last-child::after {
  display: none;
}

.stats-item .value {
  font-size: 32rpx;
  font-weight: 500;
  color: #333;
  line-height: 1;
}

.stats-item .unit {
  font-size: 24rpx;
  color: #666;
  margin: 4rpx 0 8rpx;
}

.stats-item .label {
  font-size: 24rpx;
  color: #999;
}

/* 底部导航栏 */
.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background: rgba(255,255,255,0.98);
  display: flex;
  justify-content: space-around;
  align-items: center;
  border-top: 1rpx solid rgba(0,0,0,0.05);
  padding-bottom: env(safe-area-inset-bottom);
  backdrop-filter: blur(10px);
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
  font-size: 20rpx;
  padding: 10rpx 0;
  transition: all 0.3s;
}

.tab-item:active {
  transform: scale(0.95);
}

.tab-item .iconfont {
  font-size: 40rpx;
  margin-bottom: 4rpx;
}

.tab-item.active {
  color: #1890ff;
  font-weight: 500;
}

.tab-item.active .iconfont {
  text-shadow: none;
}

/* 字体图标 */
@font-face {
  font-family: 'iconfont';
  src: url('//at.alicdn.com/t/c/font_4444357_sl63e69kjh.woff2?t=1710141245') format('woff2'),
       url('//at.alicdn.com/t/c/font_4444357_sl63e69kjh.woff?t=1710141245') format('woff'),
       url('//at.alicdn.com/t/c/font_4444357_sl63e69kjh.ttf?t=1710141245') format('truetype');
}

.iconfont {
  font-family: "iconfont" !important;
  font-size: 32rpx;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: currentColor;
}

.icon-order:before { content: "\e6df"; }
.icon-wallet:before { content: "\e6b1"; }
.icon-ongoing:before { content: "\e6e2"; }
.icon-check:before { content: "\e6b2"; }
.icon-home:before { content: "\e6b8"; }
.icon-car:before { content: "\e6b9"; }
.icon-ride:before { content: "\e6ba"; }
.icon-user:before { content: "\e6bb"; }
.icon-headset:before { content: "\e6bc"; }
.icon-message:before { content: "\e6bd"; }
.icon-setting:before { content: "\e6be"; }

.icon-img {
  width: 48rpx;
  height: 48rpx;
}

.type-item .icon-img {
  width: 56rpx;
  height: 56rpx;
}

/* 退出按钮样式 */
.logout-section {
  margin: 60rpx 24rpx;
}

.logout-btn {
  width: 100%;
  height: 90rpx;
  background: rgba(255, 255, 255, 0.9);
  border: 2rpx solid #ff4d4f;
  border-radius: 45rpx;
  color: #ff4d4f;
  font-size: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
  backdrop-filter: blur(10px);
}

.logout-btn:active {
  background: rgba(255, 77, 79, 0.05);
  transform: scale(0.98);
}

.status-notstarted { 
  color: #722ed1;
}

/* 在 style 标签内添加以下样式 */
.card-section {
  margin: 24rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 0 32rpx;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.05);
}

.card-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 32rpx 0;
  position: relative;
}

.card-left {
  display: flex;
  align-items: center;
  gap: 20rpx;
}

.card-icon {
  width: 48rpx;
  height: 48rpx;
}

.card-title {
  font-size: 30rpx;
  color: #333;
  font-weight: 500;
}

.card-right {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.card-desc {
  font-size: 26rpx;
  color: #999;
}

.icon-right {
  font-size: 24rpx;
  color: #999;
}
</style> 