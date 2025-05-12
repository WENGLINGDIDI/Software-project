<template>
  <div class="scooter-container">
    <div class="page-header">
      <h2 class="page-title">Scooter Configuration Management</h2>
      <!-- 右上角按钮已隐藏，不再显示Add Scooter和Refresh Data -->
      <!-- <div>
        <el-button type="success" @click="showAddDialog" icon="el-icon-plus">Add Scooter</el-button>
        <el-button type="primary" @click="refreshData" icon="el-icon-refresh">Refresh Data</el-button>
        <el-button v-if="activeFilter !== 'all'" @click="resetFilter" icon="el-icon-delete">Reset Filter</el-button>
      </div> -->
    </div>

    <!-- Statistics Cards -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card total-card" @click.native="showCardDetails('total')">
          <div class="stat-icon">
            <i class="el-icon-bicycle"></i>
          </div>
          <div class="stat-info">
            <div class="stat-label">Total Scooters</div>
            <div class="stat-value">{{ scooterList.length }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card available-card" @click.native="filterByStatus(0)">
          <div class="stat-icon">
            <i class="el-icon-check"></i>
          </div>
          <div class="stat-info">
            <div class="stat-label">Available</div>
            <div class="stat-value">{{ getAvailableCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card low-power-card" @click.native="filterByLowPower()">
          <div class="stat-icon">
            <i class="el-icon-warning-outline"></i>
          </div>
          <div class="stat-info">
            <div class="stat-label">Low Battery</div>
            <div class="stat-value">{{ getLowPowerCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card avg-power-card" @click.native="showPowerDistribution()">
          <div class="stat-icon">
            <i class="el-icon-data-line"></i>
          </div>
          <div class="stat-info">
            <div class="stat-label">Average Battery</div>
            <div class="stat-value">{{ getAveragePower }}%</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Rental Price Management Area -->
    <el-card class="price-management-card" shadow="hover">
      <div slot="header" class="price-card-header">
        <span class="section-title">Rental Price Management</span>
        <el-button type="primary" size="small" @click="showAddPriceDialog" icon="el-icon-plus">
          Add Price Option
        </el-button>
      </div>
      <div class="price-options">
        <div v-for="(price, hours) in priceOptions" :key="hours" class="price-item">
          <div class="price-content">
            <div class="price-left">
              <div class="duration-tag">
                <i class="el-icon-time"></i>
                {{ formatHours(Number(hours)) }}
              </div>
              <div class="price-amount">
                <span class="currency">$</span>
                <span class="amount">{{ price.money }}</span>
              </div>
            </div>
            <div class="price-actions">
              <el-button
                type="text"
                icon="el-icon-edit"
                @click="showEditPriceDialog(Number(hours))"
              ></el-button>
              <el-button
                type="text"
                icon="el-icon-delete"
                class="delete-btn"
                @click="deletePrice(Number(hours))"
              ></el-button>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- Main Table Area -->
    <el-card shadow="hover" class="main-table-card">
      <div slot="header" class="clearfix">
        <span>Scooter Details</span>
        <div class="header-controls">
          <el-button type="success" @click="showAddDialog" icon="el-icon-plus">Add Scooter</el-button>
          <el-button type="primary" @click="refreshData" icon="el-icon-refresh">Refresh Data</el-button>
          <el-tag v-if="activeFilter === 'available'" type="success" size="medium" effect="dark">
            <i class="el-icon-check"></i> Filtered: Available Scooters
          </el-tag>
          <el-tag v-if="activeFilter === 'unavailable'" type="danger" size="medium" effect="dark">
            <i class="el-icon-close"></i> Filtered: Unavailable Scooters
          </el-tag>
          <el-tag v-if="activeFilter === 'lowPower'" type="warning" size="medium" effect="dark">
            <i class="el-icon-warning-outline"></i> Filtered: Low Battery Scooters
          </el-tag>
          <el-input
            placeholder="Search Scooter ID"
            v-model="searchId"
            class="search-input"
            prefix-icon="el-icon-search"
            clearable
          ></el-input>
        </div>
      </div>

      <el-table
        :data="paginatedScooterList"
        style="width: 100%"
        :row-class-name="tableRowClassName"
        border
        stripe
        v-loading="loading"
      >
        <el-table-column label="Scooter ID" prop="id" min-width="80" align="center">
          <template slot-scope="scope">
            <el-tag :type="getTagType(scope.row.id)">{{ scope.row.id }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Status" prop="status" min-width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : scope.row.status === 1 ? 'danger' : scope.row.status === 4 ? 'warning' : 'info'">
              {{ scope.row.status === 0 ? 'Available' : scope.row.status === 1 ? 'Unavailable' : scope.row.status === 4 ? 'Charging' : 'Maintenance' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Battery" prop="power" min-width="120" align="center">
          <template slot-scope="scope">
            <template v-if="scope.row.status === 4">
              <el-tag type="warning" effect="dark">
                <i class="el-icon-loading"></i> Charging
              </el-tag>
            </template>
            <el-progress
              v-else
              :percentage="scope.row.power"
              :status="getPowerStatus(scope.row.power)"
              :stroke-width="15"
            ></el-progress>
          </template>
        </el-table-column>

        <el-table-column label="Configuration" prop="config" min-width="220">
          <template slot-scope="scope">
            <div class="config-info">
              <div class="config-speed">
                <i class="el-icon-odometer"></i>
                {{ scope.row.config.split(',')[0].trim() }}
              </div>
              <div class="config-accessories">
                <i class="el-icon-box"></i>
                {{ scope.row.config.split(',')[1].trim() }}
              </div>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="Station" min-width="150" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.station" type="info" effect="plain">
              {{ scope.row.station.name }}
            </el-tag>
            <span v-else>Unassigned</span>
          </template>
        </el-table-column>

        <el-table-column label="Location" min-width="280">
          <template slot-scope="scope">
            <div v-if="scope.row.status === 1" class="location-info" @click="viewOnMap(scope.row)">
              <div><i class="el-icon-location"></i> {{ scope.row.address || 'Fetching address...' }}</div>
              <div v-if="scope.row.station" class="station-info">
                <i class="el-icon-office-building"></i> Station: {{ scope.row.station.name }}
              </div>
            </div>
            <div v-else>
              <el-tag type="info" effect="plain">Location not shown</el-tag>
            </div>
          </template>
        </el-table-column>

        <el-table-column label="Actions" min-width="150" align="center">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === 1"
              size="mini"
              type="primary"
              icon="el-icon-location"
              @click="viewOnMap(scope.row)"
              circle
            ></el-button>
            <el-button
              size="mini"
              type="warning"
              icon="el-icon-edit"
              @click="editScooter(scope.row)"
              circle
            ></el-button>
            <el-button
              size="mini"
              type="danger"
              icon="el-icon-delete"
              @click="deleteScooter(scope.row.id)"
              circle
            ></el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[5, 10, 20, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </div>
    </el-card>

    <!-- Battery Distribution Dialog -->
    <el-dialog
      title="Scooter Battery Distribution"
      :visible.sync="powerDistributionVisible"
      width="60%"
      @closed="cleanupChart">
      <div id="powerDistributionChart" style="height: 400px;"></div>
    </el-dialog>

    <!-- Map Dialog -->
    <el-dialog
      title="Scooter Location"
      :visible.sync="mapDialogVisible"
      width="70%"
      destroy-on-close
      @closed="destroyMap">
      <div id="mapContainer" style="height: 500px; width: 100%; min-width: 600px;"></div>
    </el-dialog>

    <!-- Edit Dialog -->
    <el-dialog
      title="Edit Scooter Information"
      :visible.sync="editDialogVisible"
      width="50%"
      @close="resetEditForm">
      <el-form :model="editForm" :rules="editRules" ref="editForm" label-width="100px">
        <el-form-item label="Scooter ID" prop="id">
          <el-input v-model.number="editForm.id" disabled></el-input>
        </el-form-item>
        <el-form-item label="Latitude" prop="latitude">
          <el-input v-model.number="editForm.latitude" placeholder="Enter latitude"></el-input>
        </el-form-item>
        <el-form-item label="Longitude" prop="longitude">
          <el-input v-model.number="editForm.longitude" placeholder="Enter longitude"></el-input>
        </el-form-item>
        <el-form-item label="Status" prop="status">
          <el-select v-model.number="editForm.status" placeholder="Select status">
            <el-option :value="0" label="Available"></el-option>
            <el-option :value="1" label="Unavailable"></el-option>
            <el-option :value="2" label="Maintenance"></el-option>
            <el-option :value="4" label="Charging"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Battery" prop="power">
          <el-input-number
            v-model="editForm.power"
            :min="0"
            :max="100"
            :step="1"
            controls-position="right">
          </el-input-number>
        </el-form-item>
        <el-form-item label="Speed Config" prop="speed">
          <el-input v-model="editForm.speed" placeholder="e.g.: 30km/h">
            <template slot="prepend">speed:</template>
          </el-input>
        </el-form-item>
        <el-form-item label="Station" prop="stationId">
          <el-select v-model.number="editForm.stationId" placeholder="Select station" clearable>
            <el-option
              v-for="s in stations"
              :key="s.id"
              :label="s.name"
              :value="s.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="editDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitEdit" :loading="submitLoading">Confirm</el-button>
      </div>
    </el-dialog>

    <!-- Add Scooter Dialog -->
    <el-dialog
      title="Add Scooter"
      :visible.sync="addDialogVisible"
      width="50%"
      @close="resetAddForm">
      <el-form :model="addForm" :rules="addRules" ref="addForm" label-width="100px">
        <el-form-item label="Station" prop="stationId">
          <el-select
            v-model.number="addForm.stationId"
            placeholder="Select station"
            @change="handleStationChange">
            <el-option
              v-for="s in stations"
              :key="s.id"
              :label="s.name"
              :value="s.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Status" prop="status">
          <el-select v-model.number="addForm.status" placeholder="Select status">
            <el-option :value="0" label="Available"></el-option>
            <el-option :value="1" label="Unavailable"></el-option>
            <el-option :value="2" label="Maintenance"></el-option>
            <el-option :value="4" label="Charging"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Battery" prop="power">
          <el-input-number
            v-model="addForm.power"
            :min="0"
            :max="100"
            :step="1"
            controls-position="right">
          </el-input-number>
        </el-form-item>
        <el-form-item label="Speed" prop="speed">
          <el-input v-model="addForm.speed" placeholder="e.g.: 30km/h">
            <template slot="prepend">speed:</template>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitAdd" :loading="addSubmitLoading">Confirm</el-button>
      </div>
    </el-dialog>

    <!-- Add/Edit Price Dialog -->
    <el-dialog
      :title="priceForm.id ? 'Edit Rental Price' : 'Add Rental Price'"
      :visible.sync="priceDialogVisible"
      width="30%"
      @close="resetPriceForm">
      <el-form :model="priceForm" :rules="priceRules" ref="priceForm" label-width="100px">
        <el-form-item label="Duration" prop="hours">
          <div class="input-group">
            <el-input
              v-model.number="priceForm.hours"
              type="number"
              placeholder="Enter rental duration"
              :min="1"
              :disabled="!!priceForm.id"
              class="custom-input">
            </el-input>
            <div class="unit-addon">hours</div>
          </div>
        </el-form-item>
        <el-form-item label="Price" prop="money">
          <div class="input-group">
            <el-input
              v-model.number="priceForm.money"
              type="number"
              placeholder="Enter price"
              :min="1"
              :max="1000"
              class="custom-input">
            </el-input>
            <div class="unit-addon">pound</div>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="priceDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitPriceEdit" :loading="priceSubmitLoading">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import AMapLoader from '@amap/amap-jsapi-loader'
import * as echarts from 'echarts'

export default {
  name: 'ScooterView',
  data () {
    return {
      loading: false,
      scooterList: [],
      filteredData: [],
      searchId: '',
      currentPage: 1,
      pageSize: 10,
      activeFilter: 'all', // Current active filter
      powerDistributionVisible: false, // Controls battery distribution dialog visibility
      powerChart: null, // Stores battery distribution chart instance
      mapDialogVisible: false,
      map: null,
      marker: null,
      addressCache: {}, // For caching address information
      editDialogVisible: false,
      submitLoading: false,
      addDialogVisible: false,
      addSubmitLoading: false,
      editForm: {
        id: '',
        latitude: '',
        longitude: '',
        status: '',
        power: 0,
        speed: '',
        stationId: null
      },
      addForm: {
        status: 0, // Default to available status
        power: 80,
        speed: '30km/h',
        stationId: null,
        latitude: '',
        longitude: ''
      },
      editRules: {
        latitude: [
          { required: true, message: 'Please enter latitude', trigger: 'blur' },
          { type: 'number', message: 'Latitude must be a number', trigger: 'blur' }
        ],
        longitude: [
          { required: true, message: 'Please enter longitude', trigger: 'blur' },
          { type: 'number', message: 'Longitude must be a number', trigger: 'blur' }
        ],
        status: [
          { required: true, message: 'Please select status', trigger: 'change' },
          { type: 'number', message: 'Status must be a number', trigger: 'change' }
        ],
        power: [
          { required: true, message: 'Please enter battery level', trigger: 'blur' },
          { type: 'number', message: 'Battery level must be a number', trigger: 'blur' }
        ],
        speed: [
          { required: true, message: 'Please enter speed configuration', trigger: 'blur' }
        ],
        stationId: [
          { required: true, message: 'Please select station', trigger: 'change' },
          { type: 'number', message: 'Station ID must be a number', trigger: 'change' }
        ]
      },
      addRules: {
        stationId: [
          { required: true, message: 'Please select station', trigger: 'change' },
          { type: 'number', message: 'Station ID must be a number', trigger: 'change' }
        ],
        status: [
          { required: true, message: 'Please select status', trigger: 'change' },
          { type: 'number', message: 'Status must be a number', trigger: 'change' }
        ],
        power: [
          { required: true, message: 'Please enter battery level', trigger: 'blur' },
          { type: 'number', message: 'Battery level must be a number', trigger: 'blur' }
        ],
        speed: [
          { required: true, message: 'Please enter speed configuration', trigger: 'blur' }
        ]
      },
      // Map configuration
      amapConfig: {
        key: 'cc37cd44f45c414717314a0ff733348c',
        securityJsCode: 'bedfa07de37192f7db48417f208c7db2' // Replace with your actual security key
      },
      priceOptions: {}, // Empty object, will be populated via API
      priceDialogVisible: false,
      priceSubmitLoading: false,
      priceForm: {
        id: null,
        hours: 1, // Default value set to 1
        money: 0
      },
      priceRules: {
        hours: [
          { required: true, message: 'Please enter rental duration', trigger: 'blur' },
          { type: 'number', message: 'Duration must be a number', trigger: 'blur' },
          { type: 'number', min: 1, message: 'Duration must be greater than 0', trigger: 'blur' }
        ],
        money: [
          { required: true, message: 'Please enter price', trigger: 'blur' },
          { type: 'number', message: 'Price must be a number', trigger: 'blur' },
          { type: 'number', min: 1, max: 1000, message: 'Price must be between 1-1000', trigger: 'blur' }
        ]
      },
      stations: [],
      addStationDialogVisible: false,
      addStationLoading: false,
      addStationForm: {
        name: '',
        latitude: '',
        longitude: ''
      },
      addStationRules: {
        name: [
          { required: true, message: 'Please enter station name', trigger: 'blur' },
          { min: 2, max: 20, message: 'Length must be between 2 and 20 characters', trigger: 'blur' }
        ],
        latitude: [
          { required: true, message: 'Please enter latitude', trigger: 'blur' },
          { type: 'number', message: 'Latitude must be a number', trigger: 'blur' }
        ],
        longitude: [
          { required: true, message: 'Please enter longitude', trigger: 'blur' },
          { type: 'number', message: 'Longitude must be a number', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    // Filtered scooter list
    filteredScooterList () {
      if (this.filteredData.length > 0) {
        // If there is filtered data, use it
        if (!this.searchId) {
          return this.filteredData
        }
        return this.filteredData.filter(scooter =>
          scooter.id.toString().includes(this.searchId)
        )
      } else {
        // Otherwise use original data
        if (!this.searchId) {
          return this.scooterList
        }
        return this.scooterList.filter(scooter =>
          scooter.id.toString().includes(this.searchId)
        )
      }
    },
    // Calculate available scooter count
    getAvailableCount () {
      return this.scooterList.filter(scooter => scooter.status === 0).length
    },
    // Calculate low battery scooter count (battery below 20%)
    getLowPowerCount () {
      return this.scooterList.filter(scooter => scooter.power < 20).length
    },
    // Calculate average battery level
    getAveragePower () {
      if (this.scooterList.length === 0) return 0
      const totalPower = this.scooterList.reduce((sum, scooter) => sum + scooter.power, 0)
      return Math.round(totalPower / this.scooterList.length)
    },
    // Battery distribution data
    powerDistributionData () {
      // Count scooters in different battery ranges
      const distribution = {
        '0-20%': 0,
        '21-40%': 0,
        '41-60%': 0,
        '61-80%': 0,
        '81-100%': 0
      }

      this.scooterList.forEach(scooter => {
        const power = scooter.power
        if (power <= 20) distribution['0-20%']++
        else if (power <= 40) distribution['21-40%']++
        else if (power <= 60) distribution['41-60%']++
        else if (power <= 80) distribution['61-80%']++
        else distribution['81-100%']++
      })

      return distribution
    },
    // Paginated data
    paginatedScooterList () {
      const start = (this.currentPage - 1) * this.pageSize
      const end = this.currentPage * this.pageSize
      return this.filteredScooterList.slice(start, end)
    },
    // Total count for pagination
    total () {
      return this.filteredScooterList.length
    }
  },
  mounted () {
    // 添加安全配置
    window._AMapSecurityConfig = {
      securityJsCode: this.amapConfig.securityJsCode
    }
    this.fetchScooterData()
    this.fetchPriceOptions()
    this.fetchStations()

    // 检查路由参数，如果是从站点管理页面跳转来添加电动车
    if (this.$route.query.action === 'add') {
      this.addForm.stationId = Number(this.$route.query.stationId)
      this.showAddDialog()
    }
  },
  watch: {
    // 监听路由变化，响应参数变化
    '$route.query': {
      handler (query) {
        if (query.action === 'add') {
          this.addForm.stationId = Number(query.stationId)
          this.showAddDialog()
        }
      },
      immediate: true
    }
  },
  methods: {
    // 获取电动车数据
    async fetchScooterData () {
      this.loading = true
      try {
        const response = await this.$request.get('/admin/scooter')
        if (response.statusCode === 200) {
          this.scooterList = response.data

          // 为每辆车获取地址信息
          for (const scooter of this.scooterList) {
            if (scooter.latitude && scooter.longitude) {
              this.getAddress(scooter.latitude, scooter.longitude).then(address => {
                this.$set(scooter, 'address', address)
              }).catch(() => {
                this.$set(scooter, 'address', 'Invalid coordinates')
              })
            } else {
              this.$set(scooter, 'address', 'Invalid coordinates')
            }
          }

          this.$message.success('Data loaded successfully')
        } else {
          throw new Error(response.message || 'Failed to load data')
        }
      } catch (error) {
        console.error('Failed to fetch data:', error)
        this.$message.error('Failed to fetch data: ' + error.message)
      } finally {
        this.loading = false
      }
    },

    // 刷新数据
    refreshData () {
      this.fetchScooterData()
    },

    // 根据电量获取进度条状态
    getPowerStatus (power) {
      if (power < 20) return 'exception'
      if (power < 50) return 'warning'
      return 'success'
    },

    // 获取ID标签类型（用于视觉区分）
    getTagType (id) {
      const types = ['', 'primary', 'success', 'warning', 'danger', 'info']
      return types[id % types.length]
    },

    // 表格行样式
    tableRowClassName ({ row }) {
      if (row.power < 20) {
        return 'warning-row'
      } else if (row.status !== 0) {
        return 'disabled-row'
      }
      return ''
    },

    // 获取地址信息
    async getAddress (latitude, longitude) {
      const key = `${latitude},${longitude}`
      if (this.addressCache[key]) {
        return this.addressCache[key]
      }

      try {
        const AMap = await AMapLoader.load({
          key: this.amapConfig.key,
          version: '2.0',
          plugins: ['AMap.Geocoder']
        })

        const geocoder = new AMap.Geocoder()
        const result = await new Promise((resolve, reject) => {
          geocoder.getAddress([longitude, latitude], (status, result) => {
            if (status === 'complete' && result.info === 'OK') {
              resolve(result.regeocode.formattedAddress)
            } else {
              reject(new Error('Failed to get address'))
            }
          })
        })

        this.addressCache[key] = result
        return result
      } catch (error) {
        console.error('Address parsing failed:', error)
        return 'Address parsing failed'
      }
    },

    // Initialize AMap
    async initMap (latitude, longitude) {
      try {
        const AMap = await AMapLoader.load({
          key: this.amapConfig.key,
          version: '2.0',
          plugins: ['AMap.Geocoder', 'AMap.ToolBar', 'AMap.Scale']
        })

        const container = document.getElementById('mapContainer')
        if (!container) {
          throw new Error('Map container does not exist')
        }

        if (!this.map) {
          this.map = new AMap.Map('mapContainer', {
            zoom: 15,
            center: [longitude, latitude],
            viewMode: '2D'
          })

          // Add basic controls
          this.map.addControl(new AMap.ToolBar())
          this.map.addControl(new AMap.Scale())

          // Add marker
          this.marker = new AMap.Marker({
            position: [longitude, latitude],
            map: this.map,
            animation: 'AMAP_ANIMATION_DROP'
          })

          // Add info window
          const infoWindow = new AMap.InfoWindow({
            content: '<div style="padding: 10px;">Current Scooter Location</div>',
            offset: new AMap.Pixel(0, -30)
          })

          this.marker.on('click', () => {
            infoWindow.open(this.map, this.marker.getPosition())
          })
        } else {
          this.map.setCenter([longitude, latitude])
          this.marker.setPosition([longitude, latitude])
        }

        // Fit view
        this.map.setFitView()
      } catch (error) {
        console.error('Failed to load map:', error)
        this.$message.error('Failed to load map, please check configuration or network connection')
      }
    },

    // Destroy map instance
    destroyMap () {
      if (this.map) {
        this.map.destroy()
        this.map = null
        this.marker = null
      }
    },

    // View location on map
    async viewOnMap (scooter) {
      this.mapDialogVisible = true
      // Use nextTick to ensure container is rendered
      this.$nextTick(() => {
        this.initMap(scooter.latitude, scooter.longitude)
      })
    },

    // Edit scooter
    editScooter (scooter) {
      this.editDialogVisible = true
      this.editForm = {
        id: scooter.id,
        latitude: scooter.latitude,
        longitude: scooter.longitude,
        status: scooter.status,
        power: scooter.power,
        speed: scooter.config.split(',')[0].replace('Speed:', '').trim(),
        stationId: scooter.station ? scooter.station.id : null // Use existing station ID if available
      }
    },

    // Reset edit form
    resetEditForm () {
      if (this.$refs.editForm) {
        this.$refs.editForm.resetFields()
      }
      this.editForm = {
        id: '',
        latitude: '',
        longitude: '',
        status: '',
        power: 0,
        speed: '',
        stationId: null
      }
    },

    // Submit edit
    async submitEdit () {
      try {
        await this.$refs.editForm.validate()
        this.submitLoading = true

        // Construct complete request body
        const requestBody = {
          id: this.editForm.id,
          latitude: this.editForm.latitude,
          longitude: this.editForm.longitude,
          status: this.editForm.status,
          power: this.editForm.power,
          config: `Speed: ${this.editForm.speed}, Includes helmet and phone holder`,
          stationId: this.editForm.stationId // Keep original station if not changed
        }

        // Send request
        const response = await this.$request.post('/admin/scooter/edit', requestBody)
        if (response.statusCode === 200) {
          this.$message.success('Modified successfully')
          this.editDialogVisible = false
          // Refresh the whole page after successful edit
          window.location.reload()
        } else {
          throw new Error(response.message || 'Modification failed')
        }
      } catch (error) {
        console.error('Failed to submit modification:', error)
        this.$message.error('Failed to submit modification: ' + error.message)
      } finally {
        this.submitLoading = false
      }
    },

    // Delete scooter
    async deleteScooter (id) {
      this.$confirm('Are you sure you want to delete this scooter?', 'Warning', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await this.$request.delete(`/admin/scooter/delete/${id}`)
          if (response.statusCode === 200) {
            this.$message.success(`Successfully deleted scooter #${id}`)
            // Refresh data after successful deletion
            await this.fetchScooterData()
          }
        } catch (error) {
          console.error('Deletion failed:', error)
          this.$message.error('Deletion failed: ' + error.message)
        }
      }).catch(() => {
        this.$message.info('Deletion cancelled')
      })
    },

    // Pagination handling
    handleSizeChange (size) {
      this.pageSize = size
    },
    handleCurrentChange (page) {
      this.currentPage = page
    },

    // Show card details
    showCardDetails (cardType) {
      // Reset filter
      this.resetFilter()

      if (cardType === 'total') {
        this.$notify({
          title: 'Scooter Statistics',
          message: `System has a total of ${this.scooterList.length} scooters`,
          type: 'info',
          duration: 3000
        })
      }
    },

    // Filter scooters by status
    filterByStatus (status) {
      this.activeFilter = status === 0 ? 'available' : status === 1 ? 'unavailable' : status === 4 ? 'charging' : 'maintenance'
      this.filteredData = this.scooterList.filter(scooter => scooter.status === status)
      this.currentPage = 1

      this.$notify({
        title: 'Filter Applied',
        message: `Filtered to show ${status === 0 ? 'available' : status === 1 ? 'unavailable' : status === 4 ? 'charging' : 'maintenance'} scooters, total ${this.filteredData.length}`,
        type: 'success',
        duration: 3000
      })
    },

    // Filter scooters by low battery
    filterByLowPower () {
      this.activeFilter = 'lowPower'
      this.filteredData = this.scooterList.filter(scooter => scooter.power < 20)
      this.currentPage = 1

      this.$notify({
        title: 'Filter Applied',
        message: `Filtered to show low battery scooters, total ${this.filteredData.length}`,
        type: 'warning',
        duration: 3000
      })
    },

    // Show battery distribution
    showPowerDistribution () {
      // Show battery distribution dialog
      this.powerDistributionVisible = true

      // Initialize chart in next tick to ensure DOM is updated
      this.$nextTick(() => {
        this.initPowerDistributionChart()
      })
    },

    // Initialize battery distribution chart
    initPowerDistributionChart () {
      const chartDom = document.getElementById('powerDistributionChart')
      if (!chartDom) return

      const chart = echarts.init(chartDom)

      const option = {
        title: {
          text: 'Scooter Battery Distribution',
          left: 'center'
        },
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: Object.keys(this.powerDistributionData)
        },
        series: [
          {
            name: 'Battery Distribution',
            type: 'pie',
            radius: '70%',
            center: ['50%', '60%'],
            data: Object.entries(this.powerDistributionData).map(([name, value]) => ({
              name,
              value
            })),
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            // Set colors for different ranges
            itemStyle: {
              color: function (params) {
                const colorMap = {
                  '0-20%': '#F56C6C',
                  '21-40%': '#E6A23C',
                  '41-60%': '#F2D5A2',
                  '61-80%': '#85DCB8',
                  '81-100%': '#67C23A'
                }
                return colorMap[params.name]
              }
            }
          }
        ]
      }

      chart.setOption(option)

      // Save chart instance for cleanup
      this.powerChart = chart
    },

    // Reset filter
    resetFilter () {
      this.activeFilter = 'all'
      this.filteredData = []
      this.searchId = ''
    },

    // Cleanup power distribution chart
    cleanupChart () {
      this.powerChart = null
    },

    // Show add scooter dialog
    showAddDialog () {
      this.addDialogVisible = true
    },

    // Reset add form
    resetAddForm () {
      if (this.$refs.addForm) {
        this.$refs.addForm.resetFields()
      }
      this.addForm = {
        status: 0,
        power: 80,
        speed: '30km/h',
        stationId: null,
        latitude: '',
        longitude: ''
      }
    },

    // Submit add scooter
    async submitAdd () {
      try {
        await this.$refs.addForm.validate()
        this.addSubmitLoading = true

        // Construct complete request body
        const requestBody = {
          latitude: this.addForm.latitude,
          longitude: this.addForm.longitude,
          status: this.addForm.status,
          power: this.addForm.power,
          config: `Speed: ${this.addForm.speed}, Includes helmet and phone holder`,
          stationId: this.addForm.stationId || null
        }

        // Send request
        const response = await this.$request.post('/admin/scooter/add', requestBody)
        if (response.statusCode === 200) {
          this.$message.success('Added successfully')
          this.addDialogVisible = false
          // Refresh data after successful addition
          await this.fetchScooterData()
        } else {
          throw new Error(response.message || 'Add failed')
        }
      } catch (error) {
        console.error('Failed to submit add:', error)
        this.$message.error('Failed to submit add: ' + error.message)
      } finally {
        this.addSubmitLoading = false
      }
    },

    // Show add price dialog
    showAddPriceDialog () {
      this.priceForm = {
        id: null,
        hours: 1, // Set default value to 1
        money: 0
      }
      this.priceDialogVisible = true
    },

    // Show edit price dialog
    showEditPriceDialog (hours) {
      const option = this.priceOptions[hours]
      this.priceForm = {
        id: option.id,
        hours: hours,
        money: option.money
      }
      this.priceDialogVisible = true
    },

    // Delete price option
    deletePrice (hours) {
      const priceOption = this.priceOptions[hours]
      if (!priceOption) {
        this.$message.error('Price option to delete not found')
        return
      }

      this.$confirm(`Are you sure you want to delete the price option for ${this.formatHours(hours)}?`, 'Prompt', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await this.$request.delete(`/admin/hireOption/${priceOption.id}`)
          if (response.statusCode === 200) {
            delete this.priceOptions[hours]
            this.$message.success('Deleted successfully')
            await this.fetchPriceOptions()
          } else {
            throw new Error(response.message || 'Delete failed')
          }
        } catch (error) {
          console.error('Delete failed:', error)
          this.$message.error('Delete failed: ' + error.message)
        }
      }).catch(() => {
        this.$message.info('Delete cancelled')
      })
    },

    // Reset price form
    resetPriceForm () {
      if (this.$refs.priceForm) {
        this.$refs.priceForm.resetFields()
      }
      this.priceForm = {
        id: null,
        hours: 1, // Set default value to 1
        money: 0
      }
    },

    // Submit price edit
    async submitPriceEdit () {
      try {
        await this.$refs.priceForm.validate()
        this.priceSubmitLoading = true

        if (this.priceForm.id) {
          // Edit existing price
          const response = await this.$request.post('/admin/hireOption/edit', {
            id: this.priceForm.id,
            hours: this.priceForm.hours,
            money: this.priceForm.money
          })
          if (response.statusCode === 200) {
            this.priceOptions[response.data.hours] = {
              id: response.data.id,
              money: response.data.money
            }
            this.$message.success('Price updated successfully')
          } else {
            throw new Error(response.message || 'Update failed')
          }
        } else {
          // Add new price
          if (this.priceOptions[this.priceForm.hours]) {
            throw new Error('A price setting for this rental duration already exists')
          }

          const addRequestData = {
            hours: this.priceForm.hours,
            money: this.priceForm.money
          }

          const response = await this.$request.post('/admin/hireOption/add', addRequestData)
          if (response.statusCode === 200) {
            this.priceOptions[response.data.hours] = {
              id: response.data.id,
              money: response.data.money
            }
            this.$message.success('Price added successfully')
          } else {
            throw new Error(response.message || 'Add failed')
          }
        }
        this.priceDialogVisible = false
      } catch (error) {
        console.error('Operation failed:', error)
        this.$message.error('Operation failed: ' + error.message)
      } finally {
        this.priceSubmitLoading = false
      }
    },

    // Get all price options
    async fetchPriceOptions () {
      try {
        const response = await this.$request.get('/admin/hireOption')
        if (response.statusCode === 200) {
          const options = {}
          response.data.forEach(item => {
            options[item.hours] = {
              id: item.id,
              money: item.money
            }
          })
          this.priceOptions = options
        } else {
          throw new Error(response.message || 'Failed to get price options')
        }
      } catch (error) {
        console.error('Failed to get price options:', error)
        this.$message.error('Failed to get price options: ' + error.message)
      }
    },

    // Format rental duration display
    formatHours (hours) {
      if (hours === 1) return '1 hour'
      if (hours === 4) return '4 hours'
      if (hours === 24) return '1 day'
      if (hours === 168) return '1 week'
      return `${hours} hours`
    },

    // Get station data
    async fetchStations () {
      try {
        const response = await this.$request.get('/admin/station')
        if (response.statusCode === 200) {
          this.stations = response.data
        } else {
          throw new Error(response.message || 'Failed to get station data')
        }
      } catch (error) {
        console.error('Failed to get station data:', error)
        this.$message.error('Failed to get station data: ' + error.message)
      }
    },

    // Show add station dialog
    showAddStationDialog () {
      this.addStationDialogVisible = true
    },

    // Reset add station form
    resetAddStationForm () {
      if (this.$refs.addStationForm) {
        this.$refs.addStationForm.resetFields()
      }
      this.addStationForm = {
        name: '',
        latitude: '',
        longitude: ''
      }
    },

    // Submit add station
    async submitAddStation () {
      try {
        await this.$refs.addStationForm.validate()
        this.addStationLoading = true

        const response = await this.$request.post('/admin/station/add', this.addStationForm)
        if (response.statusCode === 200) {
          this.stations.push(response.data)
          this.$message.success('Station added successfully')
          this.addStationDialogVisible = false
        } else {
          throw new Error(response.message || 'Add station failed')
        }
      } catch (error) {
        console.error('Add station failed:', error)
        this.$message.error('Add station failed: ' + error.message)
      } finally {
        this.addStationLoading = false
      }
    },

    // Handle station selection change
    handleStationChange (stationId) {
      if (stationId) {
        const selectedStation = this.stations.find(s => s.id === stationId)
        if (selectedStation) {
          this.addForm.latitude = selectedStation.latitude
          this.addForm.longitude = selectedStation.longitude
        }
      } else {
        this.addForm.latitude = ''
        this.addForm.longitude = ''
      }
    }
  },
  // Clean up map instance when component is destroyed
  beforeDestroy () {
    if (this.map) {
      this.map.destroy()
      this.map = null
    }
    this.marker = null
  }
}
</script>

<style scoped>
.scooter-container {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  color: #303133;
  font-size: 22px;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  padding: 20px;
  height: 110px;
  transition: all 0.3s;
  border: none;
  border-radius: 8px;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 20px;
  margin-right: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: -30px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.2);
}

.stat-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.stat-label {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
  margin-top: -20px;
  margin-left: 70px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  margin-left: 70px;
}

.total-card {
  background: linear-gradient(135deg, #56CCF2 0%, #2F80ED 100%);
  color: white;
}

.total-card .stat-icon {
  color: #2F80ED;
  background-color: white;
}

.available-card {
  background: linear-gradient(135deg, #DCFFBD 0%, #B4DE7C 100%);
  color: #4D7C0F;
}

.available-card .stat-icon {
  color: #4D7C0F;
  background-color: white;
}

.low-power-card {
  background: linear-gradient(135deg, #FFD166 0%, #FB8B24 100%);
  color: #7C2D12;
}

.low-power-card .stat-icon {
  color: #7C2D12;
  background-color: white;
}

.avg-power-card {
  background: linear-gradient(135deg, #8CD4B2 0%, #2BAF70 100%);
  color: white;
}

.avg-power-card .stat-icon {
  color: #2BAF70;
  background-color: white;
}

.main-table-card {
  margin-bottom: 20px;
}

.main-table-card .el-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-controls .el-button {
  margin-left: 0;
}

.header-controls .el-tag {
  margin: 0;
}

.search-input {
  width: 200px;
  margin-left: auto;
}

.config-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 5px 0;
}

.config-speed, .config-accessories {
  display: flex;
  align-items: center;
  gap: 5px;
  white-space: normal;
  word-break: break-all;
}

.config-speed i {
  color: #409EFF;
}

.config-accessories i {
  color: #67C23A;
}

.location-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
  white-space: normal;
  word-break: break-all;
  cursor: pointer;
}

.location-info:hover {
  color: #409EFF;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* Table row styles */
::v-deep .warning-row {
  background-color: #fff8e1 !important;
}

::v-deep .disabled-row {
  background-color: #f5f5f5 !important;
  color: #909399;
}

::v-deep .el-table .cell {
  word-break: break-word;
}

#mapContainer {
  position: relative;
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.price-management-card {
  margin-bottom: 20px;
  overflow: hidden;
}

.price-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.price-options {
  display: flex;
  padding: 20px 20px 20px 0;
  gap: 15px;
  overflow-x: auto;
  scrollbar-width: thin;
  -webkit-overflow-scrolling: touch;
}

.price-options::-webkit-scrollbar {
  height: 6px;
}

.price-options::-webkit-scrollbar-track {
  background: #f5f7fa;
  border-radius: 3px;
}

.price-options::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.price-options::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

.price-item {
  flex: 0 0 260px;
  max-width: 260px;
  min-width: 260px;
}

.price-content {
  background: linear-gradient(to right, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 8px;
  padding: 12px 15px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 50px;
  position: relative;
}

.price-content:hover {
  /* 移除动画效果 */
  /* transform: translateY(-2px); */
  /* box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); */
}

.price-left {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  margin-right: 10px;
}

.duration-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background-color: rgba(255, 255, 255, 0.9);
  padding: 3px 10px;
  border-radius: 12px;
  color: #606266;
  font-size: 13px;
  white-space: nowrap;
}

.price-amount {
  display: flex;
  align-items: baseline;
  margin-right: -10px;
  margin-left: 10px;
}

.currency {
  font-size: 14px;
  color: #409EFF;
  margin-right: 1px;
}

.amount {
  font-size: 20px;
  font-weight: bold;
  color: #409EFF;
}

.price-actions {
  display: flex;
  gap: 8px;
  margin-left: 0;
}

.price-actions .el-button {
  padding: 6px;
  width: 32px;
  height: 32px;
  font-size: 18px;
  border-radius: 4px;
  /* transition: all 0.3s; */
  display: flex;
  align-items: center;
  justify-content: center;
}

.price-actions .el-button:hover {
  /* 移除动画效果 */
  /* background-color: rgba(255, 255, 255, 0.9); */
  /* transform: scale(1.1); */
}

.price-actions .el-button:active {
  /* 移除动画效果 */
  /* transform: scale(0.95); */
}

.price-actions .el-button:first-child {
  margin-right: -30px;
}

.delete-btn {
  color: #F56C6C;
}

.delete-btn:hover {
  color: #f78989;
  background-color: rgba(245, 108, 108, 0.1) !important;
}

.input-group {
  display: flex;
  align-items: stretch;
  width: 100%;
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  overflow: hidden;
  transition: all 0.3s;
}

.input-group:hover {
  border-color: #C0C4CC;
}

.input-group:focus-within {
  border-color: #409EFF;
}

.input-group .custom-input {
  flex: 1;
}

.input-group .custom-input :deep(.el-input__inner) {
  border: none;
  border-radius: 0;
  padding-right: 8px;
}

.input-group .unit-addon {
  display: flex;
  align-items: center;
  padding: 0 15px;
  background-color: #F5F7FA;
  color: #606266;
  font-size: 14px;
  border-left: 1px solid #DCDFE6;
  white-space: nowrap;
  user-select: none;
}

.input-group:focus-within .unit-addon {
  border-left-color: #409EFF;
  background-color: #ECF5FF;
  color: #409EFF;
}

.station-map-card,
.station-map-container,
.station-card-header,
.station-header-actions {
  display: none;
}
</style>
