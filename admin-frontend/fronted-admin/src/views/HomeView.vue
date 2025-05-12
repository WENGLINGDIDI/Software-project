<template>
  <div>
    <el-container>
      <el-aside :width="asideWidth">
        <div style="height: 60px; line-height: 60px; font-size: 20px; display: flex; align-items: center; justify-content: center">
          <i class="el-icon-s-platform" style="font-size: 30px; color: #42b983;"></i>
          <span class="logo-title" v-show="!isCollapse">Rental Website</span>
        </div>
        <el-menu router :collapse="isCollapse" :collapse-transition="false" background-color="#001529"
                 active-text-color="#fff" text-color="rgba(255, 255, 255, 0.65)" :default-active="$route.path"
                 style="border: none">
          <el-menu-item index="/">
            <i class="el-icon-s-home"></i>
            <span slot="title">System Home</span>
          </el-menu-item>
          <el-menu-item index="/user">
            <i class="el-icon-user"></i>
            <span slot="title">User Management</span>
          </el-menu-item>
          <el-menu-item index="/feedback">
            <i class="el-icon-message"></i>
            <span slot="title">Feedback</span>
          </el-menu-item>
          <el-menu-item index="/scooter">
            <i class="el-icon-bicycle"></i>
            <span slot="title">Scooter Configuration</span>
          </el-menu-item>
          <el-menu-item index="/station">
            <i class="el-icon-location"></i>
            <span slot="title">Station Configuration</span>
          </el-menu-item>
          <el-menu-item index="/order">
            <i class="el-icon-s-order"></i>
            <span slot="title">Order Management</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <el-container>
        <el-header style="height: 60px; line-height: 60px; display: flex; align-items: center; box-shadow: 2px 0 6px rgba(0, 21, 41, .35);">
          <i :class="collapseIcon" @click="handleCollapse" style="font-size: 26px"></i>
          <el-breadcrumb separator="/" style="margin-left: 20px">
            <el-breadcrumb-item :to="{ path: '/' }">Home</el-breadcrumb-item>
  <el-breadcrumb-item :to="{ path: $route.path }">{{ $route.meta.title }}</el-breadcrumb-item>
</el-breadcrumb>

          <div style="flex: 1; display: flex; justify-content: flex-end; align-items: center">
            <i class="el-icon-quanping" @click="handleFullScreen" style="font-size: 25px"></i>
            <el-dropdown placement="bottom">
              <div style="display: flex; align-items: center; cursor: pointer">
                <div style="width: 40px; height: 40px; border-radius: 50%; background: #e6f7f1; display: flex; align-items: center; justify-content: center; margin: 0 5px;">
                  <i class="el-icon-user" style="font-size: 24px; color: #42b983;"></i>
                </div>

              </div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="logout">Logout</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>

        <el-main>
          <router-view></router-view>
          <!-- Only show charts on the actual home page -->
          <div v-if="$route.path === '/'" class="dashboard-container">
            <!-- Statistics Card Overview -->
            <el-row :gutter="24" class="stat-overview">
              <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                <div class="stat-card">
                  <div class="stat-card-header">
                    <div class="stat-icon icon-users">
                      <i class="el-icon-user-solid"></i>
                    </div>
                    <div class="stat-info">
                      <div class="stat-title">Total Users</div>
                      <div class="stat-value">{{ userCount }}</div>
                    </div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                <div class="stat-card">
                  <div class="stat-card-header">
                    <div class="stat-icon icon-income">
                      <i class="el-icon-money"></i>
                    </div>
                    <div class="stat-info">
                      <div class="stat-title">Total Income</div>
                      <div class="stat-value">${{ totalIncome.toLocaleString() }}</div>
                    </div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                <div class="stat-card">
                  <div class="stat-card-header">
                    <div class="stat-icon icon-orders">
                      <i class="el-icon-s-order"></i>
                    </div>
                    <div class="stat-info">
                      <div class="stat-title">Total Orders</div>
                      <div class="stat-value">{{ orderCount }}</div>
                    </div>
                  </div>
                </div>
              </el-col>
              <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
                <div class="stat-card">
                  <div class="stat-card-header">
                    <div class="stat-icon icon-scooters">
                      <i class="el-icon-bicycle"></i>
                    </div>
                    <div class="stat-info">
                      <div class="stat-title">Total Scooters</div>
                      <div class="stat-value">{{ scooterCount }}</div>
                    </div>
                  </div>
                </div>
              </el-col>
            </el-row>

            <!-- Chart Area: Two rows, two columns -->
            <el-row :gutter="24" class="chart-row">
              <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                <div class="chart-card">
                  <div class="chart-header">
                    <span class="chart-title">Rental Duration Income Statistics</span>
                    <div class="chart-actions">
                      <el-button type="text" icon="el-icon-refresh" size="small" @click="fetchWeeklyData">Refresh</el-button>
                    </div>
                  </div>
                  <div id="pieChart" class="chart-container"></div>
                  <el-table
                    :data="rentalData"
                    stripe
                    border
                    class="income-table"
                    :header-cell-style="{ background: '#f5f7fa', color: '#606266' }">
                    <el-table-column prop="duration" label="Rental Duration" align="center"></el-table-column>
                    <el-table-column prop="income" label="Income" align="center">
                      <template slot-scope="scope">
                        <span class="income-value">￥{{ scope.row.income.toFixed(2) }}</span>
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </el-col>
              <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                <div class="chart-card">
                  <div class="chart-header">
                    <span class="chart-title">Weekly Income Chart</span>
                    <div class="chart-actions">
                      <el-button type="text" icon="el-icon-refresh" size="small" @click="fetchWeeklyData">Refresh</el-button>
                    </div>
                  </div>
                  <div id="weeklyChart" class="chart-container small-chart"></div>
                </div>
                <div class="chart-card" style="margin-top: 20px;">
                  <div class="chart-header">
                    <span class="chart-title">Total Income by Rental Price (Yearly)</span>
                    <div class="chart-actions">
                      <el-button type="text" icon="el-icon-refresh" size="small" @click="fetchYearlyData">Refresh</el-button>
                    </div>
                  </div>
                  <div id="yearLineChart" class="chart-container"></div>
                </div>
              </el-col>
            </el-row>
          </div>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'HomeView',
  data () {
    return {
      isCollapse: false,
      asideWidth: '200px',
      collapseIcon: 'el-icon-s-fold',
      user: {},
      rentalData: [],
      weeklyChart: null,
      pieChart: null,
      userCount: 0,
      totalIncome: 0,
      orderCount: 0,
      scooterCount: 0,
      dashboardLoading: false,
      weeklyLoading: false,
      chartCategories: [],
      chartSeries: [],
      yearLineChart: null,
      yearLineMonths: [],
      yearLineRentalOptions: [],
      yearLineSeries: [],
      yearLoading: false,
      yearLineOptionLabel: null
    }
  },
  watch: {
    '$route.path': {
      handler (newPath) {
        if (newPath === '/') {
          this.$nextTick(async () => {
            await this.fetchDashboardStats()
            await this.fetchWeeklyData()
            await this.fetchYearlyData()
            // Reinitialize charts after data is fetched
            this.$nextTick(() => {
              if (this.weeklyChart) {
                this.weeklyChart.dispose()
                this.weeklyChart = null
              }
              if (this.pieChart) {
                this.pieChart.dispose()
                this.pieChart = null
              }
              if (this.yearLineChart) {
                this.yearLineChart.dispose()
                this.yearLineChart = null
              }
              this.initChart(this.rentalData)
              this.initYearLineChart()
            })
          })
        }
      },
      immediate: true
    }
  },
  created () {
    const userStr = localStorage.getItem('user')
    if (!userStr) {
      this.$router.replace('/login')
    } else {
      this.user = JSON.parse(userStr)
    }
  },
  mounted () {
    this.fetchDashboardStats()
    this.fetchWeeklyData()
    this.fetchYearlyData()
  },
  methods: {
    handleFullScreen () {
      document.documentElement.requestFullscreen()
    },
    handleCollapse () {
      this.isCollapse = !this.isCollapse
      this.asideWidth = this.isCollapse ? '64px' : '200px'
      this.collapseIcon = this.isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'
    },
    handleRouteChange () {
      if (this.$route.path === '/') {
        if (this.rentalData.length === 0) {
          this.fetchWeeklyData()
        } else if (this.weeklyChart && this.pieChart) {
          this.$nextTick(() => {
            this.weeklyChart.resize()
            this.pieChart.resize()
          })
        }
      }
    },
    logout () {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      this.$router.push('/login')
    },
    async fetchDashboardStats () {
      this.dashboardLoading = true
      try {
        // 获取用户数
        const userRes = await this.$request.get('/admin/user')
        if (userRes.statusCode === 200) {
          this.userCount = userRes.data.length
        } else {
          this.$message.error('Failed to load user count')
        }
      } catch (e) {
        this.userCount = 0
        this.$message.error('Failed to load user count')
      }
      try {
        // 获取订单数
        const orderRes = await this.$request.get('/admin/book/all')
        if (orderRes.statusCode === 200) {
          this.orderCount = orderRes.data.length
        } else {
          this.$message.error('Failed to load order count')
        }
      } catch (e) {
        this.orderCount = 0
        this.$message.error('Failed to load order count')
      }
      try {
        // 获取电动车数
        const scooterRes = await this.$request.get('/admin/scooter')
        if (scooterRes.statusCode === 200) {
          this.scooterCount = scooterRes.data.length
        } else {
          this.$message.error('Failed to load scooter count')
        }
      } catch (e) {
        this.scooterCount = 0
        this.$message.error('Failed to load scooter count')
      }
      try {
        // 获取总收入
        const weekRes = await this.$request.get('/admin/week')
        if (weekRes.statusCode === 200) {
          this.totalIncome = Object.values(weekRes.data).reduce((sum, v) => sum + (parseFloat(v) || 0), 0)
        } else {
          this.$message.error('Failed to load income')
        }
      } catch (e) {
        this.totalIncome = 0
        this.$message.error('Failed to load income')
      }
      this.dashboardLoading = false
    },
    async fetchWeeklyData () {
      this.weeklyLoading = true
      try {
        const response = await this.$request.get('/admin/week')
        if (response && response.statusCode === 200) {
          const incomeData = response.data
          // 动态生成表格和图表数据
          const durationMap = {
            1: '1 h',
            4: '4 h',
            24: '1 day (24 h)',
            168: '1 week (168 h)'
          }
          // 动态生成表格数据
          this.rentalData = Object.keys(incomeData).map(key => ({
            duration: durationMap[key] || key + ' h',
            income: incomeData[key] || 0
          }))
          // 动态生成x轴和legend
          this.chartCategories = Object.keys(incomeData).map(key => durationMap[key] || key + ' h')
          this.chartSeries = Object.values(incomeData).map(v => v || 0)
          this.initChart(incomeData)
          this.$message({
            message: response.message || 'Income statistics loaded successfully',
            type: 'success',
            duration: 2000
          })
        } else {
          throw new Error(response?.message || 'Failed to fetch income statistics')
        }
      } catch (error) {
        this.$message.error(error.message || 'Failed to fetch income statistics, please check network connection')
      }
      this.weeklyLoading = false
    },
    async fetchYearlyData () {
      this.yearLoading = true
      try {
        const response = await this.$request.get('/admin/year')
        if (response && response.statusCode === 200) {
          const data = response.data
          // data: { '1': { '1': val, '4': val, ... }, '2': {...}, ... }
          // Get all months (x-axis)
          const months = Object.keys(data)
          // Get all unique rental options (lines)
          const rentalOptionsSet = new Set()
          months.forEach(month => {
            Object.keys(data[month] || {}).forEach(opt => rentalOptionsSet.add(opt))
          })
          const rentalOptions = Array.from(rentalOptionsSet).sort((a, b) => Number(a) - Number(b))
          // Helper for friendly label
          const optionLabel = (opt) => {
            if (opt === '1') return '1 hour'
            if (opt === '4') return '4 hours'
            if (opt === '24') return '1 day (24 hours)'
            if (opt === '168') return '1 week (168 hours)'
            if (Number(opt) < 24) return opt + ' hours'
            if (Number(opt) % 24 === 0) return (Number(opt) / 24) + ' days (' + opt + ' hours)'
            return opt + ' hours'
          }
          // Build series data for each rental option
          const series = rentalOptions.map(opt => ({
            name: optionLabel(opt),
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: { width: 3 },
            itemStyle: { borderColor: '#fff', borderWidth: 2 },
            data: months.map(month => (data[month] && data[month][opt]) ? data[month][opt] : 0)
          }))
          // Store for chart rendering
          this.yearLineMonths = months
          this.yearLineRentalOptions = rentalOptions
          this.yearLineSeries = series
          this.yearLineOptionLabel = optionLabel

          // Only initialize if we're on the home page
          if (this.$route.path === '/') {
            this.$nextTick(() => {
              this.initYearLineChart()
            })
          }
        } else {
          throw new Error(response?.message || 'Failed to fetch yearly income data')
        }
      } catch (error) {
        this.$message.error(error.message || 'Failed to fetch yearly income data')
      }
      this.yearLoading = false
    },
    initChart (data) {
      if (this.$route.path !== '/') return
      const chartDom = document.getElementById('weeklyChart')
      if (!chartDom) return
      this.weeklyChart = echarts.init(chartDom)
      const categories = this.chartCategories || []
      const seriesData = this.chartSeries || []
      const barColors = [
        ['#36D1DC', '#5B86E5'],
        ['#FF9A9E', '#FECFEF'],
        ['#FFDB01', '#FFB800'],
        ['#A1C4FD', '#C2E9FB']
      ]
      const option = {
        title: {
          text: 'Rental Duration Income Comparison',
          left: 'center',
          top: 10,
          textStyle: {
            fontSize: 16,
            fontWeight: 500,
            color: '#303133'
          }
        },
        tooltip: {
          trigger: 'axis',
          formatter: '{b}: ¥{c}',
          backgroundColor: 'rgba(255, 255, 255, 0.9)',
          borderColor: '#ebeef5',
          borderWidth: 1,
          textStyle: {
            color: '#606266'
          },
          extraCssText: 'box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);'
        },
        xAxis: {
          type: 'category',
          data: categories,
          axisLine: {
            lineStyle: {
              color: '#DCDFE6'
            }
          },
          axisLabel: {
            color: '#606266'
          }
        },
        yAxis: {
          type: 'value',
          name: 'Income',
          nameTextStyle: {
            color: '#909399'
          },
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          axisLabel: {
            color: '#606266'
          },
          splitLine: {
            lineStyle: {
              color: '#EBEEF5'
            }
          }
        },
        series: [
          {
            data: seriesData.map((v, i) => ({
              value: v,
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: barColors[i % barColors.length][0] },
                  { offset: 1, color: barColors[i % barColors.length][1] }
                ])
              }
            })),
            type: 'bar',
            showBackground: true,
            backgroundStyle: {
              color: 'rgba(180, 180, 180, 0.1)'
            },
            barWidth: '40%',
            borderRadius: 6
          }
        ],
        grid: {
          top: 50,
          left: 50,
          right: 30,
          bottom: 20,
          containLabel: true
        }
      }
      this.weeklyChart.setOption(option)
      // 饼图
      const pieDom = document.getElementById('pieChart')
      if (!pieDom) return
      this.pieChart = echarts.init(pieDom)
      const pieColors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de']
      const pieOption = {
        title: {
          text: 'Income Distribution',
          left: 'center',
          top: 0,
          textStyle: {
            fontSize: 16,
            fontWeight: 500,
            color: '#303133'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: ¥{c} ({d}%)',
          backgroundColor: 'rgba(255, 255, 255, 0.9)',
          borderColor: '#ebeef5',
          borderWidth: 1,
          textStyle: {
            color: '#606266'
          },
          extraCssText: 'box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);'
        },
        legend: {
          orient: 'horizontal',
          bottom: 0,
          itemWidth: 10,
          itemHeight: 10,
          textStyle: {
            color: '#606266'
          },
          data: categories
        },
        series: [
          {
            name: 'Income Distribution',
            type: 'pie',
            radius: ['40%', '65%'],
            center: ['50%', '52%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 6,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: true,
              formatter: '{b}: {d}%',
              color: '#606266'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              },
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.2)'
              }
            },
            labelLine: {
              show: true
            },
            data: categories.map((name, i) => ({
              value: seriesData[i],
              name,
              itemStyle: { color: pieColors[i % pieColors.length] }
            }))
          }
        ]
      }
      this.pieChart.setOption(pieOption)
      this.resizeCharts = () => {
        if (this.weeklyChart) this.weeklyChart.resize()
        if (this.pieChart) this.pieChart.resize()
      }
      window.addEventListener('resize', this.resizeCharts)
    },
    initYearLineChart () {
      const chartDom = document.getElementById('yearLineChart')
      if (!chartDom) return

      // Dispose of existing chart instance if it exists
      if (this.yearLineChart) {
        this.yearLineChart.dispose()
      }

      this.yearLineChart = echarts.init(chartDom)
      const colorList = ['#36D1DC', '#FF9A9E', '#FFDB01', '#A1C4FD', '#91cc75', '#fac858', '#ee6666', '#73c0de']
      const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
      const monthsLabel = this.yearLineMonths.map(m => monthNames[Number(m) - 1] || m)
      const option = {
        title: {
          text: 'Total Income by Rental Option (Yearly)',
          left: 'center',
          top: 10,
          textStyle: {
            fontSize: 16,
            fontWeight: 500,
            color: '#303133'
          }
        },
        tooltip: {
          trigger: 'axis',
          formatter: params => {
            let str = params[0].axisValue + '<br/>'
            params.forEach(p => {
              str += `<span style='display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background:${p.color}'></span> ${p.seriesName}: $${p.value.toFixed(2)}<br/>`
            })
            return str
          },
          backgroundColor: 'rgba(255, 255, 255, 0.9)',
          borderColor: '#ebeef5',
          borderWidth: 1,
          textStyle: { color: '#606266' },
          extraCssText: 'box-shadow: 0 0 8px rgba(0, 0, 0, 0.1);'
        },
        legend: {
          data: this.yearLineSeries.map(s => s.name),
          top: 40
        },
        xAxis: {
          type: 'category',
          data: monthsLabel,
          axisLine: { lineStyle: { color: '#DCDFE6' } },
          axisLabel: { color: '#606266' }
        },
        yAxis: {
          type: 'value',
          name: '',
          nameTextStyle: { color: '#909399' },
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: { color: '#606266' },
          splitLine: { lineStyle: { color: '#EBEEF5' } }
        },
        series: this.yearLineSeries.map((s, i) => ({ ...s, lineStyle: { ...s.lineStyle, color: colorList[i % colorList.length] }, itemStyle: { ...s.itemStyle, color: colorList[i % colorList.length] } })),
        grid: {
          top: 80,
          left: 50,
          right: 30,
          bottom: 30,
          containLabel: true
        }
      }
      this.yearLineChart.setOption(option)
      window.addEventListener('resize', () => {
        if (this.yearLineChart) this.yearLineChart.resize()
      })
    }
  },
  beforeDestroy () {
    if (this.resizeCharts) {
      window.removeEventListener('resize', this.resizeCharts)
    }

    if (this.weeklyChart) {
      this.weeklyChart.dispose()
      this.weeklyChart = null
    }
    if (this.pieChart) {
      this.pieChart.dispose()
      this.pieChart = null
    }
    if (this.yearLineChart) {
      this.yearLineChart.dispose()
      this.yearLineChart = null
    }
  }
}
</script>

<style>
.el-menu--inline, .el-menu-item {
  background-color: #000c17 !important;
}

.el-submenu__title {
  height: 40px !important;
  line-height: 40px !important;
  padding: 0 25px !important;
  transition: color 0s;
}
.el-menu--collapse .el-submenu__title {
  padding: 0 20px !important;
}
.el-submenu__title>i:nth-child(1) {
  margin-top: 2px;
}
.el-submenu__title>i.el-submenu__icon-arrow {
  margin-top: -5px;
}
.el-menu-item {
  min-width: 0 !important;
  width: calc(100% - 10px);
  margin: 5px;
  height: 40px !important;
  line-height: 40px !important;
  border-radius: 5px;
}
.el-menu--inline>.el-menu-item {
  padding-left: 50px !important;
}
.el-menu-item.is-active {
  background-color: #1890ff !important;
}

.el-menu-item:hover {
  color: #fff !important;
}

.el-submenu__title:hover *, .el-submenu__title:hover {
  color: #fff !important;
}

.el-aside {
  box-shadow: 2px 0 6px rgba(0, 21, 41, .35);
  background-color: #001529;
  color: #fff;
  min-height: 100vh;
  transition: width .3s;
}
.el-menu--collapse .el-tooltip {
  padding: 0 15px !important;
}
.logo-title {
  margin-left: 5px;
  transition: all .3s;
  font-weight: 600;
}
.el-menu {
  transition: all .3s;
}

/* Dashboard Styles - Enhanced Version */
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

/* Statistics Card Area */
.stat-overview {
  margin-bottom: 24px;
}

.stat-card {
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  height: 100%;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  display: flex;
  flex-direction: column;
}

.stat-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.stat-card-header {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 12px;
  font-size: 24px;
}

.icon-users {
  background: linear-gradient(135deg, #36d1dc, #5b86e5);
  color: white;
}

.icon-income {
  background: linear-gradient(135deg, #f093fb, #f5576c);
  color: white;
}

.icon-orders {
  background: linear-gradient(135deg, #ffb347, #ffcc33);
  color: white;
}

.icon-scooters {
  background: linear-gradient(135deg, #43cea2, #185a9d);
  color: white;
}

.stat-info {
  flex-grow: 1;
}

.stat-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  line-height: 1;
}

.stat-footer {
  margin-top: 16px;
  font-size: 12px;
  display: flex;
  align-items: center;
}

.growth-positive {
  color: #67c23a;
}

.growth-negative {
  color: #f56c6c;
}

/* Chart Area */
.chart-row {
  margin-bottom: 24px;
}

.chart-card {
  height: 100%;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  margin-bottom: 20px;
}

.chart-card:hover {
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.1);
}

.chart-header {
  padding: 16px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #ebeef5;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-actions {
  display: flex;
  gap: 8px;
}

.chart-container {
  height: 300px;
  width: 100%;
  padding: 10px 20px 20px;
}

.small-chart {
  height: 200px;
}

.income-table {
  margin: 0 20px 10px;
}

.income-table .el-table th {
  background-color: #f5f7fa;
  color: #606266;
}

.income-value {
  color: #f56c6c;
  font-weight: 600;
}

/* Responsive Adjustments */
@media (max-width: 768px) {
  .stat-card {
    margin-bottom: 16px;
  }

  .chart-container {
    height: 280px;
  }
}
</style>
