const express = require('express')
const cors = require('cors')
const app = express()
const port = 3000
const { weeklyIncomeData, scenarios, scooterData } = require('./mock-data')

// 启用 CORS
app.use(cors())

// 设置返回 JSON 格式的中间件
app.use(express.json())

// 模拟周收入数据接口
app.get('/admin/week', (req, res) => {
  // 获取查询参数中的场景名称，默认为 'balanced'
  const scenario = req.query.scenario || 'balanced'

  // 设置正确的 Content-Type
  res.setHeader('Content-Type', 'application/json')

  // 延迟 500ms 返回，模拟网络延迟
  setTimeout(() => {
    if (scenario === 'error') {
      return res.status(500).json(scenarios.error)
    } else if (scenarios[scenario]) {
      return res.json(scenarios[scenario])
    } else {
      return res.json(weeklyIncomeData)
    }
  }, 500)
})

// 模拟电动车数据接口
app.get('/admin/scooter', (req, res) => {
  // 设置正确的 Content-Type
  res.setHeader('Content-Type', 'application/json')

  // 延迟 500ms 返回，模拟网络延迟
  setTimeout(() => {
    return res.json(scooterData)
  }, 500)
})

// 添加一个测试端点
app.get('/test', (req, res) => {
  res.json({ message: 'Mock server is working properly' })
})

// 启动服务器
app.listen(port, () => {
  console.log(`Mock API server running at http://localhost:${port}`)
  console.log('Available endpoints:')
  console.log('- /admin/week - 周收入数据')
  console.log('- /admin/scooter - 电动车配置数据')
  console.log('- /test - 服务器测试')
  console.log('\nAvailable scenarios for /admin/week:')
  console.log(Object.keys(scenarios).map(s => `- ${s}`).join('\n'))
})
