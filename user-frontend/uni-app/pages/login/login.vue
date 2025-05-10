<template>
  <view class="login-container">
    <!-- 顶部状态栏 -->
    <view class="status-bar">
      <view class="center">
        <text class="center-text">哈啰</text>
      </view>

    </view>

    <!-- 返回按钮 -->
    <view class="back-btn" @tap="handleBack">
      <text class="iconfont icon-back">×</text>
    </view>

    <!-- 用户信息区域 -->
    <view class="user-info">
      <image class="avatar" src="/static/images/default-avatar.png" mode="aspectFill"></image>
      <text class="title">账号登录</text>
    </view>

    <!-- 登录表单 -->
    <view class="login-form">
      <!-- 添加消息提示 -->
      <view v-if="message" :class="['message', messageClass]">
        {{message}}
      </view>

      <view class="input-group">
        <input 
          type="text" 
          v-model="email" 
          placeholder="请输入邮箱"
          class="input-item"
        />
      </view>
      <view class="input-group">
        <input 
          :type="showPassword ? 'text' : 'password'" 
          v-model="password" 
          placeholder="请输入密码"
          class="input-item"
        />
        <text 
          class="toggle-password" 
          @tap="togglePasswordVisibility"
        >{{showPassword ? '隐藏' : '显示'}}</text>
      </view>

      <!-- 隐私政策 -->
      <view class="privacy-policy">
        <checkbox-group @change="handlePrivacyChange">
          <checkbox :checked="agreePrivacy" color="#1890ff" />
        </checkbox-group>
        <text class="policy-text">我已阅读并同意</text>
        <text class="policy-link" @tap="handlePrivacyClick">《法律条款及隐私政策》</text>
      </view>

      <!-- 登录按钮 -->
      <button 
        class="login-btn" 
        :class="{'login-btn-active': canLogin}"
        :disabled="!canLogin"
        @tap="handleLogin"
      >登录该账号</button>

      <!-- 注册按钮 -->
      <button 
        class="register-btn"
        @tap="handleRegister"
      >注册新账号</button>

      <!-- 登录遇到问题 -->
      <view class="login-help">
        <text @tap="handleLoginIssue">登录遇到问题</text>
      </view>

      <!-- 其他登录方式 -->
      <view class="other-login">
        <text class="other-title">其他登录方式</text>
        <view class="login-methods">
          <view class="method-item" @tap="handleOtherLogin('alipay')">
            <image src="/static/icons/alipay.png" mode="aspectFit"></image>
          </view>
          <view class="method-item" @tap="handleOtherLogin('apple')">
            <image src="/static/icons/apple.png" mode="aspectFit"></image>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import axios from 'axios'

const email = ref('')
const password = ref('')
const showPassword = ref(false)
const agreePrivacy = ref(false)
const message = ref('')
const messageClass = ref('')

// 计算是否可以登录
const canLogin = computed(() => {
  return email.value && 
         password.value.length >= 6 && 
         agreePrivacy.value
})

// 切换密码显示
const togglePasswordVisibility = () => {
  showPassword.value = !showPassword.value
}

// 处理隐私政策变更
const handlePrivacyChange = (e) => {
  agreePrivacy.value = e.detail.value.length > 0
}

// 处理返回
const handleBack = () => {
  uni.navigateBack()
}

// 处理登录
const handleLogin = async () => {
  if (!canLogin.value) return

  if (!email.value || !password.value) {
    message.value = "请输入邮箱和密码"
    messageClass.value = "error"
    return
  }

  try {
    console.log("开始登录请求...")
    const response = await axios.post('https://192.168.126.147:8088/user/login', {
      email: email.value,
      password: password.value,
      role: "user"
    }, {
      headers: { "Content-Type": "application/json" }
    })

    console.log('登录响应:', response)

    if (response.data.statusCode === 200) {
      message.value = "登录成功！"
      messageClass.value = "success"
      // 存储用户信息
      uni.setStorageSync('user', JSON.stringify(response.data.data))
      // 登录成功后跳转
      setTimeout(() => {
        uni.switchTab({
          url: '/pages/index/index'
        })
      }, 1500)
    } else {
      message.value = "用户名或密码错误"
      messageClass.value = "error"
    }
  } catch (error) {
    console.error('登录错误:', error)
    message.value = "登录请求失败，请检查网络"
    messageClass.value = "error"
  }
}

// 处理注册
const handleRegister = () => {
  uni.navigateTo({
    url: '/pages/register/register'
  })
}

// 处理隐私政策点击
const handlePrivacyClick = () => {
  uni.navigateTo({
    url: '/pages/privacy/privacy'
  })
}

// 处理登录问题
const handleLoginIssue = () => {
  uni.navigateTo({
    url: '/pages/login-help/login-help'
  })
}

// 处理其他登录方式
const handleOtherLogin = (type) => {
  uni.showToast({
    title: `选择了${type}登录`,
    icon: 'none'
  })
}
</script>

<style>
/* 重置页面背景 */
page {
  background: #ffffff;
}

.login-container {
  min-height: 100vh;
  position: relative;
}

/* 修改顶部状态栏样式 */
.status-bar {
  padding: 40rpx 32rpx;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #ffffff;
}

.left {
  font-size: 32rpx;
  color: #000000;
  font-weight: 400;
}

.center {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
}

.center-text {
  font-size: 32rpx;
  color: #000000;
  background: #409EFF;
  color: #ffffff;
  padding: 4rpx 24rpx;
  border-radius: 24rpx;
}

.right {
  display: flex;
  align-items: center;
  gap: 8rpx;
  font-size: 28rpx;
  color: #000000;
}

.signal {
  font-size: 24rpx;
  letter-spacing: -2rpx;
}

.battery {
  margin-left: 4rpx;
}

.battery::after {
  content: '%';
  margin-left: 2rpx;
}

/* 修改返回按钮样式 */
.back-btn {
  position: absolute;
  top: 120rpx;
  left: 32rpx;
  font-size: 48rpx;
  color: #999;
  z-index: 1;
}

.user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120rpx 0 60rpx;
}

.avatar {
  width: 140rpx;
  height: 140rpx;
  border-radius: 70rpx;
  margin-bottom: 24rpx;
  border: 6rpx solid #fff;
  box-shadow: 0 6rpx 16rpx rgba(24, 144, 255, 0.15);
}

.title {
  font-size: 40rpx;
  background: linear-gradient(120deg, #1890ff, #36cfc9);
  -webkit-background-clip: text;
  color: transparent;
  font-weight: bold;
  letter-spacing: 2rpx;
}

.login-form {
  padding: 0 40rpx;
}

.input-group {
  position: relative;
  margin-bottom: 32rpx;
}

.input-item {
  width: 100%;
  height: 96rpx;
  background: rgba(245, 245, 245, 0.8);
  border-radius: 48rpx;
  padding: 0 32rpx;
  font-size: 28rpx;
  box-sizing: border-box;
  transition: all 0.3s ease;
  border: 2rpx solid transparent;
}

.input-item:focus {
  background: #ffffff;
  border-color: #1890ff;
  box-shadow: 0 4rpx 12rpx rgba(24, 144, 255, 0.1);
}

.toggle-password {
  position: absolute;
  right: 32rpx;
  top: 50%;
  transform: translateY(-50%);
  color: #1890ff;
  font-size: 28rpx;
  padding: 10rpx;
}

.message {
  padding: 24rpx;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  font-size: 28rpx;
  text-align: center;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10rpx);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message.success {
  background: linear-gradient(120deg, #f6ffed, #d9f7be);
  border: none;
  color: #389e0d;
}

.message.error {
  background: linear-gradient(120deg, #fff2f0, #ffccc7);
  border: none;
  color: #cf1322;
}

.privacy-policy {
  display: flex;
  align-items: center;
  margin: 32rpx 0;
  font-size: 26rpx;
}

.policy-text {
  color: #666;
  margin-left: 8rpx;
}

.policy-link {
  background: linear-gradient(120deg, #1890ff, #36cfc9);
  -webkit-background-clip: text;
  color: transparent;
  font-weight: 500;
}

.login-btn {
  width: 100%;
  height: 96rpx;
  background: linear-gradient(120deg, #cccccc, #d9d9d9);
  color: #ffffff;
  border-radius: 48rpx;
  font-size: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;
  border: none;
  transition: all 0.3s ease;
  letter-spacing: 2rpx;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

.login-btn-active {
  background: linear-gradient(120deg, #1890ff, #36cfc9);
  box-shadow: 0 6rpx 16rpx rgba(24, 144, 255, 0.3);
  transform: translateY(-2rpx);
}

.register-btn {
  width: 100%;
  height: 96rpx;
  background: #ffffff;
  border-radius: 48rpx;
  font-size: 32rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24rpx;
  border: 2rpx solid transparent;
  background-image: linear-gradient(#fff, #fff), linear-gradient(120deg, #1890ff, #36cfc9);
  background-origin: border-box;
  background-clip: content-box, border-box;
  transition: all 0.3s ease;
  color: #1890ff;
  letter-spacing: 2rpx;
}

.register-btn:active {
  transform: scale(0.98);
}

.login-help {
  text-align: center;
  font-size: 28rpx;
  color: #666;
  margin: 32rpx 0;
  text-decoration: underline;
  text-underline-offset: 4rpx;
  text-decoration-color: #ccc;
}

.other-login {
  margin-top: 80rpx;
  padding: 0 60rpx;
}

.other-title {
  text-align: center;
  font-size: 26rpx;
  color: #999;
  margin-bottom: 40rpx;
  position: relative;
}

.other-title::before,
.other-title::after {
  content: '';
  position: absolute;
  top: 50%;
  width: 120rpx;
  height: 2rpx;
  background: linear-gradient(90deg, transparent, #e8e8e8);
}

.other-title::before {
  left: 25%;
  transform: translateX(-50%);
}

.other-title::after {
  right: 25%;
  transform: translateX(50%);
  background: linear-gradient(90deg, #e8e8e8, transparent);
}

.login-methods {
  display: flex;
  justify-content: center;
  gap: 80rpx;
}

.method-item {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50rpx;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.method-item:active {
  transform: scale(0.95);
  box-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.05);
}

.method-item image {
  width: 56rpx;
  height: 56rpx;
}
</style> 