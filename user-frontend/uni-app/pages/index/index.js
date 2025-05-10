Page({
  data: {
    amapKey: "7f8490d93fc22edd10cc066c1395d741", // 在这里配置高德地图 API Key
    latitude: 39.90923,
    longitude: 116.397428
  },

  onLoad() {
    // 获取用户位置
    wx.getLocation({
      type: "gcj02",
      success: (res) => {
        this.setData({
          latitude: res.latitude,
          longitude: res.longitude
        });
      },
      fail: () => {
        wx.showToast({ title: "获取位置失败", icon: "none" });
      }
    });
  },

  // 点击地图事件
  onMapTap(event) {
    console.log("点击了地图，坐标：", event.detail);
  }
});
