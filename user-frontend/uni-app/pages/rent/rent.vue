<template>
  <view class="rent-container">
    <!-- 顶部导航 -->
    <view class="nav-bar">
      <view class="back-btn" @tap="goBack">
        <text class="iconfont icon-back">×</text>
      </view>
      <text class="title">确认订单页</text>
    </view>

    <!-- 车辆信息卡片 -->
    <view class="scooter-card">
      <view class="scooter-content">
        <image class="scooter-image" src="/static/images/car1.jpeg" mode="aspectFit"></image>
        <view class="scooter-details">
          <text class="scooter-name">通勤长续航-可换电</text>
          <view class="features">
            <text class="feature-tag">含电池</text>
            <text class="feature-tag">含手机支架</text>
          </view>
          <view class="free-delivery">免费送车上门</view>
        </view>
      </view>
    </view>

    <!-- 租赁方案选择 -->
    <view class="rent-options">
      <text class="section-title">选择租车方案</text>
      <view class="options-grid">
        <view 
          v-for="option in rentOptions" 
          :key="option.type"
          class="option-item"
          :class="{'selected': selectedOption === option.type}"
          @tap="selectOption(option)"
        >
          <text class="option-type">{{option.type}}</text>
          <text class="option-price">¥{{option.price}}</text>
        </view>
      </view>
    </view>

    <!-- 租期选择 -->
    <view class="duration-selector">
      <text class="section-title">租车时长</text>
      <view class="duration-control">
        <text>共<text class="total-days">{{totalDays}}</text>{{selectedUnit}}</text>
        <view class="control-buttons">
          <button class="control-btn" @tap="adjustDuration(-1)">-</button>
          <text class="duration-value">{{durationText}}</text>
          <button class="control-btn" @tap="adjustDuration(1)">+</button>
        </view>
      </view>
    </view>

    <!-- 取还信息 -->
    <view class="delivery-info">
      <view class="info-item" @tap="selectPickupTime">
        <text class="label">取车时间</text>
        <view class="value-wrapper">
          <text class="value">{{pickupTime}}</text>
          <text class="hint">取车后开始计算订单时长</text>
          <text class="arrow">></text>
        </view>
      </view>
      <view class="info-item" @tap="selectReturnTime">
        <text class="label">还车时间</text>
        <view class="value-wrapper">
          <text class="value">{{returnTime}}</text>
          <text class="arrow">></text>
        </view>
      </view>
      <view class="delivery-address">
        <text class="address-label">【众鑫】全城免费配送-群星路站</text>
      </view>
    </view>

    <!-- 底部结算栏 -->
    <view class="bottom-bar">
      <view class="agreement">
        <checkbox-group @change="onAgreeChange">
          <checkbox :checked="isAgree" color="#1890ff" />
        </checkbox-group>
        <text class="agreement-text">已阅读并同意</text>
        <text class="agreement-link" @tap="showAgreement">《哈啰电动车租赁用户协议》</text>
      </view>
      <view class="price-section">
        <text class="total-price">¥{{totalPrice}}</text>
        <text class="price-detail" @tap="showPriceDetail">明细 ></text>
      </view>
      <button 
        class="submit-btn" 
        :disabled="!isAgree"
        @tap="submitOrder"
      >去支付</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'

// 租赁选项
const rentOptions = ref([
  { type: '1小时', price: 15, unit: '小时' },
  { type: '4小时', price: 45, unit: '小时' },
  { type: '1天', price: 59, unit: '天' },
  { type: '1周', price: 179, unit: '周' }
])

const selectedOption = ref('1天')
const duration = ref(1)
const isAgree = ref(false)
const pickupTime = ref('2025年04月07日 明天 09:00')

// 计算还车时间
const returnTime = computed(() => {
  const option = rentOptions.value.find(opt => opt.type === selectedOption.value)
  if (!option) return '2025年04月07日 明天 09:00'
  
  const pickupDate = new Date('2025-04-07 09:00:00')
  let returnDate = new Date(pickupDate)
  
  if (option.unit === '小时') {
    const hours = option.type.replace(/[^0-9]/g, '')
    returnDate.setHours(returnDate.getHours() + (duration.value * hours))
  } else if (option.unit === '天') {
    returnDate.setDate(returnDate.getDate() + duration.value)
  } else if (option.unit === '周') {
    returnDate.setDate(returnDate.getDate() + (duration.value * 7))
  }
  
  const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']
  const weekday = weekdays[returnDate.getDay()]
  const year = returnDate.getFullYear()
  const month = returnDate.getMonth() + 1
  const day = returnDate.getDate()
  const hours = returnDate.getHours().toString().padStart(2, '0')
  const minutes = returnDate.getMinutes().toString().padStart(2, '0')
  
  return `${year}年${month}月${day}日 ${weekday} ${hours}:${minutes}`
})

// 获取当前选择的单位
const selectedUnit = computed(() => {
  const option = rentOptions.value.find(opt => opt.type === selectedOption.value)
  return option ? option.unit : '天'
})

// 计算显示的时长文本
const durationText = computed(() => {
  const option = rentOptions.value.find(opt => opt.type === selectedOption.value)
  if (!option) return '1天'
  
  // 如果是小时方案，显示实际小时数
  if (option.unit === '小时') {
    const hours = option.type.replace(/[^0-9]/g, '')
    return `${duration.value * hours}小时`
  }
  
  return `${duration.value}${option.unit}`
})

// 计算共计天数
const totalDays = computed(() => {
  const option = rentOptions.value.find(opt => opt.type === selectedOption.value)
  if (!option) return 1
  
  if (option.unit === '小时') {
    const hours = option.type.replace(/[^0-9]/g, '')
    return duration.value * hours
  } else if (option.unit === '天') {
    return duration.value
  } else if (option.unit === '周') {
    return duration.value * 7
  }
  return 1
})

// 计算总价
const totalPrice = computed(() => {
  const option = rentOptions.value.find(opt => opt.type === selectedOption.value)
  return option ? option.price * duration.value : 0
})

// 选择租赁选项
const selectOption = (option) => {
  selectedOption.value = option.type
  duration.value = 1 // 重置时长
}

// 调整租期
const adjustDuration = (delta) => {
  const newDuration = duration.value + delta
  if (newDuration >= 1 && newDuration <= 12) {
    duration.value = newDuration
  }
}

// 同意协议
const onAgreeChange = (e) => {
  isAgree.value = e.detail.value.length > 0
}

// 选择取车时间
const selectPickupTime = () => {
  // TODO: 实现时间选择器
}

// 选择还车时间
const selectReturnTime = () => {
  // TODO: 实现时间选择器
}

// 显示协议
const showAgreement = () => {
  uni.navigateTo({
    url: '/pages/agreement/agreement'
  })
}

// 显示价格明细
const showPriceDetail = () => {
  uni.showModal({
    title: '价格明细',
    content: `基础租金: ¥${totalPrice.value}\n配送费用: ¥0`,
    showCancel: false
  })
}

// 提交订单
const submitOrder = () => {
  if (!isAgree.value) return
  
  // 跳转到支付页面
  uni.navigateTo({
    url: `/pages/pay/pay?amount=${totalPrice.value}&duration=${durationText.value}&pickupTime=${pickupTime.value}&returnTime=${returnTime.value}`
  })
}

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}
</script>

<style>
page {
  background: linear-gradient(180deg, #e6f7ff 0%, #f0f9ff 50%, #f7f8fa 100%);
}

.rent-container {
  min-height: 100vh;
  padding-bottom: 180rpx;
}

/* 导航栏 */
.nav-bar {
  padding: 40rpx 32rpx;
  display: flex;
  align-items: center;
  background: transparent;
  backdrop-filter: blur(10px);
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

/* 车辆信息卡片 */
.scooter-card {
  margin: 24rpx;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  border-radius: 24rpx;
  padding: 24rpx;
}

.scooter-content {
  display: flex;
  align-items: flex-start;
  gap: 24rpx;
}

.scooter-image {
  width: 180rpx;
  height: 180rpx;
  object-fit: contain;
}

.scooter-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.scooter-name {
  font-size: 32rpx;
  font-weight: 500;
}

.features {
  display: flex;
  gap: 16rpx;
}

.feature-tag {
  font-size: 24rpx;
  color: #666;
  background: #f5f5f5;
  padding: 4rpx 16rpx;
  border-radius: 12rpx;
}

.free-delivery {
  font-size: 24rpx;
  color: #ff4d4f;
  background: #fff1f0;
  padding: 8rpx 16rpx;
  border-radius: 12rpx;
  display: inline-block;
  align-self: flex-start;
}

/* 租赁选项 */
.rent-options {
  margin: 24rpx;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  border-radius: 24rpx;
  padding: 24rpx;
}

.section-title {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 24rpx;
}

.options-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
}

.option-item {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16rpx;
  padding: 20rpx 12rpx;
  text-align: center;
  transition: all 0.3s;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.option-item.selected {
  background: #e6f7ff;
  border: 2rpx solid #1890ff;
  transform: translateY(-2rpx);
  box-shadow: 0 4rpx 12rpx rgba(24, 144, 255, 0.15);
}

.option-type {
  font-size: 26rpx;
  color: #333;
  display: block;
  margin-bottom: 8rpx;
}

.option-price {
  font-size: 30rpx;
  color: #ff4d4f;
  font-weight: bold;
}

/* 租期选择器 */
.duration-selector {
  margin: 24rpx;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  border-radius: 24rpx;
  padding: 24rpx;
}

.duration-control {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.duration-control text {
  font-size: 28rpx;
  color: #666;
}

.duration-control text.total-days {
  color: #333;
}

.control-buttons {
  display: flex;
  align-items: center;
  gap: 24rpx;
}

.control-btn {
  width: 60rpx;
  height: 60rpx;
  border-radius: 30rpx;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36rpx;
  color: #666;
}

.duration-value {
  font-size: 32rpx;
  color: #333;
  min-width: 80rpx;
  text-align: center;
}

/* 取还信息 */
.delivery-info {
  margin: 24rpx;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  border-radius: 24rpx;
  padding: 24rpx;
}

.info-item {
  padding: 24rpx 0;
  border-bottom: 2rpx solid #f5f5f5;
}

.label {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 16rpx;
  display: block;
}

.value-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.value {
  font-size: 28rpx;
  color: #333;
}

.hint {
  font-size: 24rpx;
  color: #999;
  margin-left: 16rpx;
}

.arrow {
  font-size: 24rpx;
  color: #999;
}

.delivery-address {
  padding-top: 24rpx;
}

.address-label {
  font-size: 28rpx;
  color: #333;
}

/* 底部栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 24rpx;
  box-shadow: 0 -4rpx 12rpx rgba(0,0,0,0.05);
}

.agreement {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
}

.agreement-text {
  font-size: 24rpx;
  color: #666;
  margin-left: 8rpx;
}

.agreement-link {
  font-size: 24rpx;
  color: #1890ff;
}

.price-section {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
}

.total-price {
  font-size: 40rpx;
  color: #ff4d4f;
  font-weight: bold;
  margin-right: 16rpx;
}

.price-detail {
  font-size: 24rpx;
  color: #666;
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

.submit-btn[disabled] {
  background: #d9d9d9;
}

.scooter-card, .rent-options, .duration-selector, .delivery-info {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.nav-bar {
  background: transparent;
  backdrop-filter: blur(10px);
}

.bottom-bar {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}
</style> 