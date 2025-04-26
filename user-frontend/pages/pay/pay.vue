<template>
  <view class="payment-container">
    <!-- 顶部导航 -->
    <view class="nav-bar">
      <view class="back-btn" @tap="goBack">
        <text class="iconfont icon-back">×</text>
      </view>
      <text class="title">支付订单</text>
    </view>

    <!-- 订单信息 -->
    <view class="order-info">
      <view class="info-item">
        <text class="label">订单金额</text>
        <text class="value">¥{{amount}}</text>
      </view>
      <view class="info-item">
        <text class="label">租车时长</text>
        <text class="value">{{duration}}</text>
      </view>
      <view class="info-item">
        <text class="label">取车时间</text>
        <text class="value">{{pickupTime}}</text>
      </view>
      <view class="info-item">
        <text class="label">还车时间</text>
        <text class="value">{{returnTime}}</text>
      </view>
    </view>

    <!-- 支付方式 -->
    <view class="payment-methods">
      <text class="section-title">选择支付方式</text>
      <view class="method-list">
        <view 
          class="method-item"
          :class="{'selected': selectedMethod === 'alipay'}"
          @tap="selectMethod('alipay')"
        >
          <image class="method-icon" src="/static/icons/alipay.png" mode="aspectFit"></image>
          <text class="method-name">支付宝支付</text>
          <text class="check-icon" v-if="selectedMethod === 'alipay'">✓</text>
        </view>
        <view 
          class="method-item"
          :class="{'selected': selectedMethod === 'wechat'}"
          @tap="selectMethod('wechat')"
        >
          <image class="method-icon" src="/static/icons/wechat.png" mode="aspectFit"></image>
          <text class="method-name">微信支付</text>
          <text class="check-icon" v-if="selectedMethod === 'wechat'">✓</text>
        </view>
      </view>
    </view>

    <!-- 底部支付按钮 -->
    <view class="bottom-bar">
      <view class="price-section">
        <text class="total-price">¥{{amount}}</text>
      </view>
      <button 
        class="pay-btn" 
        :disabled="!selectedMethod"
        @tap="handlePay"
      >立即支付</button>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { API_BASE_URL } from '@/config/index'

const amount = ref('')
const duration = ref('')
const pickupTime = ref('')
const returnTime = ref('')
const selectedMethod = ref('')

// 选择支付方式
const selectMethod = (method) => {
  selectedMethod.value = method
}

// 处理支付
const handlePay = async () => {
  if (!selectedMethod.value) return
   console.log('取车时间:', pickupTime.value);
    console.log('还车时间:', returnTime.value);
  try {
    // 显示加载中
    uni.showLoading({
      title: '支付中...'
    })

    // 格式化时间
	const formatTime = (timeStr) => {
	  try {
	    // 1. 首先提取年月日
	    const yearMatch = timeStr.match(/(\d{4})年/);
	    const monthMatch = timeStr.match(/(\d{1,2})月/);
	    const dayMatch = timeStr.match(/(\d{1,2})日/);
	    const timeMatch = timeStr.match(/(\d{2}:\d{2})/);
	
	    if (!yearMatch || !monthMatch || !dayMatch || !timeMatch) {
	      throw new Error('时间格式解析失败');
	    }
	
	    // 2. 获取各个部分
	    const year = yearMatch[1];
	    const month = monthMatch[1].padStart(2, '0');
	    const day = dayMatch[1].padStart(2, '0');
	    const time = timeMatch[1];
	
	    // 3. 组合成标准格式
	    const standardTimeStr = `${year}-${month}-${day} ${time}:00`;
	    
	    console.log('解析后的标准时间格式:', standardTimeStr);
	
	    // 4. 验证时间是否有效
	    const date_obj = new Date(standardTimeStr);
	    if (isNaN(date_obj.getTime())) {
	      throw new Error('无效的日期时间');
	    }
	
	    // 5. 转换为ISO格式并返回
	    return date_obj.toISOString().replace('T', ' ').slice(0, 19);
	  } catch (error) {
	    console.error('时间格式化详细错误:', {
	      输入时间: timeStr,
	      错误信息: error.message
	    });
	    throw new Error(`时间格式化失败: ${error.message}`);
	  }
	}

    // 准备订单数据
    const orderData = {
      userId: 1, // 这里应该从用户信息中获取
      scooterId: 3, // 这里应该从路由参数中获取
      hireOptionId: 3, // 这里应该根据选择的租期类型确定
      startTime: formatTime(pickupTime.value),
      endTime: formatTime(returnTime.value)
    }

    // 调用提交订单API
    const response = await uni.request({
      url: `${API_BASE_URL}/user/book/add`,
      method: 'POST',
      data: orderData,
      header: {
        'Content-Type': 'application/json'
      }
    })
	console.log(response);
    if (response.statusCode === 200) {
      // 订单创建成功，模拟支付过程
      setTimeout(() => {
        uni.hideLoading()
        uni.showToast({
          title: '支付成功',
          icon: 'success',
          duration: 2000,
          success: () => {
            setTimeout(() => {
              // 跳转到订单详情页
              uni.redirectTo({
                url: `/pages/order/order?id=${response.data.data.id}`
              })
            }, 2000)
          }
        })
      }, 2000)
    } else {
      throw new Error(response.data.message || '订单创建失败')
    }
  } catch (error) {
    uni.hideLoading()
    uni.showToast({
      title: error.message || '支付失败',
      icon: 'none',
      duration: 2000
    })
  }
}

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

// 获取路由参数
onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  const options = currentPage.$page.options
  
  amount.value = options.amount
  duration.value = options.duration
  pickupTime.value = options.pickupTime
  returnTime.value = options.returnTime
})
</script>

<style>
page {
  background: #f7f8fa;
}

.payment-container {
  min-height: 100vh;
  padding-bottom: 120rpx;
}

/* 导航栏 */
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

/* 订单信息 */
.order-info {
  margin: 24rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 24rpx;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
}

.info-item .label {
  font-size: 28rpx;
  color: #666;
}

.info-item .value {
  font-size: 28rpx;
  color: #333;
}

/* 支付方式 */
.payment-methods {
  margin: 24rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 24rpx;
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
  align-items: center;
  padding: 24rpx;
  border-radius: 16rpx;
  background: #f7f8fa;
  transition: all 0.3s;
}

.method-item.selected {
  background: #e6f7ff;
  border: 2rpx solid #1890ff;
}

.method-icon {
  width: 48rpx;
  height: 48rpx;
  margin-right: 16rpx;
}

.method-name {
  flex: 1;
  font-size: 28rpx;
  color: #333;
}

.check-icon {
  color: #1890ff;
  font-size: 32rpx;
}

/* 底部栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  padding: 24rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 -4rpx 12rpx rgba(0,0,0,0.05);
}

.price-section {
  flex: 1;
}

.total-price {
  font-size: 40rpx;
  color: #ff4d4f;
  font-weight: bold;
}

.pay-btn {
  width: 240rpx;
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
</style> 