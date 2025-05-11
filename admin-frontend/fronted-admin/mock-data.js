// 模拟周收入数据
const weeklyIncomeData = {
  statusCode: 200,
  message: 'success',
  data: {
    1: 350.0, // 1小时租赁收入
    4: 850.0, // 4小时租赁收入
    24: 1200.0, // 1天租赁收入
    168: 2800.0 // 1周租赁收入
  }
}

// 模拟电动车数据
const scooterData = {
  statusCode: 200,
  message: 'success',
  data: [
    {
      id: 1,
      latitude: 30.666182541698344,
      longitude: 104.06927120541933,
      status: 0,
      power: 69,
      config: 'Speed: 30 km/h, 包含头盔与手机支架'
    },
    {
      id: 2,
      latitude: 30.656472877030765,
      longitude: 104.0742647406009,
      status: 0,
      power: 86,
      config: 'Speed: 30 km/h, 包含头盔与手机支架'
    },
    {
      id: 3,
      latitude: 30.651060567669667,
      longitude: 104.05666759366696,
      status: 0,
      power: 5,
      config: 'Speed: 30 km/h, 包含头盔与手机支架'
    },
    {
      id: 4,
      latitude: 30.655123456789,
      longitude: 104.07123456789,
      status: 1,
      power: 42,
      config: 'Speed: 25 km/h, 包含头盔与手机支架, 防滑轮胎'
    },
    {
      id: 5,
      latitude: 30.659876543210,
      longitude: 104.06876543210,
      status: 0,
      power: 93,
      config: 'Speed: 35 km/h, 包含头盔与手机支架, LED照明'
    },
    {
      id: 6,
      latitude: 30.662345678901,
      longitude: 104.05345678901,
      status: 0,
      power: 12,
      config: 'Speed: 30 km/h, 包含头盔与手机支架, 蓝牙音箱'
    }
  ]
}

// 模拟不同场景的数据
const scenarios = {
  // 平衡场景 - 各时长租赁较为均衡
  balanced: {
    statusCode: 200,
    message: 'success',
    data: {
      1: 350.0,
      4: 850.0,
      24: 1200.0,
      168: 2800.0
    }
  },

  // 短租场景 - 短时租赁占主导
  'short-term': {
    statusCode: 200,
    message: 'success',
    data: {
      1: 980.0,
      4: 1450.0,
      24: 650.0,
      168: 400.0
    }
  },

  // 长租场景 - 长时租赁占主导
  'long-term': {
    statusCode: 200,
    message: 'success',
    data: {
      1: 120.0,
      4: 450.0,
      24: 1850.0,
      168: 4200.0
    }
  },

  // 无收入场景 - 用于测试零值处理
  'no-income': {
    statusCode: 200,
    message: 'success',
    data: {
      1: 0.0,
      4: 0.0,
      24: 0.0,
      168: 0.0
    }
  },

  // 错误场景 - 用于测试错误处理
  error: {
    statusCode: 500,
    message: 'server error',
    data: null
  }
}

module.exports = {
  weeklyIncomeData,
  scenarios,
  scooterData
}
