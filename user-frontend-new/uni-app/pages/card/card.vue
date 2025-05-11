<script setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { API_BASE_URL } from '@/config/index'

// 模拟银行列表
const bankList = [
  'Industrial and Commercial Bank of China',
  'Agricultural Bank of China',
  'Bank of China',
  'China Construction Bank',
  'Bank of Communications',
  'China Merchants Bank',
  'China CITIC Bank',
  'China Minsheng Bank',
  'China Everbright Bank',
  'Shanghai Pudong Development Bank'
]

// 表单数据
const cardForm = ref({
  holderName: 'Zhang San',
  cardNumber: '6222021234567890123',
  bankName: 'Industrial and Commercial Bank of China',
  expiryMonth: '12',
  expiryYear: '25',
  cvv: '123'
})

const bankIndex = ref(0)

// 银行选择变化
const onBankChange = (e) => {
  bankIndex.value = e.detail.value
  cardForm.value.bankName = bankList[e.detail.value]
}

// 提交表单
const handleSubmit = async () => {
  // 表单验证
  if (!validateForm()) return

  try {
    // 获取用户信息
    const userInfoStr = uni.getStorageSync('userInfo')
    console.log('存储的用户信息字符串:', userInfoStr) // 添加日志

    if (!userInfoStr) {
      uni.showToast({
        title: 'Please log in first',
        icon: 'none'
      })
      return
    }

    let userInfo
    try {
      userInfo = JSON.parse(userInfoStr)
      console.log('解析后的用户信息:', userInfo) // 添加日志
    } catch (parseError) {
      console.error('解析用户信息失败:', parseError)
      uni.showToast({
        title: '用户信息格式错误，请重新登录',
        icon: 'none'
      })
      return
    }

    if (!userInfo || !userInfo.id) {
      console.error('用户信息缺少ID:', userInfo) // 添加日志
      uni.showToast({
        title: '用户信息无效，请重新登录',
        icon: 'none'
      })
      return
    }
    console.log(userInfo.id);

    // 调用绑定银行卡API，将 axios 改为 uni.request
    const response = await uni.request({
      url: `${API_BASE_URL}/user/card`,
      method: 'POST',
      data: {
        id: userInfo.id,
        card: cardForm.value.cardNumber,
      },
      header: {
        'Content-Type': 'application/json'
      }
    })

    console.log('API响应:', response.data)

    if (response.data.statusCode === 200) {
      // 更新用户信息中的银行卡信息
      const updatedUserInfo = {
        ...userInfo,
        card: {
          cardNumber: cardForm.value.cardNumber,
          holderName: cardForm.value.holderName,
          bankName: cardForm.value.bankName,
          expiryMonth: cardForm.value.expiryMonth,
          expiryYear: cardForm.value.expiryYear
        }
      }
      
      // 更新本地存储
      uni.setStorageSync('userInfo', JSON.stringify(updatedUserInfo))
      console.log('更新后的用户信息:', updatedUserInfo)

      uni.showToast({
        title: 'Binding successful',
        icon: 'success',
        duration: 1500
      })

      // 延迟返回上一页
      setTimeout(() => {
        uni.navigateBack({
          delta: 1,
          success: () => {
            // 获取当前页面栈
            const pages = getCurrentPages()
            // 获取上一个页面（支付页面）
            const prevPage = pages[pages.length - 1]
            // 如果上一个页面存在，则调用其刷新方法
            if (prevPage && prevPage.$vm && prevPage.$vm.refreshPage) {
              prevPage.$vm.refreshPage()
            }
          }
        })
      }, 1500)
    } else {
      throw new Error(response.data.message || 'Binding failed')
    }
  } catch (error) {
    console.error('绑定银行卡失败:', error)
    console.error('错误详情:', error.response?.data || error.message) // 添加详细错误日志
    uni.showToast({
      title: error.response?.data?.message || error.message || 'Binding failed',
      icon: 'none'
    })
  }
}

// 表单验证
const validateForm = () => {
  const { holderName, cardNumber, bankName, expiryMonth, expiryYear, cvv } = cardForm.value

  if (!holderName) {
    uni.showToast({
      title: 'Please enter cardholder name',
      icon: 'none'
    })
    return false
  }

  if (!cardNumber || cardNumber.length < 16) {
    uni.showToast({
      title: 'Please enter a valid card number',
      icon: 'none'
    })
    return false
  }

  if (!bankName) {
    uni.showToast({
      title: 'Please select a bank',
      icon: 'none'
    })
    return false
  }

  if (!expiryMonth || !expiryYear) {
    uni.showToast({
      title: 'Please enter expiry date',
      icon: 'none'
    })
    return false
  }

  if (!cvv || cvv.length !== 3) {
    uni.showToast({
      title: 'Please enter a valid security code',
      icon: 'none'
    })
    return false
  }

  return true
}

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

onMounted(() => {
  // 设置默认银行索引
  bankIndex.value = bankList.indexOf(cardForm.value.bankName)
})
</script>

<template>
  <view class="card-container">
    <!-- 顶部导航 -->
    <view class="nav-bar">
      <view class="back-btn" @tap="goBack">
        <text class="iconfont icon-back">×</text>
      </view>
      <text class="title">Bind Bank Card</text>
    </view>

    <!-- 银行卡表单 -->
    <view class="card-form">
      <view class="form-item">
        <text class="label">Cardholder</text>
        <input 
          type="text" 
          v-model="cardForm.holderName" 
          placeholder="Please enter cardholder name"
          class="input"
        />
      </view>
      <view class="form-item">
        <text class="label">Card Number</text>
        <input 
          type="number" 
          v-model="cardForm.cardNumber" 
          placeholder="Please enter card number"
          class="input"
        />
      </view>
      <view class="form-item">
        <text class="label">Bank</text>
        <picker 
          :range="bankList" 
          :value="bankIndex" 
          @change="onBankChange"
          class="picker"
        >
          <view class="picker-value">
            {{cardForm.bankName || 'Please select bank'}}
          </view>
        </picker>
      </view>
      <view class="form-item">
        <text class="label">Expiry Date</text>
        <view class="expiry-inputs">
          <input 
            type="number" 
            v-model="cardForm.expiryMonth" 
            placeholder="MM"
            class="input expiry"
            maxlength="2"
          />
          <text class="separator">/</text>
          <input 
            type="number" 
            v-model="cardForm.expiryYear" 
            placeholder="YY"
            class="input expiry"
            maxlength="2"
          />
        </view>
      </view>
      <view class="form-item">
        <text class="label">Security Code</text>
        <input 
          type="password" 
          v-model="cardForm.cvv" 
          placeholder="Please enter 3-digit security code"
          class="input"
          maxlength="3"
        />
      </view>
    </view>

    <!-- 提交按钮 -->
    <view class="bottom-bar">
      <button class="submit-btn" @tap="handleSubmit">
        Confirm Binding
      </button>
    </view>
  </view>
</template>

<style>
.card-container {
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

.card-form {
  margin: 24rpx;
  background: #fff;
  border-radius: 24rpx;
  padding: 32rpx;
}

.form-item {
  margin-bottom: 32rpx;
}

.form-item:last-child {
  margin-bottom: 0;
}

.label {
  font-size: 28rpx;
  color: #333;
  margin-bottom: 16rpx;
  display: block;
}

.input {
  width: 100%;
  height: 88rpx;
  background: #f7f8fa;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
}

.picker {
  width: 100%;
  height: 88rpx;
  background: #f7f8fa;
  border-radius: 12rpx;
  padding: 0 24rpx;
  display: flex;
  align-items: center;
}

.picker-value {
  font-size: 28rpx;
  color: #333;
}

.expiry-inputs {
  display: flex;
  align-items: center;
  gap: 16rpx;
}

.expiry {
  width: 120rpx;
  text-align: center;
}

.separator {
  font-size: 28rpx;
  color: #333;
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
</style>