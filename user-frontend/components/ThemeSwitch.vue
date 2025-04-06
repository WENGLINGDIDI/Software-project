<template>
  <view class="theme-switch" @tap="toggleTheme">
    <view class="switch-icon">
      <uni-icons :type="isDark ? 'moon' : 'sun'" size="24" :color="isDark ? '#ffffff' : '#333333'"></uni-icons>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'

const isDark = ref(false)

// 初始化主题
onMounted(() => {
  // 获取系统主题
  uni.getSystemInfo({
    success: (res) => {
      isDark.value = res.theme === 'dark'
      applyTheme(isDark.value)
    }
  })
})

// 切换主题
const toggleTheme = () => {
  isDark.value = !isDark.value
  applyTheme(isDark.value)
}

// 应用主题
const applyTheme = (dark) => {
  const root = document.documentElement
  if (dark) {
    root.classList.add('theme-dark')
    root.classList.remove('theme-light')
  } else {
    root.classList.add('theme-light')
    root.classList.remove('theme-dark')
  }
  // 存储主题偏好
  uni.setStorageSync('theme', dark ? 'dark' : 'light')
}

// 监听系统主题变化
watch(isDark, (newVal) => {
  applyTheme(newVal)
})
</script>

<style lang="scss">
.theme-switch {
  padding: 10rpx;
  border-radius: 50%;
  background-color: var(--card-background);
  box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  &:active {
    transform: scale(0.95);
  }
}

.switch-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 48rpx;
  height: 48rpx;
}
</style> 