<template>
  <view class="container">
    <!-- 顶部导航栏 -->
    <view class="header" :class="{ dark: isDark }">
      <view class="header-center">
        <text class="title">Rongcheng Travel</text>
        <text class="sub">Safe Rental</text>
      </view>
    </view>

    <!-- 地图容器 -->
    <view class="map-container">
      <map
        id="map"
        class="map"
        :latitude="mapState.latitude"
        :longitude="mapState.longitude"
        :markers="mapState.markers"
        :scale="16"
        show-location
      ></map>
    </view>

    <!-- 租赁列表 -->
    <view class="list">
      <view 
        class="item" 
        v-for="item in rentList" 
        :key="item.shopName + item.locationName"
      >
        <view class="item-header">
          <view class="left">
            <text class="shop">【{{item.shopName}}】</text>
            <text class="addr">{{item.locationName}}</text>
          </view>
          <text class="dist">{{item.distance}}km</text>
        </view>
        <view class="item-body">
          <view class="info">
            <image :src="item.image" mode="aspectFit" class="pic"></image>
            <view class="detail">
              <text class="name">{{item.scooterName}}</text>
              <view class="tags">
                <text 
                  class="tag" 
                  v-for="tag in item.tags" 
                  :key="tag"
                >{{tag}}</text>
              </view>
            </view>
          </view>
          <view class="price-box">
            <text class="price">Month Rent ¥30</text>
            <button class="btn" @tap="goToRent(item)">Rental</button>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部导航栏 -->
  <!--  <view class="tab-bar">
      <view class="tab-item">
        <view class="icon">
          <text class="iconfont icon-home"></text>
        </view>
        <text>Rental</text>
      </view>
      <view class="tab-item" @tap="goToUser">
        <view class="icon">
          <text class="iconfont icon-user"></text>
        </view>
        <text>My</text>
      </view>
    </view> -->
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { API_BASE_URL } from '@/config/index'

// 地图状态
const mapState = ref({
  latitude: 30.657420,  // 成都市中心纬度
  longitude: 104.065840,  // 成都市中心经度
  markers: [
    {
      id: 1,
      latitude: 30.6586,
      longitude: 104.0757,
      iconPath: '/static/icons/location.png',
      width: 40,
      height: 40,
      callout: {
        content: 'Current Location',
        color: '#000000',
        fontSize: 14,
        borderRadius: 4,
        padding: 5,
        display: 'ALWAYS'
      }
    },
    {
      id: 2,
      latitude: 30.6678,
      longitude: 104.0873,
      iconPath: '/static/icons/location.png',
      width: 40,
      height: 40,
      callout: {
        content: 'Chunxi Road Station',
        color: '#000000',
        fontSize: 14,
        borderRadius: 4,
        padding: 5,
        display: 'ALWAYS'
      }
    },
    {
      id: 3,
      latitude: 30.7048,
      longitude: 104.0731,
      iconPath: '/static/icons/location.png',
      width: 40,
      height: 40,
      callout: {
        content: 'Shuangshui Street Station',
        color: '#000000',
        fontSize: 14,
        borderRadius: 4,
        padding: 5,
        display: 'ALWAYS'
      }
    },
    {
      id: 4,
      latitude: 30.6383,
      longitude: 104.0526,
      iconPath: '/static/icons/location.png',
      width: 40,
      height: 40,
      callout: {
        content: 'Yulin Street Station',
        color: '#000000',
        fontSize: 14,
        borderRadius: 4,
        padding: 5,
        display: 'ALWAYS'
      }
    }
  ]
})

// 防止重复获取位置
let isGettingLocation = false

// 租赁数据
const rentList = ref([
  {
    shopName: 'Chunxi Road Station',
    locationName: 'Chunxi Road Station',
    distance: '1.3',
    scooterName: 'Long-range Commuting-Battery Swap',
    image: '/static/images/car1.jpeg',
    price: '249',
    tags: ['Unlimited Battery Swap', 'Transparent Price', 'Credit-Free'],
    latitude: 30.6586,
    longitude: 104.0757
  },
  {
    shopName: 'Academy Street Station',
    locationName: 'Academy Street Station',
    distance: '1.5',
    scooterName: 'Long-range Commuting-Battery Swap',
    image: '/static/images/car2.jpg',
    price: '249',
    tags: ['Unlimited Battery Swap', 'Transparent Price', 'Credit-Free'],
    latitude: 30.6678,
    longitude: 104.0873
  },
  {
    shopName: 'Shuangshui Street Station',
    locationName: 'Shuangshui Street Station',
    distance: '2.0',
    scooterName: 'Long-range Commuting-Battery Swap',
    image: '/static/images/car1.jpeg',
    price: '249',
    tags: ['Unlimited Battery Swap', 'Transparent Price', 'Credit-Free'],
    latitude: 30.7048,
    longitude: 104.0731
  },
  {
    shopName: 'Yulin Street Station',
    locationName: 'Yulin Street Station',
    distance: '1.8',
    scooterName: 'Long-range Commuting-Battery Swap',
    image: '/static/images/car2.jpg',
    price: '249',
    tags: ['Unlimited Battery Swap', 'Transparent Price', 'Credit-Free'],
    latitude: 30.6383,
    longitude: 104.0526
  }
])

// 深色模式
const isDark = ref(false)

// 跳转到租赁页面
const goToRent = (item) => {
  try {
    // 从 rentList 中找到对应的站点数据
    const stationData = rentList.value.find(station => 
      station.shopName === item.shopName
    )
    
    if (!stationData) {
      throw new Error('未找到站点数据')
    }

    const queryParams = encodeURIComponent(JSON.stringify({
      shopName: item.shopName,
      locationName: item.locationName,
      scooterName: item.scooterName,
      price: item.price,
      image: item.image,
      scooters: stationData.scooters || [] // 添加默认空数组
    }))

    uni.navigateTo({
      url: `/pages/rent/rent?data=${queryParams}`
    })
  } catch (error) {
    console.error('跳转租赁页面失败:', error)
    uni.showToast({
      title: '跳转失败，请重试',
      icon: 'none'
    })
  }
}

// 获取位置信息
const getLocation = async () => {
  if (isGettingLocation) return
  isGettingLocation = true

  try {
    // 使用高德地图IP定位服务
    const response = await uni.request({
      url: 'https://restapi.amap.com/v3/ip',
      data: {
        key: 'ca477bb891ebf75dc880f2688b9cf324',
        output: 'json'
      }
    })

    if (response.data.status === '1' && response.data.city) {
      const geoResponse = await uni.request({
        url: 'https://restapi.amap.com/v3/geocode/geo',
        data: {
          key: 'ca477bb891ebf75dc880f2688b9cf324',
          address: response.data.city,
          output: 'json'
        }
      })

      if (geoResponse.data.status === '1' && geoResponse.data.geocodes?.length > 0) {
        const location = geoResponse.data.geocodes[0].location.split(',')
        const longitude = parseFloat(location[0])
        const latitude = parseFloat(location[1])

        // 更新中心点位置，但保留原有的站点标记
        mapState.value = {
          latitude,
          longitude,
          markers: [
            // 当前位置标记
            {
              id: 0,
              latitude,
              longitude,
              iconPath: '/static/icons/location.png',
              width: 40,
              height: 40,
              callout: {
                content: 'Current Location',
                color: '#000000',
                fontSize: 14,
                borderRadius: 4,
                padding: 5,
                display: 'ALWAYS'
              }
            },
            // 站点标记
            {
              id: 1,
              latitude: 30.6586,
              longitude: 104.0757,
              iconPath: '/static/icons/location.png',
              width: 40,
              height: 40,
              callout: {
                content: 'Chunxi Road Station',
                color: '#000000',
                fontSize: 14,
                borderRadius: 4,
                padding: 5,
                display: 'ALWAYS'
              }
            },
            {
              id: 2,
              latitude: 30.6678,
              longitude: 104.0873,
              iconPath: '/static/icons/location.png',
              width: 40,
              height: 40,
              callout: {
                content: 'Academy Street Station',
                color: '#000000',
                fontSize: 14,
                borderRadius: 4,
                padding: 5,
                display: 'ALWAYS'
              }
            },
            {
              id: 3,
              latitude: 30.7048,
              longitude: 104.0731,
              iconPath: '/static/icons/location.png',
              width: 40,
              height: 40,
              callout: {
                content: 'Shuangshui Street Station',
                color: '#000000',
                fontSize: 14,
                borderRadius: 4,
                padding: 5,
                display: 'ALWAYS'
              }
            },
            {
              id: 4,
              latitude: 30.6383,
              longitude: 104.0526,
              iconPath: '/static/icons/location.png',
              width: 40,
              height: 40,
              callout: {
                content: 'Yulin Street Station',
                color: '#000000',
                fontSize: 14,
                borderRadius: 4,
                padding: 5,
                display: 'ALWAYS'
              }
            }
          ]
        }
      }
    }
  } catch (err) {
    console.error('位置获取失败：', err)
    uni.showToast({
      title: 'Using default location (Chengdu)',
      icon: 'none',
      duration: 2000
    })
  } finally {
    isGettingLocation = false
  }
}

// 修改数据获取函数
const fetchStationAndScooter = async () => {
  try {
    const response = await uni.request({
      url: `${API_BASE_URL}/admin/stationAndScooter`,
      method: 'GET',
      header: {
        'Content-Type': 'application/json'
      }
    })

    // uni.request 的响应数据在 response.data 中
    if (response.statusCode === 200 && response.data.statusCode === 200) {
      // 处理返回的数据
      const stationsData = response.data.data
      
      // 更新地图标记
      const newMarkers = stationsData.map((item, index) => ({
        id: index + 1,
        latitude: item.station.latitude,
        longitude: item.station.longitude,
        iconPath: '/static/icons/location.png',
        width: 40,
        height: 40,
        callout: {
          content: item.station.name,
          color: '#000000',
          fontSize: 14,
          borderRadius: 4,
          padding: 5,
          display: 'ALWAYS'
        }
      }))

      // 更新地图标记，保留当前位置标记
      mapState.value.markers = [
        mapState.value.markers[0], // 保留当前位置标记
        ...newMarkers
      ]

      // 更新租赁列表数据
      rentList.value = stationsData.map(item => {
        // 获取该站点可用的电动车数量
        const availableScooters = item.scooters.filter(scooter => scooter.status === 0).length

        return {
          shopName: item.station.name,
          locationName: item.station.name,
          distance: calculateDistance(
            mapState.value.latitude,
            mapState.value.longitude,
            item.station.latitude,
            item.station.longitude
          ).toFixed(1),
          scooterName: 'Long-range Commuting-Battery Swap',
          image: '/static/images/car1.jpeg',
          price: '249',
          tags: [
            `Available Vehicles: ${availableScooters}`,
            'Unlimited Battery Swap',
            'Transparent Price',
            'Credit-Free'
          ],
          latitude: item.station.latitude,
          longitude: item.station.longitude,
          scooters: item.scooters // 添加电动车信息
        }
      })
    } else {
      throw new Error('获取数据失败')
    }
  } catch (err) {
    console.error('获取站点和电动车数据失败：', err)
    uni.showToast({
      title: 'Failed to get data, using default data',
      icon: 'none'
    })
    // 保持使用模拟数据
  }
}

// 添加计算距离的辅助函数
const calculateDistance = (lat1, lon1, lat2, lon2) => {
  const R = 6371 // 地球半径，单位km
  const dLat = (lat2 - lat1) * Math.PI / 180
  const dLon = (lon2 - lon1) * Math.PI / 180
  const a = 
    Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * 
    Math.sin(dLon/2) * Math.sin(dLon/2)
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
  return R * c
}

// 初始化地图
const initMap = async () => {
  try {
    await getLocation()
  } catch (err) {
    console.error('地图初始化失败：', err)
    uni.showToast({
      title: 'Map initialization failed, please try again',
      icon: 'none'
    })
  }
}

// 生命周期钩子
onMounted(() => {
  initMap()
  fetchStationAndScooter() // 添加数据获取
})

// 页面显示时更新位置
onShow(() => {
  if (!isGettingLocation) {
    getLocation()
    fetchStationAndScooter() // 每次显示页面时刷新数据
  }
})

// 页面跳转
const goToUser = () => {
  uni.switchTab({
    url: '/pages/user/user'
  })
}

// 事件处理
const onNavTap = (item) => {
  uni.showToast({
    title: `Clicked ${item.title}`,
    icon: 'none'
  })
}

const onPromoTap = (type) => {
  uni.showToast({
    title: `Clicked ${type} promotion`,
    icon: 'none'
  })
}
</script>

<style>
page { background: #f7f8fa; }

.container {
  min-height: 100vh;
}

/* 头部 */
.header {
  padding: 40rpx 20rpx 20rpx;
  background: #0050b3; /* 加深蓝色以提高对比度 */
}

.header.dark {
  background: #003a8c; /* 加深深色模式背景色 */
}

.header-center {
  text-align: center;
  color: #ffffff;
}

.header .title {
  font-size: 36rpx;
  font-weight: bold;
}

.header .sub {
  font-size: 24rpx;
  margin-left: 10rpx;
  opacity: 1; /* 提高不透明度 */
}

/* 地图样式 */
.map-container {
  width: 100%;
  height: 500rpx;
  position: relative;
  z-index: 1;
}

.map {
  width: 100%;
  height: 100%;
}

/* 调整导航区域样式 */
.nav {
  margin-top: -60rpx;
  position: relative;
  z-index: 2;
}

/* 导航 */
.nav {
  display: flex;
  justify-content: space-around;
  padding: 30rpx;
  background: #ffffff;
  border-radius: 20rpx;
  margin: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.1);
}

.nav-item {
  text-align: center;
}

.nav-icon-box {
  width: 88rpx;
  height: 88rpx;
  background: #e6f7ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 12rpx;
}

.nav-item .icon {
  width: 48rpx;
  height: 48rpx;
}

.nav-item .text {
  font-size: 24rpx;
  color: #262626; /* 加深文本颜色 */
}

/* 促销区 */
.promo {
  display: flex;
  justify-content: space-between;
  padding: 0 20rpx;
  margin-bottom: 20rpx;
}

.promo-card {
  width: 48%;
  height: 200rpx;
  background: #fff8e1;
  border-radius: 20rpx;
  padding: 20rpx;
  position: relative;
  overflow: hidden;
}

.promo-card.buy {
  background: #ffe0e0;
}

/* 列表 */
.list {
  padding: 0 20rpx;
  margin-bottom: 120rpx;
}

.list .item {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.1);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.item-header .shop {
  font-weight: bold;
  color: #0050b3; /* 加深蓝色 */
}

.item-header .addr {
  color: #434343; /* 加深灰色 */
  font-size: 26rpx;
}

.item-header .dist {
  color: #0050b3; /* 加深蓝色 */
  font-size: 24rpx;
  background: #e6f7ff;
  padding: 4rpx 12rpx;
  border-radius: 20rpx;
}

.info {
  display: flex;
  align-items: center;
}

.info .pic {
  width: 200rpx;
  height: 200rpx;
  margin-right: 20rpx;
  border-radius: 12rpx;
}

.info .name {
  font-size: 28rpx;
  font-weight: 500;
  color: #262626; /* 加深文本颜色 */
}

.tags {
  display: flex;
  flex-wrap: wrap;
  margin-top: 12rpx;
}

.tag {
  background: #e6f7ff;
  color: #0050b3; /* 加深蓝色 */
  padding: 4rpx 12rpx;
  border-radius: 10rpx;
  margin-right: 10rpx;
  margin-bottom: 8rpx;
  font-size: 22rpx;
}

.price-box {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20rpx;
}

.price {
  color: #cf1322; /* 加深红色 */
  font-size: 32rpx;
  font-weight: bold;
}

.btn {
  background: #0050b3; /* 加深蓝色 */
  color: #ffffff;
  padding: 12rpx 32rpx;
  border-radius: 30rpx;
  font-size: 26rpx;
  border: none;
}

/* 底部导航栏 */
.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background: rgba(255,255,255,0.98);
  display: flex;
  justify-content: space-around;
  align-items: center;
  border-top: 1rpx solid rgba(0,0,0,0.1);
  padding-bottom: env(safe-area-inset-bottom);
  backdrop-filter: blur(10px);
}

.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #595959; /* 加深灰色 */
  font-size: 20rpx;
  padding: 10rpx 0;
  transition: all 0.3s;
}

.tab-item:active {
  transform: scale(0.95);
}

.tab-item .iconfont {
  font-size: 40rpx;
  margin-bottom: 4rpx;
}

.tab-item.active {
  color: #0050b3; /* 加深蓝色 */
  font-weight: 500;
}

.tab-item.active .iconfont {
  text-shadow: none;
}

/* 字体图标 */
@font-face {
  font-family: 'iconfont';
  src: url('//at.alicdn.com/t/font_3237479_q75o5h9jgyj.woff2') format('woff2'),
       url('//at.alicdn.com/t/font_3237479_q75o5h9jgyj.woff') format('woff'),
       url('//at.alicdn.com/t/font_3237479_q75o5h9jgyj.ttf') format('truetype');
}

.iconfont {
  font-family: "iconfont";
  font-size: 24px;
  font-style: normal;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.icon-home:before { content: "\e6b8"; }
.icon-list:before { content: "\e682"; }
.icon-user:before { content: "\e7ae"; }
</style> 