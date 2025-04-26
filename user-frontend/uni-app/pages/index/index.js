<template>
  <view class="container">
    <!-- 顶部导航栏 -->
    <view class="header" :class="{ 'header-dark': isDarkMode }">
      <view class="header-center">
        <text class="title">蓉城出行</text>
        <text class="subtitle">放心租</text>
      </view>
    </view>

    <!-- 租赁列表 -->
    <view class="rental-list">
      <view class="rental-item" v-for="(item, index) in rentalItems" :key="index">
        <view class="rental-header">
          <view class="rental-title">
            <text class="shop-name">【{{item.shopName}}】</text>
            <text class="location-name">{{item.locationName}}</text>
          </view>
          <text class="distance">{{item.distance}}km</text>
        </view>
        <view class="rental-content">
          <view class="scooter-info">
            <image :src="item.image" mode="aspectFit" class="scooter-image"></image>
            <view class="scooter-details">
              <text class="scooter-name">{{item.scooterName}}</text>
              <view class="tags">
                <text class="tag" v-for="(tag, tagIndex) in item.tags" :key="tagIndex">{{tag}}</text>
              </view>
            </view>
          </view>
          <view class="price-section">
            <text class="price">月租 ¥{{item.price}}</text>
            <button class="rent-button">免费送车上门</button>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部导航栏 -->
    <view class="tab-bar">
      <view class="tab-item active">
        <view class="tab-icon">
          <text class="iconfont icon-home"></text>
        </view>
        <text>租车</text>
      </view>
      <view class="tab-item">
        <view class="tab-icon">
          <text class="iconfont icon-user"></text>
        </view>
        <text>我的</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'


// 租赁项目数据
const rentalItems = ref([
  {
    shopName: '众鑫',
    locationName: '全城免费配送-群星路站',
    distance: '1.3',
    scooterName: '通勤长续航-可换电',
    image: '/static/images/scooter1.png',
    price: '249',
    tags: ['无限换电', '价格透明', '信用免押']
  },
  {
    shopName: '众鑫',
    locationName: '全城免费配送-犀浦地铁站',
    distance: '1.3',
    scooterName: '通勤长续航-可换电',
    image: '/static/images/scooter1.png',
    price: '249',
    tags: ['无限换电', '价格透明', '信用免押']
  }
])

// 深色模式状态
const isDarkMode = ref(false)

// 检查系统主题
onMounted(() => {
  // 获取系统主题
  uni.getSystemInfo({
    success: (res) => {
      isDarkMode.value = res.theme === 'dark'
    }
  })
})

// 处理导航点击
const handleNavClick = (item) => {
  uni.showToast({
    title: `点击了${item.title}`,
    icon: 'none'
  })
}
</script>

<style lang="scss">
/* 引入字体图标 */
@font-face {
  font-family: 'iconfont';
  src: url('//at.alicdn.com/t/font_3237479_q75o5h9jgyj.woff2') format('woff2'),
       url('//at.alicdn.com/t/font_3237479_q75o5h9jgyj.woff') format('woff'),
       url('//at.alicdn.com/t/font_3237479_q75o5h9jgyj.ttf') format('truetype');
}

.iconfont {
  font-family: "iconfont" !important;
  font-size: 24px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.icon-home:before {
  content: "\e6b8";
}

.icon-list:before {
  content: "\e682";
}

.icon-user:before {
  content: "\e7ae";
}

.container {
  min-height: 100vh;
  background-color: #f7f8fa;
}

/* 顶部导航栏样式 */
.header {
  padding: 40rpx 20rpx 20rpx;
  background: linear-gradient(135deg, #1890ff, #36cfc9);
  color: #ffffff;
}

.header-dark {
  background: linear-gradient(135deg, #096dd9, #08979c);
}

.header-center {
  text-align: center;
}

.title {
  font-size: 36rpx;
  font-weight: bold;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

.subtitle {
  font-size: 24rpx;
  margin-left: 10rpx;
  opacity: 0.9;
}

/* 导航区域样式 */
.nav-section {
  display: flex;
  justify-content: space-around;
  padding: 30rpx;
  background-color: #ffffff;
  border-radius: 20rpx;
  margin: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.nav-item {
  text-align: center;
}

.nav-icon-wrapper {
  width: 88rpx;
  height: 88rpx;
  background: linear-gradient(135deg, #e6f7ff, #bae7ff);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
}

.nav-icon {
  width: 48rpx;
  height: 48rpx;
}

.nav-text {
  font-size: 24rpx;
  color: #333;
  font-weight: 500;
}

/* 租赁列表样式 */
.rental-list {
  padding: 0 20rpx;
  margin-bottom: 120rpx; /* 为底部导航栏留出空间 */
}

.rental-item {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.05);
}

.rental-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.shop-name {
  font-weight: bold;
  color: #1890ff;
}

.location-name {
  color: #666;
  font-size: 26rpx;
}

.distance {
  color: #1890ff;
  font-size: 24rpx;
  background: #e6f7ff;
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
}

.scooter-info {
  display: flex;
  align-items: center;
}

.scooter-image {
  width: 200rpx;
  height: 200rpx;
  margin-right: 20rpx;
  border-radius: 12rpx;
}

.scooter-name {
  font-size: 28rpx;
  font-weight: 500;
  color: #333;
}

.tags {
  display: flex;
  flex-wrap: wrap;
  margin-top: 12rpx;
}

.tag {
  background: #f0f5ff;
  color: #1890ff;
  padding: 4rpx 12rpx;
  border-radius: 10rpx;
  margin-right: 10rpx;
  margin-bottom: 8rpx;
  font-size: 22rpx;
}

.price-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20rpx;
}

.price {
  color: #f5222d;
  font-size: 32rpx;
  font-weight: bold;
}

.rent-button {
  background: linear-gradient(135deg, #1890ff, #36cfc9);
  color: #ffffff;
  padding: 12rpx 32rpx;
  border-radius: 30rpx;
  font-size: 26rpx;
  border: none;
  box-shadow: 0 4rpx 12rpx rgba(24, 144, 255, 0.3);
}

/* 底部导航栏样式 */
.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background-color: #ffffff;
  display: flex;
  justify-content: space-around;
  align-items: center;
  border-top: 1rpx solid #f0f0f0;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
  font-size: 24rpx;
  padding: 10rpx 0;
}

.tab-icon {
  margin-bottom: 4rpx;
}

.tab-item.active {
  color: #1890ff;
}

.tab-item.active .iconfont {
  color: #1890ff;
}
</style>