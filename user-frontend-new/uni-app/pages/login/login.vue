<template>
  <view class="login-container">
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
      <text class="title">Account Login</text>
    </view>

    <!-- 登录表单 -->
    <view class="login-form">
      <!-- 添加消息提示 -->
      <view v-if="msg" :class="['message', msgType]">
        {{msg}}
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

      <!-- 隐私政策 -->
      <view class="privacy-policy">
        <checkbox-group @change="onAgreeChange">
          <checkbox :checked="isAgree" color="#1890ff" />
        </checkbox-group>
        <text class="policy-text">I have read the</text>
        <text class="policy-link" @tap="goPrivacy">Legal and Privacy Policy</text>
      </view>

      <!-- 登录按钮 -->
      <button 
        class="login-btn" 
        :class="{'login-btn-active': canLogin}"
        :disabled="!canLogin"
        @tap="doLogin"
      >Login</button>

      <!-- 游客登录按钮 -->
      <button 
        class="guest-login-btn"
        @tap="doGuestLogin"
      >Login as Guest</button>

      <!-- 注册按钮 -->
      <button 
        class="register-btn"
        @tap="goRegister"
      >Create Account</button>

      <!-- 登录遇到问题 -->
      <view class="login-help">
        <text @tap="goHelp">Having trouble logging in?</text>
      </view>

      <!-- 其他登录方式 -->
      <view class="other-login">
        <text class="other-title">Other Login Methods</text>
        <view class="login-methods">
          <view class="method-item" @tap="otherLogin('alipay')">
            <image src="/static/icons/alipay.png" mode="aspectFit"></image>
          </view>
          <view class="method-item" @tap="otherLogin('apple')">
            <image src="/static/icons/apple.png" mode="aspectFit"></image>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, computed } from 'vue'
import { API_BASE_URL } from '@/config/index'

// 基础数据
const email = ref('1550320492@qq.com')
const password = ref('123456')
const showPwd = ref(false)
const isAgree = ref(false)
const msg = ref('')
const msgType = ref('')

// 是否可以登录
const canLogin = computed(() => {
  return email.value && password.value.length >= 6 && isAgree.value
})

// 显示/隐藏密码
const togglePwd = () => {
  showPwd.value = !showPwd.value
}

// 同意协议
const onAgreeChange = (e) => {
  isAgree.value = e.detail.value.length > 0
}

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

// 登录
const doLogin = async () => {
  if (!canLogin.value) return
  
  if (!email.value || !password.value) {
    showMsg('Please enter email and password', 'error')
    return
  }

  try {
    const res = await uni.request({
      url: `${API_BASE_URL}/user/login`,
      method: 'POST',
      data: {
        email: email.value,
        password: password.value,
        role: 'user'
      },
      header: {
        'Content-Type': 'application/json'
      }
    })
    console.log(res);
    if (res.data.statusCode === 200) {
      // 存储用户信息
      const userData = {
        id: res.data.data.user.id,
        name: res.data.data.user.name,
        email: res.data.data.user.email,
        card: res.data.data.user.card,
        createTime: res.data.data.user.createTime,
        discount: res.data.data.user.discount,
        role: res.data.data.user.role
      }
      
      // 存入本地
      uni.setStorageSync('userInfo', JSON.stringify(userData))
      // 存储token
      uni.setStorageSync('token', res.data.data.token)
      
      showMsg('Login successful', 'success')
      
      // 延迟跳转
      setTimeout(() => {
        uni.switchTab({
          url: '/pages/index/index'
        })
      }, 1000)
    } else {
      showMsg('Incorrect email or password', 'error')
    }
  } catch (err) {
    console.error('Login failed:', err)
    showMsg('Network error, please try again', 'error')
  }
}

// 显示消息
const showMsg = (text, type) => {
  msg.value = text
  msgType.value = type
}

// 去注册
const goRegister = () => {
  uni.navigateTo({ url: '/pages/register/register' })
}

// 查看协议
const goPrivacy = () => {
  uni.navigateTo({ url: '/pages/privacy/privacy' })
}

// 遇到问题
const goHelp = () => {
  uni.navigateTo({ url: '/pages/login-help/login-help' })
}

// 第三方登录
const otherLogin = (type) => {
  uni.showToast({
    title: `${type} Login`,
    icon: 'none'
  })
}

// 游客登录
const doGuestLogin = async () => {
  try {
    // 生成随机游客名称
    const guestName = `Guest_${Math.floor(Math.random() * 10000)}`
    const guestEmail = `${guestName.toLowerCase()}@guest.com`
    const guestPassword = '123456'

    // 先注册游客账号
    const registerRes = await uni.request({
      url: `${API_BASE_URL}/user`,
      method: 'POST',
      data: {
        name: guestName,
        email: guestEmail,
        password: guestPassword,
        role: 'user'
      },
      header: {
        'Content-Type': 'application/json'
      }
    })

    if (registerRes.data.statusCode === 200) {
      // 注册成功后直接登录
      const loginRes = await uni.request({
        url: `${API_BASE_URL}/user/login`,
        method: 'POST',
        data: {
          email: guestEmail,
          password: guestPassword,
          role: 'user'
        },
        header: {
          'Content-Type': 'application/json'
        }
      })

      if (loginRes.data.statusCode === 200) {
        // 存储用户信息
        const userData = {
          id: loginRes.data.data.user.id,
          name: loginRes.data.data.user.name,
          email: loginRes.data.data.user.email,
          card: loginRes.data.data.user.card,
          createTime: loginRes.data.data.user.createTime,
          discount: loginRes.data.data.user.discount,
          role: loginRes.data.data.user.role
        }
        
        // 存入本地
        uni.setStorageSync('userInfo', JSON.stringify(userData))
        // 存储token
        uni.setStorageSync('token', loginRes.data.data.token)
        
        showMsg('Login successful', 'success')
        
        // 延迟跳转
        setTimeout(() => {
          uni.switchTab({
            url: '/pages/index/index'
          })
        }, 1000)
      }
    }
  } catch (err) {
    console.error('Guest login failed:', err)
    showMsg('Guest login failed, please try again', 'error')
  }
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
  height: 100rpx;
  background: linear-gradient(45deg, #1890ff, #36b4ff);
  border-radius: 50rpx;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  margin-top: 60rpx;
  opacity: 0.6;
  transition: all 0.3s;
}

.login-btn-active {
  opacity: 1;
  box-shadow: 0 8rpx 24rpx rgba(24,144,255,0.3);
}

.login-btn:active {
  transform: scale(0.98);
  box-shadow: 0 4rpx 12rpx rgba(24,144,255,0.2);
}

.guest-login-btn {
  width: 100%;
  height: 100rpx;
  background: #f5f5f5;
  border-radius: 50rpx;
  color: #666;
  font-size: 32rpx;
  margin-top: 30rpx;
  transition: all 0.3s;
}

.guest-login-btn:active {
  background: #e8e8e8;
  transform: scale(0.98);
}

.register-btn {
  width: 100%;
  height: 100rpx;
  background: transparent;
  border: 2rpx solid #1890ff;
  border-radius: 50rpx;
  color: #1890ff;
  font-size: 32rpx;
  margin-top: 30rpx;
  transition: all 0.3s;
}

.register-btn:active {
  background: rgba(24,144,255,0.05);
  transform: scale(0.98);
}

/* 登录帮助 */
.login-help {
  text-align: center;
  margin-top: 40rpx;
}

.login-help text {
  font-size: 26rpx;
  color: #666;
  text-decoration: underline;
}

/* 其他登录方式 */
.other-login {
  margin-top: 80rpx;
  text-align: center;
}

.other-title {
  font-size: 28rpx;
  color: #999;
  margin-bottom: 40rpx;
  display: block;
}

.login-methods {
  display: flex;
  justify-content: center;
  gap: 60rpx;
}

.method-item {
  width: 100rpx;
  height: 100rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.method-item:first-child {
  background-color: #1677ff;
}

.method-item:last-child {
  background-color: #000000;
}

.method-item:active {
  transform: scale(0.95);
}

.method-item image {
  width: 50rpx;
  height: 50rpx;
  object-fit: contain;
}
</style> 