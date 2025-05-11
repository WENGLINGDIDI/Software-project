<template>
  <div class="orders-container">
    <!-- Global Active Orders Map -->
    <el-card class="global-map-card" shadow="hover">
      <div slot="header" class="station-card-header">
        <span class="section-title">Active Orders Distribution</span>
        <div class="header-actions">
          <el-button type="primary" @click="refreshActiveOrders" icon="el-icon-refresh">Refresh Map</el-button>
        </div>
      </div>
      <div id="globalMapContainer" class="global-map-container"></div>
    </el-card>

    <!-- Order Table Area -->
    <div class="orders-card">
      <div class="chart-header">
        <span class="chart-title">Order List</span>
        <div class="chart-actions">
          <el-button type="primary" icon="el-icon-plus" @click="showAddOrderDialog">New Order</el-button>
          <el-button type="text" icon="el-icon-refresh" size="small" @click="fetchUserOrders">Refresh</el-button>
        </div>
      </div>

      <el-tabs v-model="orderActiveTab" @tab-click="handleTabChange">
        <el-tab-pane label="All Orders" name="all"></el-tab-pane>
        <el-tab-pane label="Reserved" name="reserved"></el-tab-pane>
        <el-tab-pane label="Activated" name="activated"></el-tab-pane>
        <el-tab-pane label="Completed" name="completed"></el-tab-pane>
        <el-tab-pane label="Cancelled" name="cancelled"></el-tab-pane>
      </el-tabs>

      <el-table
        v-loading="ordersLoading"
        :data="filteredOrders"
        stripe
        border
        class="orders-table"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }">
        <el-table-column prop="id" label="Order ID" width="80" align="center"></el-table-column>
        <el-table-column prop="user.name" label="User" width="120" align="center">
          <template slot-scope="scope">
            <div class="user-cell">
              <i class="el-icon-user"></i>
              <span>{{ scope.row.user.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="scooter.station.name" label="Station" width="150" align="center">
          <template slot-scope="scope">
            <el-tag size="medium" type="info" class="station-tag">{{ scope.row.scooter.station.name }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="hireOption.hours" label="Rental Duration" width="120" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.hireOption.hours }} hours</span>
            <span v-if="scope.row.extend_time > 0" class="extend-tag">
              (+{{ scope.row.extend_time }})
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="totalCost" label="Cost" width="100" align="center">
          <template slot-scope="scope">
            <span class="cost-value">${{ scope.row.totalCost.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="Status" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getOrderStatusType(scope.row.status)" class="status-tag">
              {{ getOrderStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payed" label="Payment Status" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.payed === 1 ? 'success' : 'danger'" class="status-tag">
              {{ scope.row.payed === 1 ? 'Paid' : 'Unpaid' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Time" align="center">
          <template slot-scope="scope">
            <div class="time-cell">
              <div>Start: {{ formatDateTime(scope.row.startTime) }}</div>
              <div>End: {{ formatDateTime(scope.row.endTime) }}</div>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="no-data" v-if="filteredOrders.length === 0 && !ordersLoading">
        <i class="el-icon-tickets"></i>
        <span>No Order Data</span>
      </div>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="totalOrders"
        class="pagination">
      </el-pagination>
    </div>

    <!-- Add Order Dialog -->
    <el-dialog
      title="Create New Order"
      :visible.sync="addOrderDialogVisible"
      width="50%"
      @close="resetOrderForm">
      <el-form :model="orderForm" :rules="orderRules" ref="orderForm" label-width="120px">
        <el-form-item label="Station" prop="stationId">
          <el-select v-model="orderForm.stationId" placeholder="Select a station" @change="handleStationChange">
            <el-option
              v-for="station in stations"
              :key="station.id"
              :label="station.name"
              :value="station.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Scooter" prop="scooterId">
          <el-select v-model="orderForm.scooterId" placeholder="Select a scooter" :disabled="!orderForm.stationId">
            <el-option
              v-for="scooter in availableScooters"
              :key="scooter.id"
              :label="`Scooter #${scooter.id} (Power: ${scooter.power}%, Config: ${scooter.config})`"
              :value="scooter.id">
              <span style="float: left">Scooter #{{ scooter.id }}</span>
              <span style="float: right; color: #8492a6; font-size: 13px">
                Power: {{ scooter.power }}%
              </span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Rental Duration" prop="hireOptionId">
          <el-select v-model="orderForm.hireOptionId" placeholder="Select rental duration" @change="handleDurationChange">
            <el-option
              v-for="option in hireOptions"
              :key="option.id"
              :label="`${option.hours} hours - $${option.money}`"
              :value="option.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Start Time" prop="startTime">
          <el-date-picker
            v-model="orderForm.startTime"
            type="datetime"
            placeholder="Select start time"
            value-format="yyyy-MM-dd HH:mm:ss"
            @change="calculateEndTime">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="End Time" prop="endTime">
          <el-date-picker
            v-model="orderForm.endTime"
            type="datetime"
            placeholder="End time will be calculated automatically"
            :disabled="true"
            value-format="yyyy-MM-dd HH:mm:ss">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addOrderDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitOrder" :loading="submitLoading">Create Order</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import AMapLoader from '@amap/amap-jsapi-loader'

export default {
  name: 'OrderView',
  data () {
    return {
      userOrders: [],
      ordersLoading: false,
      orderActiveTab: 'all',
      currentPage: 1,
      pageSize: 10,
      totalOrders: 0,
      filteredOrders: [],
      globalMap: null,
      globalMarkers: [],
      amapConfig: {
        key: 'cc37cd44f45c414717314a0ff733348c',
        securityJsCode: 'bedfa07de37192f7db48417f208c7db2'
      },
      activeOrders: [],
      addOrderDialogVisible: false,
      orderForm: {
        userId: null,
        stationId: null,
        scooterId: null,
        hireOptionId: null,
        startTime: '',
        endTime: ''
      },
      orderRules: {
        stationId: [{ required: true, message: 'Please select a station', trigger: 'change' }],
        scooterId: [{ required: true, message: 'Please select a scooter', trigger: 'change' }],
        hireOptionId: [{ required: true, message: 'Please select a rental duration', trigger: 'change' }],
        startTime: [{ required: true, message: 'Please select a start time', trigger: 'change' }],
        endTime: [{ required: true, message: 'Please select an end time', trigger: 'change' }]
      },
      submitLoading: false,
      stations: [],
      availableScooters: [],
      hireOptions: [],
      stationAndScooterData: [],
      selectedDuration: null
    }
  },
  mounted () {
    // Add security configuration
    window._AMapSecurityConfig = {
      securityJsCode: this.amapConfig.securityJsCode
    }
    this.fetchUserOrders()
    // Initialize global map
    this.$nextTick(() => {
      this.initGlobalMap()
    })
  },
  beforeDestroy () {
    // Destroy global map
    if (this.globalMap) {
      this.globalMap.destroy()
      this.globalMap = null
      this.globalMarkers = []
    }
  },
  methods: {
    // Get order status text
    getOrderStatusText (status) {
      const statusMap = {
        1: 'Reserved',
        2: 'Activated',
        3: 'Completed',
        4: 'Cancelled'
      }
      return statusMap[status] || 'Unknown Status'
    },

    // Get order status tag type
    getOrderStatusType (status) {
      const typeMap = {
        1: 'info', // Reserved
        2: 'warning', // Activated
        3: 'success', // Completed
        4: 'danger' // Cancelled
      }
      return typeMap[status] || 'info'
    },

    // Get user order data
    async fetchUserOrders () {
      this.ordersLoading = true
      try {
        const response = await this.$request.get('/admin/book/all')
        if (response.statusCode === 200) {
          let orders = response.data
          // 根据tab筛选不同status
          if (this.orderActiveTab === 'reserved') {
            orders = orders.filter(order => order.status === 1)
          } else if (this.orderActiveTab === 'activated') {
            orders = orders.filter(order => order.status === 2)
          } else if (this.orderActiveTab === 'completed') {
            orders = orders.filter(order => order.status === 3)
          } else if (this.orderActiveTab === 'cancelled') {
            orders = orders.filter(order => order.status === 4)
          }
          const startIndex = (this.currentPage - 1) * this.pageSize
          const endIndex = startIndex + this.pageSize
          this.filteredOrders = orders.slice(startIndex, endIndex)
          this.totalOrders = orders.length
        } else {
          throw new Error(response.message || 'Failed to fetch order data')
        }
      } catch (error) {
        console.error('Failed to fetch order data:', error)
        this.$message.error(error.message || 'Failed to fetch order data')
      } finally {
        this.ordersLoading = false
      }
    },

    // Format date time
    formatDateTime (dateTimeStr) {
      if (!dateTimeStr) return '--'

      const date = new Date(dateTimeStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')

      return `${year}-${month}-${day} ${hours}:${minutes}`
    },

    handleTabChange (tab) {
      this.orderActiveTab = tab.name
      this.currentPage = 1
      this.fetchUserOrders()
    },

    handleSizeChange (size) {
      this.pageSize = size
      this.currentPage = 1
      this.fetchUserOrders()
    },

    handleCurrentChange (page) {
      this.currentPage = page
      this.fetchUserOrders()
    },

    // Initialize global map - show all active orders
    async initGlobalMap () {
      try {
        const AMap = await AMapLoader.load({
          key: this.amapConfig.key,
          version: '2.0',
          plugins: ['AMap.ToolBar', 'AMap.Scale']
        })
        // 调试：检查AMap对象
        console.log('AMap loaded:', AMap)

        const container = document.getElementById('globalMapContainer')
        if (!container) {
          throw new Error('Global map container does not exist')
        }

        if (!this.globalMap) {
          this.globalMap = new AMap.Map('globalMapContainer', {
            zoom: 11,
            center: [120.15, 30.25],
            viewMode: '2D'
          })

          this.globalMap.addControl(new AMap.ToolBar())
          this.globalMap.addControl(new AMap.Scale())
        }

        this.clearGlobalMarkers()

        // Get all activated orders and show their scooters on map
        const response = await this.$request.get('/admin/book/all')
        if (response.statusCode === 200) {
          // 只显示status为2（Activated）的订单
          this.activeOrders = response.data.filter(order => order.status === 2)
          this.activeOrders.forEach(order => {
            console.log('Scooter coordinates:', order.scooter && order.scooter.latitude, order.scooter && order.scooter.longitude)
            if (order.scooter && order.scooter.latitude && order.scooter.longitude) {
              const marker = new AMap.Marker({
                position: [order.scooter.longitude, order.scooter.latitude],
                map: this.globalMap,
                title: `Order #${order.id}`,
                cursor: 'pointer',
                animation: 'AMAP_ANIMATION_DROP'
              })

              const infoWindow = new AMap.InfoWindow({
                content: this.createInfoWindowContent(order),
                offset: new AMap.Pixel(0, -30)
              })

              marker.on('click', () => {
                infoWindow.open(this.globalMap, marker.getPosition())
              })

              this.globalMarkers.push(marker)
            }
          })

          this.globalMap.setFitView()
        }
      } catch (error) {
        console.error('Failed to load global map:', error)
        this.$message.error('Failed to load global map')
      }
    },

    // Clear global map markers
    clearGlobalMarkers () {
      this.globalMarkers.forEach(marker => {
        marker.setMap(null)
      })
      this.globalMarkers = []
    },

    // Create info window content for global map
    createInfoWindowContent (order) {
      return `
        <div style="padding: 10px;">
          <h4 style="margin: 0 0 5px 0;">Order #${order.id}</h4>
          <p style="margin: 0;">User: ${order.user.name}</p>
          <p style="margin: 5px 0;">Scooter ID: ${order.scooter.id}</p>
          <p style="margin: 5px 0;">Start Time: ${this.formatDateTime(order.startTime)}</p>
        </div>
      `
    },

    // Refresh active orders on map
    async refreshActiveOrders () {
      await this.initGlobalMap()
      this.$message.success('Active orders map refreshed')
    },

    showAddOrderDialog () {
      this.addOrderDialogVisible = true
      // Get user ID from localStorage
      const user = JSON.parse(localStorage.getItem('user'))
      this.orderForm.userId = user.id

      // Fetch stations and hire options when dialog opens
      this.fetchStations()
      this.fetchHireOptions()
    },

    async fetchStations () {
      try {
        const response = await this.$request.get('/admin/stationAndScooter')
        if (response.statusCode === 200) {
          // Store the full response data for later use
          this.stationAndScooterData = response.data
          // Extract stations for the station select
          this.stations = response.data.map(item => item.station)
        } else {
          throw new Error(response.message || 'Failed to fetch stations')
        }
      } catch (error) {
        console.error('Failed to fetch stations:', error)
        this.$message.error('Failed to fetch stations')
      }
    },

    async handleStationChange (stationId) {
      this.orderForm.scooterId = null // Reset scooter selection
      if (!stationId) return

      try {
        // Find the selected station's data from our stored data
        const stationData = this.stationAndScooterData.find(item => item.station.id === stationId)
        if (stationData) {
          // Filter only available scooters (status === 0)
          this.availableScooters = stationData.scooters.filter(scooter => scooter.status === 0)
          if (this.availableScooters.length === 0) {
            this.$message.warning('No available scooters at this station')
          }
        } else {
          throw new Error('Station not found')
        }
      } catch (error) {
        console.error('Failed to get available scooters:', error)
        this.$message.error('Failed to get available scooters')
        this.availableScooters = [] // Reset available scooters on error
      }
    },

    async fetchHireOptions () {
      try {
        const response = await this.$request.get('/admin/hireOption')
        if (response.statusCode === 200) {
          this.hireOptions = response.data
        } else {
          throw new Error(response.message || 'Failed to fetch hire options')
        }
      } catch (error) {
        console.error('Failed to fetch hire options:', error)
        this.$message.error('Failed to fetch hire options')
      }
    },

    handleDurationChange (hireOptionId) {
      this.selectedDuration = this.hireOptions.find(option => option.id === hireOptionId)
      this.calculateEndTime()
    },

    calculateEndTime () {
      if (this.selectedDuration && this.orderForm.startTime) {
        const startDate = new Date(this.orderForm.startTime)
        const endDate = new Date(startDate.getTime())
        endDate.setHours(endDate.getHours() + this.selectedDuration.hours)

        // Format date with 6 decimal places
        const formatDateWithMillis = (date) => {
          const year = date.getFullYear()
          const month = String(date.getMonth() + 1).padStart(2, '0')
          const day = String(date.getDate()).padStart(2, '0')
          const hours = String(date.getHours()).padStart(2, '0')
          const minutes = String(date.getMinutes()).padStart(2, '0')
          const seconds = String(date.getSeconds()).padStart(2, '0')
          return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}.000000`
        }

        this.orderForm.endTime = formatDateWithMillis(endDate)
      }
    },

    resetOrderForm () {
      if (this.$refs.orderForm) {
        this.$refs.orderForm.resetFields()
      }
      this.orderForm = {
        userId: null,
        stationId: null,
        scooterId: null,
        hireOptionId: null,
        startTime: '',
        endTime: ''
      }
      this.availableScooters = []
      this.selectedDuration = null
    },

    submitOrder () {
      this.$refs.orderForm.validate(async (valid) => {
        if (!valid) return

        this.submitLoading = true
        try {
          const payload = { ...this.orderForm }

          // Add .000000 to both start and end times if they don't have milliseconds
          if (!payload.startTime.includes('.')) {
            payload.startTime = payload.startTime + '.000000'
          }
          if (!payload.endTime.includes('.')) {
            payload.endTime = payload.endTime + '.000000'
          }

          const response = await this.$request.post('/user/book/add', payload)
          if (response.statusCode === 200) {
            this.$message.success('Order created successfully')
            this.addOrderDialogVisible = false
            this.fetchUserOrders()
          } else {
            throw new Error(response.message || 'Failed to create order')
          }
        } catch (error) {
          console.error('Failed to create order:', error)
          this.$message.error(error.message || 'Failed to create order')
        } finally {
          this.submitLoading = false
        }
      })
    }
  }
}
</script>

<style scoped>
.orders-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.orders-card {
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.orders-card:hover {
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

.orders-table {
  width: 100%;
}

.orders-table .el-table th {
  background-color: #f5f7fa;
  color: #606266;
}

.orders-table .el-table__row {
  transition: all 0.3s;
}

.orders-table .el-table__row:hover > td {
  background-color: #f0f9ff !important;
}

.user-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
}

.station-tag {
  font-weight: normal;
  border-radius: 4px;
}

.time-cell {
  display: flex;
  flex-direction: column;
  font-size: 12px;
  line-height: 1.5;
}

.extend-tag {
  margin-left: 5px;
  font-size: 12px;
  color: #E6A23C;
  font-weight: bold;
}

.cost-value {
  color: #f56c6c;
  font-weight: bold;
}

.status-tag {
  font-weight: normal;
  text-transform: none;
}

.no-data {
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #909399;
}

.no-data i {
  font-size: 40px;
  margin-bottom: 10px;
}

.pagination {
  margin-top: 20px;
  padding: 20px;
  text-align: right;
}

.global-map-card {
  margin-bottom: 20px;
}

.global-map-container {
  height: 400px;
  width: 100%;
  border-radius: 4px;
  overflow: hidden;
}

.station-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}
</style>
