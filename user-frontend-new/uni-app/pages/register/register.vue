<template>
  <view class="register-container" v-if="isPageReady">
    <!-- 顶部状态栏 -->
    <view class="status-bar">
      <view class="center">
        <text class="center-text">Rongcheng Travel</text>
      </view>
    </view>

    <!-- 返回按钮 -->
    <view class="back-btn" @tap="goBack">
      <text class="iconfont icon-back">×</text>
    </view>

    <!-- 用户信息区域 -->
    <view class="user-info">
      <image class="avatar" src="/static/images/husky-avatar.png" mode="aspectFill"></image>
      <text class="title">Create Account</text>
    </view>

    <!-- 注册表单 -->
    <view class="register-form">
      <!-- 添加消息提示 -->
      <view v-if="msg" :class="['message', msgType]">
        {{msg}}
      </view>

      <view class="input-group">
        <input 
          type="text" 
          v-model="name" 
          placeholder="Please enter username"
          class="input-item"
          :focus="true"
        />
      </view>

      <view class="input-group">
        <input 
          type="text" 
          v-model="email" 
          placeholder="Please enter email"
          class="input-item"
        />
      </view>

      <view class="input-group">
        <input 
          :type="showPwd ? 'text' : 'password'" 
          v-model="password" 
          placeholder="Please enter password"
          class="input-item"
        />
        <text 
          class="toggle-password" 
          @tap="togglePwd"
        >{{showPwd ? 'Hide' : 'Show'}}</text>
      </view>

      <view class="input-group">
        <input 
          :type="showConfirmPwd ? 'text' : 'password'" 
          v-model="confirmPassword" 
          placeholder="Please confirm password"
          class="input-item"
        />
        <text 
          class="toggle-password" 
          @tap="toggleConfirmPwd"
        >{{showConfirmPwd ? 'Hide' : 'Show'}}</text>
      </view>

      <!-- 隐私政策 -->
      <view class="privacy-policy">
        <checkbox-group @change="onAgreeChange">
          <checkbox :checked="isAgree" color="#1890ff" />
        </checkbox-group>
        <text class="policy-text">I have read and agree to the</text>
        <text class="policy-link" @tap="goPrivacy">Legal Terms and Privacy Policy</text>
      </view>

      <!-- 注册按钮 -->
      <button 
        class="register-btn" 
        :class="{'register-btn-active': canRegister}"
        :disabled="!canRegister || isRegistering"
        @tap="doRegister"
      >{{isRegistering ? 'Registering...' : 'Register'}}</button>

      <!-- 返回登录 -->
      <button 
        class="login-btn"
        @tap="goLogin"
      >Back to Login</button>
    </view>
  </view>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { API_BASE_URL } from '@/config/index'

// 基础数据
const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPwd = ref(false)
const showConfirmPwd = ref(false)
const isAgree = ref(false)
const msg = ref('')
const msgType = ref('')
const isPageReady = ref(false)
const isRegistering = ref(false)

// 页面显示时触发
onShow(() => {
  resetPage()
})

// 页面挂载时触发
onMounted(() => {
  resetPage()
})

// 重置页面状态
const resetPage = () => {
  isPageReady.value = false
  setTimeout(() => {
    isPageReady.value = true
    resetForm()
  }, 50)
}

// 重置表单
const resetForm = () => {
  name.value = ''
  email.value = ''
  password.value = ''
  confirmPassword.value = ''
  showPwd.value = false
  showConfirmPwd.value = false
  isAgree.value = false
  msg.value = ''
  msgType.value = ''
  isRegistering.value = false
}

// 是否可以注册
const canRegister = computed(() => {
  return name.value && 
         email.value && 
         password.value && 
         confirmPassword.value && 
         password.value === confirmPassword.value && 
         password.value.length >= 6 && 
         isAgree.value
})

// 显示/隐藏密码
const togglePwd = () => {
  showPwd.value = !showPwd.value
}

const toggleConfirmPwd = () => {
  showConfirmPwd.value = !showConfirmPwd.value
}

// 同意协议
const onAgreeChange = (e) => {
  isAgree.value = e.detail.value.length > 0
}

// 返回上一页
const goBack = () => {
  uni.navigateTo({ url: '/pages/login/login' })
}

// 注册
const doRegister = async () => {
  if (isRegistering.value) return
  isRegistering.value = true

  if (!canRegister.value) {
    showMsg('Please fill in all required fields', 'error')
    isRegistering.value = false
    return
  }

  // 验证邮箱格式
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(email.value)) {
    showMsg('Please enter a valid email address', 'error')
    isRegistering.value = false
    return
  }

  // 验证密码一致性
  if (password.value !== confirmPassword.value) {
    showMsg('Passwords do not match', 'error')
    isRegistering.value = false
    return
  }

  try {
    console.log('Sending registration request...')
    const res = await uni.request({
      url: `${API_BASE_URL}/user`,
      method: 'POST',
      data: {
        name: name.value,
        email: email.value,
        password: password.value,
        role: 'user'
      },
      header: {
        'Content-Type': 'application/json'
      }
    })

    console.log('Registration response:', res)

    if (res.data && res.data.statusCode === 200) {
      showMsg('Registration successful', 'success')
      
      // 延迟跳转到登录页
      setTimeout(() => {
        uni.navigateTo({ url: '/pages/login/login' })
      }, 1500)
    } else {
      const errorMsg = res.data?.message || 'Registration failed, please try again'
      showMsg(errorMsg, 'error')
    }
  } catch (err) {
    console.error('Registration failed:', err)
    if (err.data) {
      // 服务器返回错误
      const errorMsg = err.data?.message || 'Registration failed, please try again'
      showMsg(errorMsg, 'error')
    } else {
      // 其他错误
      showMsg('An unexpected error occurred', 'error')
    }
  } finally {
    isRegistering.value = false
  }
}

// 显示消息
const showMsg = (text, type) => {
  msg.value = text
  msgType.value = type
}

// 去登录
const goLogin = () => {
  uni.navigateTo({ url: '/pages/login/login' })
}

// 查看协议
const goPrivacy = () => {
  uni.navigateTo({ url: '/pages/privacy/privacy' })
}
</script>

<style>
page {
  background: linear-gradient(180deg, #f0f7ff 0%, #ffffff 100%);
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.register-container {
  min-height: 100vh;
  padding: 0 60rpx;
  position: relative;
  animation: fadeIn 0.3s ease;
  will-change: opacity;
}

/* 状态栏 */
.status-bar {
  padding-top: 88rpx;
  text-align: center;
  position: relative;
  background: linear-gradient(180deg, #f0f7ff 0%, #ffffff 100%);
  z-index: 1;
}

.center-text {
  font-size: 36rpx;
  font-weight: bold;
  color: #333;
  font-family: inherit;
}

/* 返回按钮 */
.back-btn {
  position: absolute;
  left: 0;
  top: 88rpx;
  padding: 20rpx;
  z-index: 2;
}

.back-btn text {
  font-size: 40rpx;
  color: #333;
  font-family: inherit;
}

/* 用户信息 */
.user-info {
  margin-top: 60rpx;
  text-align: center;
  position: relative;
  z-index: 1;
}

.avatar {
  width: 160rpx;
  height: 160rpx;
  border-radius: 50%;
  margin-bottom: 40rpx;
  border: 8rpx solid rgba(24,144,255,0.1);
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.1);
}

.title {
  font-size: 48rpx;
  font-weight: bold;
  background: linear-gradient(45deg, #1890ff, #36b4ff);
  -webkit-background-clip: text;
  color: transparent;
  margin-bottom: 80rpx;
  display: block;
  font-family: inherit;
}

/* 表单样式 */
.register-form {
  margin-top: 40rpx;
  position: relative;
  z-index: 1;
}

.message {
  padding: 20rpx;
  border-radius: 12rpx;
  text-align: center;
  margin-bottom: 20rpx;
  font-size: 28rpx;
  animation: fadeIn 0.3s ease;
  font-family: inherit;
}

.message.success {
  background: #f6ffed;
  color: #52c41a;
  border: 2rpx solid #b7eb8f;
}

.message.error {
  background: #fff2f0;
  color: #ff4d4f;
  border: 2rpx solid #ffccc7;
}

.input-group {
  position: relative;
  margin-bottom: 40rpx;
}

.input-item {
  width: 100%;
  height: 100rpx;
  background: rgba(255,255,255,0.8);
  border-radius: 20rpx;
  padding: 0 40rpx;
  font-size: 32rpx;
  color: #333;
  box-shadow: 0 4rpx 12rpx rgba(0,0,0,0.05);
  transition: all 0.3s;
  font-family: inherit;
}

.input-item:focus {
  background: #ffffff;
  box-shadow: 0 4rpx 16rpx rgba(24,144,255,0.1);
  transform: translateY(-2rpx);
}

.toggle-password {
  position: absolute;
  right: 40rpx;
  top: 50%;
  transform: translateY(-50%);
  font-size: 28rpx;
  color: #1890ff;
  z-index: 1;
  font-family: inherit;
}

/* 隐私政策 */
.privacy-policy {
  display: flex;
  align-items: center;
  margin: 40rpx 0;
  padding: 0 20rpx;
}

.policy-text {
  font-size: 26rpx;
  color: #666;
  margin-left: 12rpx;
  font-family: inherit;
}

.policy-link {
  font-size: 26rpx;
  color: #1890ff;
  font-family: inherit;
}

/* 按钮样式 */
.register-btn {
  width: 100%;
  height: 100rpx;
  background: linear-gradient(45deg, #1890ff, #36b4ff);
  border-radius: 50rpx;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  margin-top: 60rpx;
  opacity: 0.6;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: none;
  font-family: inherit;
}

.register-btn-active {
  opacity: 1;
  box-shadow: 0 8rpx 24rpx rgba(24,144,255,0.3);
}

.register-btn:active {
  transform: scale(0.98);
}

.login-btn {
  width: 100%;
  height: 100rpx;
  background: transparent;
  border-radius: 50rpx;
  color: #666;
  font-size: 32rpx;
  margin-top: 30rpx;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2rpx solid #ddd;
  font-family: inherit;
}

.login-btn:active {
  background: rgba(0,0,0,0.05);
}

@keyframes fadeIn {
  from { 
    opacity: 0;
    transform: translateY(10rpx);
  }
  to { 
    opacity: 1;
    transform: translateY(0);
  }
}
</style>