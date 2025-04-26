<script setup>
import { ref } from 'vue'
import { API_BASE_URL } from '@/config/index'

const cardNumber = ref('')
const cardHolder = ref('')
const expiryDate = ref('')
const cvv = ref('')

// 验证卡号格式
const formatCardNumber = (value) => {
  // 移除所有非数字字符
  let numbers = value.replace(/\D/g, '')
  // 每4位添加空格
  numbers = numbers.replace(/(\d{4})(?=\d)/g, '$1 ')
  // 限制长度
  numbers = numbers.slice(0, 19)
  cardNumber.value = numbers
}

// 验证持卡人姓名
const validateName = (value) => {
  cardHolder.value = value.replace(/[^a-zA-Z\s]/g, '')
}

// 验证有效期格式
const formatExpiryDate = (value) => {
  // 移除非数字字符
  let numbers = value.replace(/\D/g, '')
  // 添加斜杠
  if (numbers.length > 2) {
    numbers = numbers.slice(0, 2) + '/' + numbers.slice(2, 4)
  }
  // 限制长度
  numbers = numbers.slice(0, 5)
  expiryDate.value = numbers
}

// 验证CVV
const validateCVV = (value) => {
  cvv.value = value.replace(/\D/g, '').slice(0, 3)
}

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

// 提交表单
const submitCard = async () => {
  // 表单验证
  if (!cardNumber.value || !cardHolder.value || !expiryDate.value || !cvv.value) {
    uni.showToast({
      title: '请填写完整信息',
      icon: 'none'
    })
    return
  }

  try {
    const userInfo = uni.getStorageSync('userInfo')
    if (!userInfo) {
      uni.showToast({
        title: '请先登录',
        icon: 'none'
      })
      return
    }

    // TODO: 实际API调用
    /*const response = await uni.request({
      url: `${API_BASE_URL}/user/card/bind`,
      method: 'POST',
      data: {
        userId: JSON.parse(userInfo).id,
        cardNumber: cardNumber.value.replace(/\s/g, ''),
        cardHolder: cardHolder.value,
        expiryDate: expiryDate.value,
        cvv: cvv.value
      }
    })

    if (response.statusCode === 200) {
      uni.showToast({
        title: '绑定成功',
        icon: 'success'
      })
      setTimeout(() => {
        uni.navigateBack()
      }, 1500)
    }*/

    // 模拟成功响应
    uni.showToast({
      title: '绑定成功',
      icon: 'success'
    })
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('绑定银行卡失败:', error)
    uni.showToast({
      title: '绑定失败',
      icon: 'none'
    })
  }
}
</script>

<template>
  <view class="container">
    <!-- 顶部导航栏 -->
    <view class="nav-bar">
      <view class="back-btn" @tap="goBack">
        <text class="iconfont icon-back">×</text>
      </view>
      <text class="title">绑定银行卡</text>
    </view>

    <!-- 表单区域 -->
    <view class="card-form">
      <view class="form-item">
        <text class="label">卡号</text>
        <input
          type="text"
          v-model="cardNumber"
          @input="(e) => formatCardNumber(e.detail.value)"
          placeholder="请输入银行卡号"
          maxlength="19"
        />
      </view>

      <view class="form-item">
        <text class="label">持卡人</text>
        <input
          type="text"
          v-model="cardHolder"
          @input="(e) => validateName(e.detail.value)"
          placeholder="请输入持卡人姓名"
        />
      </view>

      <view class="form-row">
        <view class="form-item half">
          <text class="label">有效期</text>
          <input
            type="text"
            v-model="expiryDate"
            @input="(e) => formatExpiryDate(e.detail.value)"
            placeholder="MM/YY"
            maxlength="5"
          />
        </view>

        <view class="form-item half">
          <text class="label">CVV</text>
          <input
            type="password"
            v-model="cvv"
            @input="(e) => validateCVV(e.detail.value)"
            placeholder="请输入CVV"
            maxlength="3"
          />
        </view>
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="submit-section">
      <button class="submit-btn" @tap="submitCard">确认绑定</button>
    </view>
  </view>
</template>

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

.card-form {
  margin: 24rpx;
  padding: 32rpx;
  background: #fff;
  border-radius: 16rpx;
}

.form-item {
  margin-bottom: 32rpx;
}

.form-item:last-child {
  margin-bottom: 0;
}

.form-row {
  display: flex;
  gap: 24rpx;
}

.half {
  flex: 1;
}

.label {
  display: block;
  font-size: 28rpx;
  color: #666;
  margin-bottom: 12rpx;
}

input {
  width: 100%;
  height: 88rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333;
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