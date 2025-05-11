<template>
  <div class="station-view">
    <!-- Global Station Distribution Map -->
    <el-card class="global-map-card" shadow="hover">
      <div slot="header" class="station-card-header">
        <span class="section-title">All Station Distribution</span>
        <div class="header-actions">
          <el-button type="success" @click="showAddStationDialog" icon="el-icon-plus">Add Station</el-button>
          <el-button type="primary" @click="refreshStations" icon="el-icon-refresh">Refresh Stations</el-button>
        </div>
      </div>
      <div id="globalMapContainer" class="global-map-container"></div>
    </el-card>

    <!-- Station Selection Dropdown -->
    <el-card class="station-selection-card" shadow="hover">
      <div slot="header" class="clearfix">
        <span>Station Selection</span>
      </div>
      <el-form :inline="true" class="station-form">
        <el-form-item label="Select Station">
          <el-select
            v-model="selectedStationId"
            placeholder="Please select a station"
            @change="handleStationChange"
            style="width: 240px">
            <el-option
              v-for="station in stations"
              :key="station.id"
              :label="station.name"
              :value="station.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Station Details -->
    <el-card class="station-info-card" shadow="hover" v-if="selectedStation">
      <div slot="header" class="clearfix">
        <span>Station Details</span>
        <div class="header-controls">
          <el-button
            size="small"
            type="primary"
            icon="el-icon-edit"
            @click="editStation(selectedStation)">
            Edit Station
          </el-button>
          <el-button
            size="small"
            type="danger"
            icon="el-icon-delete"
            @click="deleteStation(selectedStation.id)">
            Delete Station
          </el-button>
        </div>
      </div>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="Station ID">{{ selectedStation.id }}</el-descriptions-item>
        <el-descriptions-item label="Station Name">{{ selectedStation.name }}</el-descriptions-item>
        <el-descriptions-item label="Location">
          <div v-if="selectedStation.address">{{ selectedStation.address }}</div>
          <div v-else>
            <el-tag type="info">Fetching address...</el-tag>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="Coordinates">
          {{ selectedStation.latitude }}, {{ selectedStation.longitude }}
        </el-descriptions-item>
        <el-descriptions-item label="Number of Scooters" :span="2">
          <el-tag type="success" size="medium">{{ stationScooters.length }}</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- Statistics Cards After Station Selection -->
    <div class="statistics-container" v-if="selectedStation">
      <el-card class="statistics-card" shadow="hover" :body-style="{ padding: '0px' }">
        <div class="statistic-item blue">
          <div class="statistic-icon">
            <i class="el-icon-bicycle"></i>
          </div>
          <div class="statistic-info">
            <div class="statistic-label">Total Scooters</div>
            <div class="statistic-value">{{ stationScooters.length }}</div>
          </div>
        </div>
      </el-card>

      <el-card class="statistics-card" shadow="hover" :body-style="{ padding: '0px' }">
        <div class="statistic-item green">
          <div class="statistic-icon">
            <i class="el-icon-check"></i>
          </div>
          <div class="statistic-info">
            <div class="statistic-label">Available Scooters</div>
            <div class="statistic-value">{{ stationAvailableScooters }}</div>
          </div>
        </div>
      </el-card>

      <el-card class="statistics-card" shadow="hover" :body-style="{ padding: '0px' }">
        <div class="statistic-item orange">
          <div class="statistic-icon">
            <i class="el-icon-warning-outline"></i>
          </div>
          <div class="statistic-info">
            <div class="statistic-label">Low Battery Scooters</div>
            <div class="statistic-value">{{ stationLowBatteryScooters }}</div>
          </div>
        </div>
      </el-card>

      <el-card class="statistics-card" shadow="hover" :body-style="{ padding: '0px' }">
        <div class="statistic-item teal">
          <div class="statistic-icon">
            <i class="el-icon-data-analysis"></i>
          </div>
          <div class="statistic-info">
            <div class="statistic-label">Average Battery</div>
            <div class="statistic-value">{{ stationAverageBatteryLevel }}%</div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- Station Scooter List -->
    <el-card class="scooter-list-card" shadow="hover" v-if="selectedStation">
      <div slot="header" class="clearfix">
        <span>Station Scooter List</span>
        <div class="header-controls">
          <el-input
            placeholder="Search Scooter ID"
            v-model="searchScooterId"
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
            <el-tag :type="scope.row.status === 0 ? 'success' : scope.row.status === 1 ? 'danger' : 'warning'">
              {{ scope.row.status === 0 ? 'Available' : scope.row.status === 1 ? 'Unavailable' : 'Maintenance' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="Battery" prop="power" min-width="120" align="center">
          <template slot-scope="scope">
            <el-progress
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
                {{ getSpeedFromConfig(scope.row.config) }}
              </div>
              <div class="config-accessories">
                <i class="el-icon-box"></i>
                {{ getAccessoriesFromConfig(scope.row.config) }}
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

        <el-table-column label="Actions" min-width="150" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="warning"
              icon="el-icon-edit"
              @click="handleEditScooter(scope.row)"
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

      <div class="pagination-container" v-if="stationScooters.length > 0">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[5, 10, 20, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="filteredScooters.length">
        </el-pagination>
      </div>

      <div class="empty-data" v-else>
        <el-empty description="No scooters at current station"></el-empty>
      </div>

      <!-- Add Scooter 按钮 -->
      <div style="margin-top: 20px; text-align: right;">
        <el-button type="success" icon="el-icon-plus" @click="goToAddScooter">Add Scooter</el-button>
      </div>
    </el-card>

    <!-- No Station Selected Prompt -->
    <div class="empty-selection" v-if="!selectedStation && stations.length > 0">
      <el-empty description="Please select a station to view details"></el-empty>
    </div>

    <!-- Add/Edit Station Dialog -->
    <el-dialog
      :title="dialogType === 'add' ? 'Add Station' : 'Edit Station'"
      :visible.sync="dialogVisible"
      width="50%">
      <el-form :model="stationForm" :rules="stationRules" ref="stationForm" label-width="100px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="stationForm.name" placeholder="Enter station name"></el-input>
        </el-form-item>
        <el-form-item label="Longitude" prop="longitude">
          <el-input v-model="stationForm.longitude" placeholder="Enter longitude"></el-input>
        </el-form-item>
        <el-form-item label="Latitude" prop="latitude">
          <el-input v-model="stationForm.latitude" placeholder="Enter latitude"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitStation" :loading="submitLoading">Confirm</el-button>
      </div>
    </el-dialog>

    <!-- Edit Scooter Dialog -->
    <el-dialog
      title="Edit Scooter Information"
      :visible.sync="scooterDialogVisible"
      width="50%"
      @close="resetScooterForm">
      <el-form :model="scooterForm" :rules="scooterRules" ref="scooterForm" label-width="100px">
        <el-form-item label="Scooter ID">
          <span>{{ scooterForm.id }}</span>
        </el-form-item>
        <el-form-item label="Status" prop="status">
          <el-select v-model="scooterForm.status" placeholder="Select status">
            <el-option :value="0" label="Available"></el-option>
            <el-option :value="1" label="Unavailable"></el-option>
            <el-option :value="2" label="Maintenance"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Battery" prop="power">
          <el-input-number
            v-model="scooterForm.power"
            :min="0"
            :max="100"
            :step="1"
            controls-position="right">
          </el-input-number>
        </el-form-item>
        <el-form-item label="Configuration" prop="config">
          <el-input
            type="textarea"
            v-model="scooterForm.config"
            :rows="2"
            placeholder="Enter configuration information">
          </el-input>
        </el-form-item>
        <el-form-item label="Longitude" prop="longitude">
          <el-input v-model="scooterForm.longitude"></el-input>
        </el-form-item>
        <el-form-item label="Latitude" prop="latitude">
          <el-input v-model="scooterForm.latitude"></el-input>
        </el-form-item>
        <el-form-item label="Station" prop="stationId">
          <el-select v-model="scooterForm.stationId" placeholder="Select station">
            <el-option
              v-for="station in stations"
              :key="station.id"
              :label="station.name"
              :value="station.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="scooterDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitScooterEdit" :loading="submitLoading">Confirm</el-button>
      </div>
    </el-dialog>

    <!-- Rental Price Dialog -->
    <el-dialog
      title="Rental Price Settings"
      :visible.sync="hireOptionsDialogVisible"
      width="50%">
      <div v-loading="hireOptionsLoading">
        <el-table
          :data="currentHireOptions"
          style="width: 100%"
          border>
          <el-table-column
            prop="hours"
            label="Rental Duration"
            width="180"
            align="center">
            <template slot-scope="scope">
              {{ scope.row.hours }} hours
            </template>
          </el-table-column>
          <el-table-column
            prop="money"
            label="Price"
            width="180"
            align="center">
            <template slot-scope="scope">
              ¥{{ scope.row.money }}
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="hireOptionsDialogVisible = false">Close</el-button>
      </div>
    </el-dialog>

    <!-- Add Scooter Dialog -->
    <el-dialog
      title="Add Scooter"
      :visible.sync="addScooterDialogVisible"
      width="40%"
      @close="resetAddScooterForm">
      <el-form :model="addScooterForm" :rules="addScooterRules" ref="addScooterForm" label-width="100px">
        <el-form-item label="Latitude" prop="latitude">
          <el-input v-model="addScooterForm.latitude" placeholder="Enter latitude"></el-input>
        </el-form-item>
        <el-form-item label="Longitude" prop="longitude">
          <el-input v-model="addScooterForm.longitude" placeholder="Enter longitude"></el-input>
        </el-form-item>
        <el-form-item label="Status" prop="status">
          <el-select v-model="addScooterForm.status" placeholder="Select status">
            <el-option :value="0" label="Available"></el-option>
            <el-option :value="1" label="Unavailable"></el-option>
            <el-option :value="2" label="Maintenance"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="Power" prop="power">
          <el-input-number v-model="addScooterForm.power" :min="0" :max="100" :step="1" controls-position="right"></el-input-number>
        </el-form-item>
        <el-form-item label="Config" prop="config">
          <el-input v-model="addScooterForm.config" placeholder="e.g. speed:30km/h"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="addScooterDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitAddScooter" :loading="addScooterLoading">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import AMapLoader from '@amap/amap-jsapi-loader'

export default {
  name: 'StationView',
  data () {
    return {
      loading: false,
      submitLoading: false,
      stations: [], // Station list
      stationsData: [], // Complete station and scooter data
      selectedStationId: null, // Currently selected station ID
      selectedStation: null, // Currently selected station object
      stationScooters: [], // Scooters at the station
      searchScooterId: '', // Scooter ID search
      currentPage: 1,
      pageSize: 10,
      dialogVisible: false,
      dialogType: 'add',
      stationForm: {
        id: '',
        name: '',
        latitude: '',
        longitude: ''
      },
      stationRules: {
        name: [
          { required: true, message: 'Please enter station name', trigger: 'blur' },
          { min: 2, max: 20, message: 'Length should be 2 to 20 characters', trigger: 'blur' }
        ],
        latitude: [
          { required: true, validator: function (rule, value, callback) { return this.validateFloat({ field: 'latitude' }, value, callback) }.bind(this), trigger: 'blur' }
        ],
        longitude: [
          { required: true, validator: function (rule, value, callback) { return this.validateFloat({ field: 'longitude' }, value, callback) }.bind(this), trigger: 'blur' }
        ]
      },
      globalMap: null, // Global map instance
      globalMarkers: [], // Global station markers
      // Map configuration
      amapConfig: {
        key: 'cc37cd44f45c414717314a0ff733348c',
        securityJsCode: 'bedfa07de37192f7db48417f208c7db2'
      },
      scooterDialogVisible: false,
      scooterForm: {
        id: '',
        status: '',
        power: '',
        config: '',
        latitude: '',
        longitude: '',
        stationId: null
      },
      scooterRules: {
        status: [
          { required: true, message: 'Please select status', trigger: 'change' }
        ],
        power: [
          { required: true, message: 'Please enter battery level', trigger: 'blur' },
          { type: 'number', min: 0, max: 100, message: 'Battery level must be between 0 and 100', trigger: 'blur' }
        ],
        config: [
          { required: true, message: 'Please enter configuration information', trigger: 'blur' }
        ],
        latitude: [
          { required: true, message: 'Please enter latitude', trigger: 'blur' },
          { type: 'number', message: 'Latitude must be a number', trigger: 'blur' }
        ],
        longitude: [
          { required: true, message: 'Please enter longitude', trigger: 'blur' },
          { type: 'number', message: 'Longitude must be a number', trigger: 'blur' }
        ]
      },
      hireOptionsDialogVisible: false,
      hireOptionsLoading: false,
      currentHireOptions: [],
      currentScooterId: null,
      addScooterDialogVisible: false,
      addScooterLoading: false,
      addScooterForm: {
        latitude: '',
        longitude: '',
        status: 0,
        power: 80,
        config: 'speed:30km/h'
      },
      addScooterRules: {
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
        config: [
          { required: true, message: 'Please enter config', trigger: 'blur' }
        ]
      },
      addressCache: {}, // For caching address information
      // Custom float validator for latitude/longitude
      validateFloat: (rule, value, callback) => {
        if (value === '' || value === null || value === undefined) {
          callback(new Error('Please enter ' + rule.field))
        } else if (!/^(-?\d+)(\.\d+)?$/.test(value)) {
          callback(new Error(rule.field.charAt(0).toUpperCase() + rule.field.slice(1) + ' must be a valid number'))
        } else {
          callback()
        }
      }
    }
  },
  computed: {
    // Filtered scooter list
    filteredScooters () {
      if (!this.searchScooterId) {
        return this.stationScooters
      }
      return this.stationScooters.filter(scooter =>
        scooter.id.toString().includes(this.searchScooterId)
      )
    },
    // Available scooters at current station
    stationAvailableScooters () {
      return this.stationScooters.filter(s => s.status === 1).length
    },
    // Low battery scooters at current station
    stationLowBatteryScooters () {
      return this.stationScooters.filter(s => s.power < 20).length
    },
    // Average battery level at current station
    stationAverageBatteryLevel () {
      if (this.stationScooters.length === 0) return 0
      const totalPower = this.stationScooters.reduce((sum, scooter) => sum + scooter.power, 0)
      return Math.round(totalPower / this.stationScooters.length)
    },
    // Paginated scooter list
    paginatedScooterList () {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.filteredScooters.slice(start, end)
    }
  },
  methods: {
    // Fetch all stations and scooter data
    async fetchStationsAndScooters () {
      this.loading = true
      try {
        const response = await this.$request.get('/admin/stationAndScooter')
        if (response.statusCode === 200) {
          this.stationsData = response.data
          // Extract station data
          this.stations = response.data.map(item => item.station)

          // Update scooter data if a station is selected
          if (this.selectedStationId) {
            const selectedStationData = response.data.find(
              item => item.station.id === this.selectedStationId
            )
            if (selectedStationData) {
              this.selectedStation = selectedStationData.station
              this.stationScooters = selectedStationData.scooters
            }
          }

          return response.data
        } else {
          throw new Error(response.message || 'Failed to fetch data')
        }
      } catch (error) {
        console.error('Failed to fetch data:', error)
        this.$message.error('Failed to fetch data: ' + error.message)
        return []
      } finally {
        this.loading = false
      }
    },

    // Refresh station data
    async refreshStations () {
      this.loading = true
      try {
        await this.fetchStationsAndScooters()
        // Update global map
        await this.initGlobalMap()
        this.$message.success('Station data refreshed')
      } catch (error) {
        console.error('Failed to refresh station data:', error)
        this.$message.error('Failed to refresh station data')
      } finally {
        this.loading = false
      }
    },

    // Handle station selection change
    async handleStationChange (stationId) {
      if (!stationId) {
        this.selectedStation = null
        this.stationScooters = []
        return
      }

      const selectedStationData = this.stationsData.find(
        item => item.station.id === stationId
      )

      if (selectedStationData) {
        this.selectedStation = selectedStationData.station
        this.stationScooters = selectedStationData.scooters

        // Get address information
        if (this.selectedStation.latitude && this.selectedStation.longitude) {
          try {
            const address = await this.getAddress(this.selectedStation.latitude, this.selectedStation.longitude)
            this.$set(this.selectedStation, 'address', address)
          } catch (error) {
            console.error('Failed to get address:', error)
            this.$set(this.selectedStation, 'address', 'Failed to get address')
          }
        }

        // If global map is loaded, highlight selected station
        if (this.globalMap && this.globalMarkers.length > 0) {
          // Find corresponding marker
          const index = this.stations.findIndex(s => s.id === stationId)
          if (index >= 0 && index < this.globalMarkers.length) {
            // Center global map on selected station
            this.globalMap.setCenter([this.selectedStation.longitude, this.selectedStation.latitude])
            this.globalMap.setZoom(14)
          }
        }
      }

      // Reset pagination
      this.currentPage = 1
    },

    // Get address information
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

    // Get battery level status
    getPowerStatus (power) {
      if (power < 20) return 'exception'
      if (power < 50) return 'warning'
      return 'success'
    },

    // Show add station dialog
    showAddStationDialog () {
      this.dialogType = 'add'
      this.stationForm = {
        id: '',
        name: '',
        latitude: '',
        longitude: '',
        address: ''
      }
      this.dialogVisible = true
    },

    // Edit station
    editStation (station) {
      this.dialogType = 'edit'
      this.stationForm = { ...station }
      this.dialogVisible = true
    },

    // Submit station form
    async submitStation () {
      try {
        await this.$refs.stationForm.validate()
        this.submitLoading = true

        let response
        // 保证经纬度为数字类型
        const payload = {
          ...this.stationForm,
          longitude: Number(this.stationForm.longitude),
          latitude: Number(this.stationForm.latitude)
        }
        if (this.dialogType === 'add') {
          response = await this.$request.post('/admin/station/add', payload)
        } else {
          response = await this.$request.post('/admin/station/edit', payload)
        }

        if (response.statusCode === 200) {
          // Refresh station and scooter data
          await this.refreshStations()
          this.$message.success(this.dialogType === 'add' ? 'Station added successfully' : 'Station edited successfully')
          this.dialogVisible = false
        } else {
          throw new Error(response.message || 'Operation failed')
        }
      } catch (error) {
        console.error('Failed to submit station data:', error)
        this.$message.error('Submission failed: ' + error.message)
      } finally {
        this.submitLoading = false
      }
    },

    // Delete station
    async deleteStation (id) {
      try {
        await this.$confirm('Are you sure you want to delete this station? All scooters at this station will be released.', 'Warning', {
          confirmButtonText: 'Confirm',
          cancelButtonText: 'Cancel',
          type: 'warning'
        })

        const response = await this.$request.delete(`/admin/station/delete/${id}`)
        if (response.statusCode === 200) {
          // If deleting currently selected station, clear selection
          if (this.selectedStationId === id) {
            this.selectedStationId = null
            this.selectedStation = null
            this.stationScooters = []
          }

          // Refresh station data
          await this.refreshStations()
          this.$message.success('Station deleted successfully')
        } else {
          throw new Error(response.message || 'Deletion failed')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Failed to delete station:', error)
          this.$message.error('Failed to delete station: ' + error.message)
        }
      }
    },

    // Pagination handling
    handleSizeChange (val) {
      this.pageSize = val
    },
    handleCurrentChange (val) {
      this.currentPage = val
    },

    // Initialize global map - show all stations
    async initGlobalMap () {
      try {
        // Load AMap API
        const AMap = await AMapLoader.load({
          key: this.amapConfig.key,
          version: '2.0',
          plugins: ['AMap.ToolBar', 'AMap.Scale']
        })

        const container = document.getElementById('globalMapContainer')
        if (!container) {
          throw new Error('Global map container does not exist')
        }

        // Create or reuse map instance
        if (!this.globalMap) {
          this.globalMap = new AMap.Map('globalMapContainer', {
            zoom: 11,
            center: [120.15, 30.25], // Default center
            viewMode: '2D'
          })

          // Add basic controls
          this.globalMap.addControl(new AMap.ToolBar())
          this.globalMap.addControl(new AMap.Scale())
        }

        // Clear existing markers
        this.clearGlobalMarkers()

        // Update station markers
        if (this.stationsData && this.stationsData.length > 0) {
          this.stationsData.forEach(stationData => {
            const station = stationData.station
            const marker = new AMap.Marker({
              position: [station.longitude, station.latitude],
              map: this.globalMap,
              title: station.name,
              cursor: 'pointer',
              animation: 'AMAP_ANIMATION_DROP'
            })

            const infoWindow = new AMap.InfoWindow({
              content: this.createInfoWindowContent(stationData),
              offset: new AMap.Pixel(0, -30)
            })

            marker.on('click', () => {
              infoWindow.open(this.globalMap, marker.getPosition())
              this.selectedStationId = station.id
              this.handleStationChange(station.id)
            })

            this.globalMarkers.push(marker)
          })

          this.globalMap.setFitView()
        }
      } catch (error) {
        console.error('Failed to load global map:', error)
        this.$message.error('Failed to load global map, please check configuration or network connection')
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
    createInfoWindowContent (stationData) {
      return `
        <div style="padding: 10px;">
          <h4 style="margin: 0 0 5px 0;">${stationData.station.name}</h4>
          <p style="margin: 0;">Number of Scooters: ${stationData.scooters.length}</p>
          <p style="margin: 5px 0 0 0;">Available Scooters: ${stationData.scooters.filter(s => s.status === 1).length}</p>
        </div>
      `
    },

    // Get status type
    getStatusType (status) {
      switch (status) {
        case 0: return 'success'
        case 2: return 'warning'
        default: return 'danger'
      }
    },

    // Get status text
    getStatusText (status) {
      switch (status) {
        case 0: return 'Available'
        case 2: return 'In Maintenance'
        default: return 'Unavailable'
      }
    },

    // Extract speed from configuration
    getSpeedFromConfig (config) {
      const speedMatch = config.match(/Speed:\s*(\d+)\s*km\/h/i) ||
                        config.match(/speed:\s*(\d+)\s*km\/h/i)
      return speedMatch ? `${speedMatch[1]} km/h` : 'Speed Unknown'
    },

    // Extract accessories from configuration
    getAccessoriesFromConfig (config) {
      const accessories = config.split(',')[1]
      return accessories ? accessories.trim() : 'No Accessories'
    },

    // Handle edit scooter
    handleEditScooter (scooter) {
      this.scooterForm = {
        id: scooter.id,
        status: scooter.status,
        power: scooter.power,
        config: scooter.config,
        latitude: scooter.latitude,
        longitude: scooter.longitude,
        stationId: scooter.station?.id || this.selectedStationId
      }
      this.scooterDialogVisible = true
    },

    // Submit scooter edit
    async submitScooterEdit () {
      try {
        await this.$refs.scooterForm.validate()
        this.submitLoading = true

        const response = await this.$request.post('/admin/scooter/edit', {
          id: this.scooterForm.id,
          status: this.scooterForm.status,
          power: this.scooterForm.power,
          config: this.scooterForm.config,
          latitude: this.scooterForm.latitude,
          longitude: this.scooterForm.longitude,
          stationId: this.scooterForm.stationId
        })

        if (response.statusCode === 200) {
          // Update local data
          const index = this.stationScooters.findIndex(s => s.id === this.scooterForm.id)
          if (index !== -1) {
            this.stationScooters[index] = response.data
          }

          this.$message.success('Scooter edited successfully')
          this.scooterDialogVisible = false

          // Refresh data
          await this.refreshStations()
        } else {
          throw new Error(response.message || 'Edit failed')
        }
      } catch (error) {
        console.error('Failed to edit scooter:', error)
        this.$message.error('Edit failed: ' + error.message)
      } finally {
        this.submitLoading = false
      }
    },

    // Reset scooter form
    resetScooterForm () {
      if (this.$refs.scooterForm) {
        this.$refs.scooterForm.resetFields()
      }
      this.scooterForm = {
        id: '',
        status: '',
        power: '',
        config: '',
        latitude: '',
        longitude: '',
        stationId: null
      }
    },

    // Show rental options
    async showHireOptions (scooter) {
      this.currentScooterId = scooter.id
      this.hireOptionsDialogVisible = true
      this.hireOptionsLoading = true

      try {
        const response = await this.$request.get(`/admin/hireOption/${scooter.id}`)
        if (response.statusCode === 200) {
          // Convert single price option to array format for display
          this.currentHireOptions = [response.data]
        } else {
          throw new Error(response.message || 'Failed to get rental prices')
        }
      } catch (error) {
        console.error('Failed to get rental prices:', error)
        this.$message.error('Failed to get rental prices: ' + error.message)
        this.currentHireOptions = []
      } finally {
        this.hireOptionsLoading = false
      }
    },

    // Get available scooter count
    getAvailableCount () {
      return this.scooterList.filter(scooter => scooter.status === 0).length
    },

    // Get tag type
    getTagType (id) {
      // Implement logic to get tag type based on scooter ID
      return 'success' // Temporary return, needs actual implementation
    },

    // Get table row class name
    tableRowClassName ({ row }) {
      return row.status === 0 ? 'success-row' : row.status === 1 ? 'danger-row' : 'warning-row'
    },

    // Delete scooter
    async deleteScooter (id) {
      try {
        await this.$confirm('Are you sure you want to delete this scooter?', 'Warning', {
          confirmButtonText: 'Confirm',
          cancelButtonText: 'Cancel',
          type: 'warning'
        })

        const response = await this.$request.delete(`/admin/scooter/delete/${id}`)
        if (response.statusCode === 200) {
          this.$message.success('Scooter deleted successfully')
          // Refresh data after successful deletion
          await this.refreshStations()
        } else {
          throw new Error(response.message || 'Delete failed')
        }
      } catch (error) {
        if (error !== 'cancel') {
          console.error('Failed to delete scooter:', error)
          this.$message.error('Failed to delete scooter: ' + error.message)
        }
      }
    },

    showAddScooterDialog () {
      this.addScooterDialogVisible = true
      // 默认带入当前站点ID
      this.addScooterForm = {
        latitude: '',
        longitude: '',
        status: 0,
        power: 80,
        config: 'speed:30km/h'
      }
    },
    resetAddScooterForm () {
      if (this.$refs.addScooterForm) {
        this.$refs.addScooterForm.resetFields()
      }
      this.addScooterForm = {
        latitude: '',
        longitude: '',
        status: 0,
        power: 80,
        config: 'speed:30km/h'
      }
    },
    async submitAddScooter () {
      try {
        await this.$refs.addScooterForm.validate()
        this.addScooterLoading = true
        // stationId自动带入当前选中站点
        const requestBody = {
          ...this.addScooterForm,
          stationId: this.selectedStationId
        }
        const response = await this.$request.post('/admin/scooter/add', requestBody)
        if (response.statusCode === 200) {
          this.$message.success('Scooter added successfully')
          this.addScooterDialogVisible = false
          // 刷新当前站点电动车列表
          await this.refreshStations()
        } else {
          throw new Error(response.message || 'Add failed')
        }
      } catch (error) {
        this.$message.error('Add failed: ' + error.message)
      } finally {
        this.addScooterLoading = false
      }
    },
    goToAddScooter () {
      this.$router.push({
        path: '/scooter',
        query: {
          action: 'add',
          stationId: this.selectedStationId
        }
      })
    }
  },
  mounted () {
    // Add security configuration
    window._AMapSecurityConfig = {
      securityJsCode: this.amapConfig.securityJsCode
    }
    // Load station data
    this.refreshStations()
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
  }
}
</script>

<style scoped>
.station-view {
  padding: 20px;
  height: 100%;
  box-sizing: border-box;
  background-color: #f0f2f5;
}

.global-map-card,
.statistics-container,
.station-selection-card,
.station-info-card,
.scooter-list-card {
  margin-bottom: 20px;
}

.global-map-container {
  height: 400px;
  width: 100%;
  border-radius: 4px;
  overflow: hidden;
}

/* 统计卡片样式 */
.statistics-container {
  display: flex;
  justify-content: space-between;
  gap: 20px;
  margin-top: 20px;
}

.statistics-card {
  flex: 1;
  border-radius: 4px;
  overflow: hidden;
}

.statistic-item {
  height: 120px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  color: white;
}

.statistic-item.blue {
  background: linear-gradient(135deg, #1c92d2, #47b8e0);
}

.statistic-item.green {
  background: linear-gradient(135deg, #56ab2f, #a8e063);
}

.statistic-item.orange {
  background: linear-gradient(135deg, #f7971e, #ffd200);
}

.statistic-item.teal {
  background: linear-gradient(135deg, #16a085, #2ecc71);
}

.statistic-icon {
  font-size: 36px;
  padding-right: 20px;
}

.statistic-info {
  display: flex;
  flex-direction: column;
}

.statistic-label {
  font-size: 16px;
  margin-bottom: 10px;
}

.statistic-value {
  font-size: 36px;
  font-weight: bold;
}

.station-form {
  display: flex;
  align-items: center;
}

.station-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions,
.header-controls {
  display: flex;
  align-items: center;
  gap: 10px;
  float: right;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.search-input {
  width: 200px;
}

.location-info {
  color: #606266;
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

.empty-selection {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 40px;
}

.empty-data {
  padding: 40px 0;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .station-view {
    padding: 10px;
  }

  .search-input {
    width: 150px;
  }
}
</style>
