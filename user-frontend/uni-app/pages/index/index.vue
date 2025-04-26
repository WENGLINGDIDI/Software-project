<template>
  <view class="container">
    <!-- 顶部导航栏 -->
    <view class="header" :class="{ dark: isDark }">
      <view class="header-center">
        <text class="title">蓉城出行</text>
        <text class="sub">放心租</text>
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
            <text class="price">月租 ¥{{item.price}}</text>
            <button class="btn" @tap="goToRent(item)">租赁</button>
          </view>
        </view>
      </view>
    </view>

    <!-- 底部导航栏 -->
    <view class="tabs">
      <view class="tab active">
        <view class="icon">
          <text class="iconfont icon-home"></text>
        </view>
        <text>租车</text>
      </view>
      <view class="tab" @tap="goToUser">
        <view class="icon">
          <text class="iconfont icon-user"></text>
        </view>
        <text>我的</text>
      </view>
    </view>
  </view>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import axios from 'axios'
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
        content: '春熙路街道站',
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
        content: '书院街街道站',
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
        content: '双水碾街道站',
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
        content: '玉林街道站',
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
    shopName: '春熙路',
    locationName: '春熙路',
    distance: '1.3',
    scooterName: '通勤长续航-可换电',
    image: '/static/images/car1.jpeg',
    price: '249',
    tags: ['无限换电', '价格透明', '信用免押'],
    latitude: 30.6586,
    longitude: 104.0757
  },
  {
    shopName: '书院街街道站',
    locationName: '书院街街道站',
    distance: '1.5',
    scooterName: '通勤长续航-可换电',
    image: '/static/images/car2.jpg',
    price: '249',
    tags: ['无限换电', '价格透明', '信用免押'],
    latitude: 30.6678,
    longitude: 104.0873
  },
  {
    shopName: '双水碾街道站',
    locationName: '双水碾街道站',
    distance: '2.0',
    scooterName: '通勤长续航-可换电',
    image: '/static/images/car1.jpeg',
    price: '249',
    tags: ['无限换电', '价格透明', '信用免押'],
    latitude: 30.7048,
    longitude: 104.0731
  },
  {
    shopName: '玉林街道站',
    locationName: '玉林街道站',
    distance: '1.8',
    scooterName: '通勤长续航-可换电',
    image: '/static/images/car2.jpg',
    price: '249',
    tags: ['无限换电', '价格透明', '信用免押'],
    latitude: 30.6383,
    longitude: 104.0526
  }
])

// 深色模式
const isDark = ref(false)

// 跳转到租赁页面
const goToRent = (item) => {
  uni.navigateTo({
    url: `/pages/rent/rent?shopName=${item.shopName}&locationName=${item.locationName}&scooterName=${item.scooterName}&price=${item.price}&image=${item.image}`
  })
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
                content: '当前位置',
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
                content: '春熙路街道站',
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
                content: '书院街街道站',
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
                content: '双水碾街道站',
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
                content: '玉林街道站',
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
      title: '已使用默认位置（成都市）',
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
    const response = await axios.get(`${API_BASE_URL}/admin/stationAndScooter`)
	console.log(response);
    if (response.data.statusCode === 200) {
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
          scooterName: '通勤长续航-可换电',
          image: '/static/images/car1.jpeg',
          price: '249',
          tags: [
            `可用车辆: ${availableScooters}`,
            '无限换电',
            '价格透明',
            '信用免押'
          ],
          latitude: item.station.latitude,
          longitude: item.station.longitude
        }
      })
    } else {
      throw new Error('获取数据失败')
    }
  } catch (err) {
    console.error('获取站点和电动车数据失败：', err)
    uni.showToast({
      title: '获取数据失败，使用默认数据',
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
      title: '地图初始化失败，请重试',
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
    title: `点击了${item.title}`,
    icon: 'none'
  })
}

const onPromoTap = (type) => {
  uni.showToast({
    title: `点击了${type}活动`,
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
  background: #1890ff;
}

.header.dark {
  background: #096dd9;
}

.header-center {
  text-align: center;
  color: #fff;
}

.header .title {
  font-size: 36rpx;
  font-weight: bold;
}

.header .sub {
  font-size: 24rpx;
  margin-left: 10rpx;
  opacity: 0.9;
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
  background: #fff;
  border-radius: 20rpx;
  margin: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05);
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
  color: #333;
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
  background: #fff;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0,0,0,0.05);
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20rpx;
}

.item-header .shop {
  font-weight: bold;
  color: #1890ff;
}

.item-header .addr {
  color: #666;
  font-size: 26rpx;
}

.item-header .dist {
  color: #1890ff;
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

.price-box {
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

.btn {
  background: #1890ff;
  color: #fff;
  padding: 12rpx 32rpx;
  border-radius: 30rpx;
  font-size: 26rpx;
  border: none;
}

/* 底部导航 */
.tabs {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100rpx;
  background: #fff;
  display: flex;
  justify-content: space-around;
  align-items: center;
  border-top: 1rpx solid #f0f0f0;
}

.tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #999;
  font-size: 24rpx;
  padding: 10rpx 0;
}

.tab .icon {
  margin-bottom: 4rpx;
}

.tab.active {
  color: #1890ff;
}

.tab.active .iconfont {
  color: #1890ff;
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