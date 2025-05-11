<template>
  <view class="rent-container">
    <!-- 顶部导航 -->
    <view class="nav-bar">
      <view class="back-btn" @tap="goBack">
        <text class="iconfont icon-back">×</text>
      </view>
      <text class="title">Confirm Order</text>
    </view>

    <!-- 车辆信息卡片 -->
    <view class="scooter-card">
      <view class="scooter-content">
        <image class="scooter-image" src="/static/images/car1.jpeg" mode="aspectFit"></image>
        <view class="scooter-details">
          <text class="scooter-name">Long-range Commuting-Battery Swap</text>
          <view class="features">
            <text class="feature-tag">With Battery</text>
            <text class="feature-tag">With Phone Mount</text>
          </view>
          <view class="free-delivery">Free Delivery</view>
        </view>
      </view>
    </view>

    <!-- 在车辆信息卡片后添加选车部分 -->
    <view class="scooter-selector">
      <text class="section-title">Select Scooter</text>
      <scroll-view class="scooter-list" scroll-y>
        <view 
          v-for="scooter in availableScooters" 
          :key="scooter.id"
          class="scooter-item"
          :class="{'selected': selectedScooter?.id === scooter.id}"
          @tap="selectScooter(scooter)"
        >
          <view class="scooter-info">
            <view class="scooter-header">
              <text class="scooter-id">ID: {{scooter.id}}</text>
              <text :class="['scooter-status', getStatusClass(scooter.status)]">
                {{getStatusText(scooter.status)}}
              </text>
            </view>
            <view class="scooter-detail">
              <text class="config">{{scooter.config}}</text>
              <text class="power">Power: {{scooter.power}}%</text>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 租赁方案选择 -->
    <view class="rent-options">
      <text class="section-title">Select Rental Plan</text>
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

    <!-- 取还信息 -->
    <view class="delivery-info">
      <view class="info-item" @tap="selectPickupTime">
        <text class="label">Pickup Time</text>
        <view class="value-wrapper">
          <text class="value">{{pickupTime}}</text>
          <text class="hint">Order duration starts after pickup</text>
          <text class="arrow">></text>
        </view>
      </view>
      <view class="info-item" @tap="selectReturnTime">
        <text class="label">Return Time</text>
        <view class="value-wrapper">
          <text class="value">{{returnTimeText}}</text>
          <text class="arrow">></text>
        </view>
      </view>
    </view>

    <!-- 底部结算栏 -->
    <view class="bottom-bar">
      <view class="agreement">
        <checkbox-group @change="onAgreeChange">
          <checkbox :checked="isAgree" color="#1890ff" />
        </checkbox-group>
        <text class="agreement-text">I have read and agree to</text>
        <text class="agreement-link" @tap="showAgreement">《Scooter Rental Agreement》</text>
      </view>
      <view class="price-section">
        <text class="total-price">¥{{totalPrice}}</text>
        <text class="price-detail" @tap="showPriceDetail">Details ></text>
      </view>
      <button 
        class="submit-btn" 
        :disabled="!isAgree"
        @tap="submitOrder"
      >Place Order</button>
    </view>

    <!-- 修改选择器为弹窗形式 -->
    <uni-popup ref="pickerPopup" type="bottom">
      <view class="picker-content">
        <view class="picker-header">
          <text class="picker-cancel" @tap="closePicker">Cancel</text>
          <text class="picker-title">Select Time</text>
          <text class="picker-confirm" @tap="confirmPicker">Confirm</text>
        </view>
        <view class="picker-body">
          <view class="date-section">
            <text class="section-title">Date</text>
            <view class="date-grid">
              <view 
                v-for="(date, index) in availableDates" 
                :key="index"
                class="date-item"
                :class="{'selected': selectedDateIndex === index}"
                @tap="selectDate(index)"
              >
                <text class="date-day">{{date.day}}</text>
                <text class="date-weekday">{{date.weekday}}</text>
              </view>
            </view>
          </view>
          <view class="time-section">
            <text class="section-title">Time</text>
            <view class="time-grid">
              <view 
                v-for="(time, index) in availableTimes" 
                :key="index"
                class="time-item"
                :class="{'selected': selectedTimeIndex === index}"
                @tap="selectTime(index)"
              >
                <text>{{time}}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </uni-popup>
  </view>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { API_BASE_URL } from '@/config/index'

// 租赁选项
const rentOptions = ref([])
const selectedOption = ref('1 Day')
const duration = ref(1)
const isAgree = ref(false)
const pickupTime = ref('')
const returnTimeText = ref('')
const pickupDateTime = ref(new Date())
const hireOptionId = ref(1)
const pageData = ref(null)
const selectedScooter = ref(null)

// 替换选择器相关数据
const showPicker = ref(false)
const selectedDateIndex = ref(0)
const selectedTimeIndex = ref(0)
const availableDates = ref([])
const availableTimes = ref([])

// 修改选择器相关方法
const pickerPopup = ref(null)

// 获取租赁方案
const fetchRentOptions = async () => {
  try {
    const response = await uni.request({
      url: `${API_BASE_URL}/admin/hireOption`,
      method: 'GET',
      header: {
        'Content-Type': 'application/json'
      }
    })
    
    if (response.data.statusCode === 200) {
      rentOptions.value = response.data.data.map(option => {
        let type, unit
        if (option.hours === 24) {
          type = '1 Day'
          unit = 'day'
        } else if (option.hours === 168) {
          type = '1 Week'
          unit = 'week'
        } else {
          type = `${option.hours} Hours`
          unit = 'hour'
        }
        return {
          type,
          price: option.money,
          unit,
          id: option.id,
          hours: option.hours
        }
      })
      
      if (rentOptions.value.length > 0) {
        selectedOption.value = rentOptions.value[0].type
        hireOptionId.value = rentOptions.value[0].id
      }
    }
  } catch (error) {
    console.error('获取租赁方案失败:', error)
    uni.showToast({
      title: error.message || '获取租赁方案失败',
      icon: 'none'
    })
  }
}

// 计算还车时间
const updateReturnTime = () => {
  if (!pickupDateTime.value) return
  
  const returnDate = new Date(pickupDateTime.value)
  const option = rentOptions.value.find(opt => opt.type === selectedOption.value)
  
  if (!option) return
  
  // 根据租赁选项的小时数计算还车时间
  returnDate.setHours(returnDate.getHours() + option.hours)
  
  const weekdays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
  const weekday = weekdays[returnDate.getDay()]
  const month = String(returnDate.getMonth() + 1).padStart(2, '0')
  const day = String(returnDate.getDate()).padStart(2, '0')
  const returnHour = String(returnDate.getHours()).padStart(2, '0')
  
  returnTimeText.value = `${month}/${day} ${weekday} ${returnHour}:00`
}

// 获取可用的电动车列表
const availableScooters = computed(() => {
  if (!pageData.value?.scooters) return []
  return pageData.value.scooters.filter(scooter => scooter.status === 0)
})

// 选择电动车
const selectScooter = (scooter) => {
  selectedScooter.value = scooter
}

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: 'Available',
    1: 'In Use',
    2: 'Maintenance'
  }
  return statusMap[status] || 'Unknown'
}

// 获取状态样式类
const getStatusClass = (status) => {
  const classMap = {
    0: 'status-available',
    1: 'status-in-use',
    2: 'status-maintenance'
  }
  return classMap[status] || ''
}

// 选择租赁选项
const selectOption = (option) => {
  selectedOption.value = option.type
  duration.value = 1
  // 同步更新还车时间
  updateReturnTime()
}

// 监听租赁选项变化
watch(selectedOption, () => {
  updateReturnTime()
})

// 监听取车时间变化
watch(pickupDateTime, () => {
  updateReturnTime()
})

// 同意协议
const onAgreeChange = (e) => {
  isAgree.value = e.detail.value.length > 0
}

// 提交订单
const submitOrder = async () => {
  if (!isAgree.value) {
    uni.showToast({
      title: 'Please agree to the agreement',
      icon: 'none'
    })
    return
  }
  
  if (!selectedScooter.value) {
    uni.showToast({
      title: 'Please select a scooter',
      icon: 'none'
    })
    return
  }
  
  if (!pickupTime.value) {
    uni.showToast({
      title: 'Please select pickup time',
      icon: 'none'
    })
    return
  }

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
    const option = rentOptions.value.find(opt => opt.type === selectedOption.value)
    if (!option) {
      throw new Error('Invalid rental option')
    }

    // 格式化时间
    const startTime = formatTime(pickupTime.value)
    const endTime = formatTime(returnTimeText.value)

    console.log('提交订单数据:', {
      userId: userInfo.id,
      scooterId: selectedScooter.value.id,
      hireOptionId: option.id,
      startTime,
      endTime
    })

    const response = await uni.request({
      url: `${API_BASE_URL}/user/book/add`,
      method: 'POST',
      data: {
        userId: userInfo.id,
        scooterId: selectedScooter.value.id,
        hireOptionId: option.id,
        startTime,
        endTime
      },
      header: {
        'Content-Type': 'application/json'
      }
    })

    console.log('订单提交响应:', response)

    if (response.data.statusCode === 200) {
      uni.showToast({
        title: 'Order placed successfully',
        icon: 'success'
      })
      
      // 延迟跳转到订单详情页面
      setTimeout(() => {
        uni.navigateTo({
          url: `/pages/order/detail?id=${response.data.data.id}`
        })
      }, 1500)
    } else {
      throw new Error(response.data.message || 'Failed to place order')
    }
  } catch (error) {
    console.error('提交订单失败:', error)
    uni.showToast({
      title: error.message || 'Failed to place order',
      icon: 'none'
    })
  }
}

// 格式化时间函数
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  
  try {
    // 解析时间字符串 (格式: MM/DD Weekday HH:00)
    const parts = timeStr.split(' ')
    if (parts.length < 3) {
      throw new Error('Invalid time format')
    }
    
    const datePart = parts[0] // MM/DD
    const timePart = parts[2] // HH:00
    
    const [month, day] = datePart.split('/').map(num => parseInt(num))
    const [hours] = timePart.split(':').map(num => parseInt(num))
    
    console.log('解析的时间:', { month, day, hours }) // 添加调试日志
    
    const currentYear = new Date().getFullYear()
    const date = new Date(currentYear, month - 1, day, hours, 0, 0, 0)
    
    // 格式化为后端需要的格式
    const year = date.getFullYear()
    const formattedMonth = String(date.getMonth() + 1).padStart(2, '0')
    const formattedDay = String(date.getDate()).padStart(2, '0')
    const formattedHours = String(date.getHours()).padStart(2, '0')
    
    const formattedTime = `${year}-${formattedMonth}-${formattedDay} ${formattedHours}:00:00.605087`
    console.log('格式化后的时间:', formattedTime) // 添加调试日志
    
    return formattedTime
  } catch (error) {
    console.error('时间格式化错误:', error, '原始时间字符串:', timeStr)
    throw new Error('Invalid time format')
  }
}

// 初始化选择器数据
const initPickerData = () => {
  // 生成未来7天的日期
  const dates = []
  const weekdays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
  const today = new Date()
  
  for (let i = 0; i < 7; i++) {
    const date = new Date(today)
    date.setDate(today.getDate() + i)
    dates.push({
      day: `${date.getMonth() + 1}/${date.getDate()}`,
      weekday: weekdays[date.getDay()],
      date: date
    })
  }
  
  availableDates.value = dates
  
  // 生成可选时间（9:00-21:00，每小时一个选项）
  const times = []
  for (let i = 9; i <= 21; i++) {
    times.push(`${i}:00`)
  }
  
  availableTimes.value = times
}

// 选择日期
const selectDate = (index) => {
  selectedDateIndex.value = index
}

// 选择时间
const selectTime = (index) => {
  selectedTimeIndex.value = index
}

// 打开选择器
const openPicker = () => {
  initPickerData()
  pickerPopup.value.open()
}

// 关闭选择器
const closePicker = () => {
  pickerPopup.value.close()
}

// 确认选择
const confirmPicker = () => {
  const selectedDate = availableDates.value[selectedDateIndex.value].date
  const selectedTime = availableTimes.value[selectedTimeIndex.value]
  const [hours] = selectedTime.split(':').map(Number)
  
  console.log('选择的时间:', { selectedDate, selectedTime, hours }) // 添加调试日志
  
  selectedDate.setHours(hours, 0, 0, 0)
  pickupDateTime.value = selectedDate
  
  const weekdays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
  const month = String(selectedDate.getMonth() + 1).padStart(2, '0')
  const day = String(selectedDate.getDate()).padStart(2, '0')
  
  pickupTime.value = `${month}/${day} ${weekdays[selectedDate.getDay()]} ${selectedTime}`
  console.log('设置的取车时间:', pickupTime.value) // 添加调试日志
  
  updateReturnTime()
  closePicker()
}

// 修改选择取车时间函数
const selectPickupTime = () => {
  openPicker()
}

// 修改选择还车时间函数
const selectReturnTime = () => {
  // 还车时间由系统自动计算，不需要选择
  uni.showToast({
    title: 'Return time is automatically calculated',
    icon: 'none'
  })
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
    title: 'Price Details',
    content: `Base Price: ¥${totalPrice.value}\nDelivery Fee: ¥0`,
    showCancel: false
  })
}

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

// 添加总价计算
const totalPrice = computed(() => {
  const option = rentOptions.value.find(opt => opt.type === selectedOption.value)
  return option ? option.price : 0
})

// 页面加载时初始化
onMounted(() => {
  fetchRentOptions()
  
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  tomorrow.setHours(9, 0, 0, 0)
  pickupDateTime.value = tomorrow
  
  const weekdays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
  const month = String(tomorrow.getMonth() + 1).padStart(2, '0')
  const day = String(tomorrow.getDate()).padStart(2, '0')
  pickupTime.value = `${month}/${day} ${weekdays[tomorrow.getDay()]} 09:00`
  
  updateReturnTime()
})

onLoad((options) => {
  if (options.data) {
    pageData.value = JSON.parse(decodeURIComponent(options.data))
  }
})
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
  position: relative;
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
  padding: 10rpx 0;
}

.value {
  flex: 1;
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
  margin-left: 16rpx;
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
  z-index: 100;
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

/* 选车相关样式 */
.scooter-selector {
  margin: 24rpx;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  border-radius: 24rpx;
  padding: 24rpx;
}

.scooter-list {
  max-height: 400rpx;
}

.scooter-item {
  padding: 20rpx;
  border-bottom: 2rpx solid #f5f5f5;
  transition: all 0.3s;
}

.scooter-item.selected {
  background: #e6f7ff;
  border-radius: 12rpx;
}

.scooter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12rpx;
}

.scooter-id {
  font-size: 28rpx;
  font-weight: 500;
}

.scooter-status {
  font-size: 24rpx;
  padding: 4rpx 12rpx;
  border-radius: 8rpx;
}

.status-available {
  color: #52c41a;
  background: #f6ffed;
}

.status-in-use {
  color: #1890ff;
  background: #e6f7ff;
}

.status-maintenance {
  color: #faad14;
  background: #fffbe6;
}

.scooter-detail {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.config {
  font-size: 24rpx;
  color: #666;
}

.power {
  font-size: 24rpx;
  color: #1890ff;
}

/* 交互样式 */
.info-item:active {
  background-color: #f5f5f5;
}

/* 修改选择器样式 */
.picker-content {
  background: #fff;
  border-radius: 24rpx 24rpx 0 0;
  overflow: hidden;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 32rpx;
  border-bottom: 2rpx solid #f5f5f5;
  background: #fff;
}

.picker-title {
  font-size: 32rpx;
  color: #333;
  font-weight: 500;
}

.picker-cancel, .picker-confirm {
  font-size: 28rpx;
  color: #1890ff;
}

.picker-body {
  padding: 24rpx;
  background: #fff;
  max-height: 70vh;
  overflow-y: auto;
}

.date-section, .time-section {
  margin-bottom: 32rpx;
}

.section-title {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 16rpx;
  display: block;
}

.date-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
}

.time-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16rpx;
}

.date-item, .time-item {
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 16rpx;
  text-align: center;
  transition: all 0.3s;
}

.date-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8rpx;
}

.date-day {
  font-size: 28rpx;
  color: #333;
}

.date-weekday {
  font-size: 24rpx;
  color: #666;
}

.time-item {
  font-size: 28rpx;
  color: #333;
}

.date-item.selected, .time-item.selected {
  background: #e6f7ff;
  border: 2rpx solid #1890ff;
  color: #1890ff;
}
</style> 