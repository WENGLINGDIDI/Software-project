<template>
  <div class="user-management">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <div class="stat-card stat-card--red" :class="{ 'hover-effect': true }">
          <div class="stat-icon">
            <i class="el-icon-user-solid"></i>
          </div>
          <div class="stat-info">
            <div class="stat-label">Total Users</div>
            <div class="stat-value">{{ users.length }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card--cyan" :class="{ 'hover-effect': true }">
          <div class="stat-icon">
            <i class="el-icon-bank-card"></i>
          </div>
          <div class="stat-info">
            <div class="stat-label">Users with Card</div>
            <div class="stat-value">{{ bindCardUsersCount }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card--blue" :class="{ 'hover-effect': true }">
          <div class="stat-icon">
            <i class="el-icon-price-tag"></i>
          </div>
          <div class="stat-info">
            <div class="stat-label">Users with Discount</div>
            <div class="stat-value">{{ usersWithDiscountCount }}</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card--orange" :class="{ 'hover-effect': true }">
          <div class="stat-icon">
            <i class="el-icon-s-order"></i>
          </div>
          <div class="stat-info">
            <div class="stat-label">Total Orders</div>
            <div class="stat-value">{{ totalOrders }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 搜索区域 -->
    <div class="search-section">
      <el-input
        v-model="searchQuery"
        placeholder="Search by username or email"
        prefix-icon="el-icon-search"
        clearable
        @clear="handleSearchClear"
        @input="handleSearch"
        class="search-input">
      </el-input>
      <el-button type="primary" icon="el-icon-refresh" @click="fetchUsers" class="refresh-btn">Refresh Data</el-button>
    </div>

    <!-- Discount Settings Row -->
    <div class="discount-settings-row">
      <span class="discount-settings-title">Discount applied for:</span>
      <el-form :inline="true" size="small" @submit.native.prevent class="discount-settings-form">
        <el-row :gutter="24" type="flex" align="middle">
          <el-col :span="6">
            <el-form-item label="Frequent users (8+hrs/week)">
              <el-input-number v-model="discountSettings.frequent" :min="0.5" :max="1" :step="0.01" :precision="2" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="Students">
              <el-input-number v-model="discountSettings.student" :min="0.5" :max="1" :step="0.01" :precision="2" />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="Senior citizens">
              <el-input-number v-model="discountSettings.senior" :min="0.5" :max="1" :step="0.01" :precision="2" />
            </el-form-item>
          </el-col>
          <el-col :span="6" class="discount-save-btn-col">
            <el-button type="success" size="medium" class="discount-save-btn" @click="saveDiscountSettings">
              Save
            </el-button>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <div class="table-section">
      <div class="table-header">
        <span class="section-title">User Details</span>
      </div>
      <!-- 用户列表表格 -->
      <el-table
        v-loading="loading"
        :data="filteredUsers"
        style="width: 100%"
        border
        stripe
        highlight-current-row
        @row-click="handleRowClick"
        class="user-table">
        <el-table-column
          prop="name"
          label="Username"
          min-width="150">
          <template slot-scope="scope">
            <div class="user-name">
              <i class="el-icon-user"></i>
              <span>{{ scope.row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="email"
          label="Email"
          min-width="200">
          <template slot-scope="scope">
            <div class="email-cell">
              <i class="el-icon-message"></i>
              <span>{{ scope.row.email }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="discount"
          label="Discount"
          width="180"
          align="center">
          <template slot-scope="scope">
            <div class="discount-cell">
              <el-tag :type="getDiscountTagType(scope.row.discount)" effect="light" size="medium">
                {{ getDiscountText(scope.row.discount) }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="card"
          label="Bank Card"
          width="180"
          align="center">
          <template slot-scope="scope">
            <div class="card-cell">
              <i class="el-icon-bank-card"></i>
              <span v-if="scope.row.card">{{ formatCardNumber(scope.row.card) }}</span>
              <el-tag v-else type="info" effect="plain" size="medium">Not Bound</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          label="Actions"
          width="280"
          align="center">
          <template slot-scope="scope">
            <div class="action-buttons">
              <el-button
                size="mini"
                type="primary"
                @click.stop="showUserDetail(scope.row)">
                <i class="el-icon-view"></i> Details
              </el-button>
              <el-button
                size="mini"
                type="success"
                @click.stop="showUserOrders(scope.row)">
                <i class="el-icon-s-order"></i> Orders
              </el-button>
              <el-button
                size="mini"
                type="warning"
                @click.stop="showDiscountDialog(scope.row)">
                <i class="el-icon-price-tag"></i> Discount
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 50, 100]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="searchedUsers.length"
          background>
        </el-pagination>
      </div>
    </div>

    <!-- 用户详情对话框 -->
    <el-dialog title="User Details" :visible.sync="userDetailVisible" width="50%" custom-class="user-detail-dialog">
      <div v-if="currentUser" class="user-detail">
        <div class="user-profile">
          <div class="user-title">
            <i class="el-icon-user"></i>
            <h2>{{ currentUser.name }}</h2>
          </div>
          <p class="user-email">
            <i class="el-icon-message"></i>
            {{ currentUser.email }}
          </p>
          <div class="user-badges">
            <el-tag :type="getDiscountTagType(currentUser.discount)" effect="dark" class="discount-badge">
              {{ getDiscountText(currentUser.discount) }}
            </el-tag>
            <el-tag type="info" effect="plain" class="role-badge">
              <i class="el-icon-user"></i> {{ currentUser.role }}
            </el-tag>
          </div>
        </div>
        <el-divider></el-divider>
        <el-descriptions :column="2" border class="user-info">
          <el-descriptions-item label="Username">
            <i class="el-icon-user"></i> {{ currentUser.name }}
          </el-descriptions-item>
          <el-descriptions-item label="Email">
            <i class="el-icon-message"></i> {{ currentUser.email }}
          </el-descriptions-item>
          <el-descriptions-item label="Role">
            <el-tag>{{ currentUser.role }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="Discount">
            <div class="discount-detail">
              <el-tag :type="getDiscountTagType(currentUser.discount)" effect="dark">{{ getDiscountText(currentUser.discount) }}</el-tag>
            </div>
          </el-descriptions-item>
          <el-descriptions-item label="Created Time">
            <i class="el-icon-time"></i> {{ formatDate(currentUser.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="Bank Card">
            <div class="card-info">
              <i class="el-icon-bank-card"></i>
              <span v-if="currentUser.card">{{ formatCardNumber(currentUser.card) }}</span>
              <el-tag v-else type="info" effect="plain">Not Bound</el-tag>
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 用户订单对话框 -->
    <el-dialog title="User Orders" :visible.sync="userOrdersVisible" width="70%" custom-class="user-orders-dialog">
      <div v-if="currentUser" class="user-orders-header">
        <div class="orders-title">
          <h3>{{ currentUser.name }}'s Order History</h3>
          <el-tag type="info" effect="plain" class="orders-count-tag">
            <i class="el-icon-s-order"></i> Total: {{ userOrders.length }} orders
          </el-tag>
        </div>
        <div class="orders-summary">
          <el-tag type="success" effect="plain" class="active-orders-tag">
            <i class="el-icon-time"></i> Active: {{ activeOrdersCount }}
          </el-tag>
          <el-tag type="info" effect="plain" class="completed-orders-tag">
            <i class="el-icon-check"></i> Completed: {{ completedOrdersCount }}
          </el-tag>
        </div>
      </div>
      <el-table
        v-loading="ordersLoading"
        :data="paginatedOrders"
        style="width: 100%"
        border
        stripe
        class="orders-table">
        <el-table-column
          prop="id"
          label="Order ID"
          width="100">
          <template slot-scope="scope">
            <el-tag size="medium" effect="plain">#{{ scope.row.id }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="Scooter Info"
          width="120"
          align="center">
          <template slot-scope="scope">
            <div class="scooter-info">
              <div><i class="el-icon-bicycle"></i> ID: {{ scope.row.scooter.id }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          label="Rental Options"
          width="150">
          <template slot-scope="scope">
            <div class="hire-option">
              <div><i class="el-icon-time"></i> {{ scope.row.hireOption.hours }} hours</div>
              <div><i class="el-icon-money"></i> ${{ scope.row.hireOption.money }}</div>
              <div v-if="scope.row.extend_time > 0" class="extend-time">
                <i class="el-icon-plus"></i>{{ scope.row.extend_time }} hours
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          prop="totalCost"
          label="Total Cost"
          width="100">
          <template slot-scope="scope">
            <span class="total-cost">${{ scope.row.totalCost.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="Status"
          width="150"
          align="center">
          <template slot-scope="scope">
            <el-tag :type="getOrderStatusType(scope.row.status)" effect="dark">
              <i :class="getOrderStatusIcon(scope.row.status)"></i>
              {{ getOrderStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="payed"
          label="Payment"
          width="100">
          <template slot-scope="scope">
            <el-tag :type="scope.row.payed === 1 ? 'success' : 'danger'" effect="plain">
              {{ scope.row.payed === 1 ? 'Paid' : 'Unpaid' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="Time"
          width="200">
          <template slot-scope="scope">
            <div class="order-time">
              <div><i class="el-icon-date"></i> Start: {{ formatDate(scope.row.startTime) }}</div>
              <div><i class="el-icon-date"></i> End: {{ formatDate(scope.row.endTime) }}</div>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- Pagination -->
      <div class="order-pagination-container">
        <el-pagination
          @size-change="handleOrderSizeChange"
          @current-change="handleOrderCurrentChange"
          :current-page="orderCurrentPage"
          :page-sizes="[5, 10, 20, 50]"
          :page-size="orderPageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="userOrders.length"
          background>
        </el-pagination>
      </div>
    </el-dialog>

    <el-dialog
      title="Set Discount for User"
      :visible.sync="discountDialogVisible"
      width="400px">
      <div v-if="discountDialogUser">
        <div style="margin-bottom: 16px;">
          <b>User:</b> {{ discountDialogUser.name }}
        </div>
        <el-radio-group v-model="discountDialogType" class="vertical-radio-group">
          <el-radio label="frequent">Frequent users (8+hrs/week)</el-radio>
          <el-radio label="student">Students</el-radio>
          <el-radio label="senior">Senior citizens</el-radio>
        </el-radio-group>
        <div style="margin: 16px 0 0 0;">
          <b>Discount:</b> {{ getDiscountText(discountSettings[discountDialogType]) }}
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="discountDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="applyUserDiscount">Apply</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'UserManagement',
  data () {
    return {
      users: [],
      loading: false,
      userDetailVisible: false,
      userOrdersVisible: false,
      currentUser: null,
      userOrders: [],
      ordersLoading: false,
      searchQuery: '',
      currentPage: 1,
      pageSize: 10,
      discountDialogVisible: false,
      orderCurrentPage: 1,
      orderPageSize: 5,
      discountSettings: {
        frequent: 0.9,
        student: 0.85,
        senior: 0.8
      },
      discountDialogUser: null,
      discountDialogType: 'frequent',
      discountDialogHours: 24,
      totalOrders: 0
    }
  },
  computed: {
    searchedUsers () {
      if (!this.searchQuery) return this.users
      const query = this.searchQuery.toLowerCase()
      return this.users.filter(user =>
        user.name.toLowerCase().includes(query) ||
        user.email.toLowerCase().includes(query)
      )
    },
    filteredUsers () {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.searchedUsers.slice(start, end)
    },
    bindCardUsersCount () {
      return this.searchedUsers.filter(user => user.card !== null).length
    },
    avgDiscount () {
      if (this.searchedUsers.length === 0) return 0
      const sum = this.searchedUsers.reduce((acc, user) => acc + user.discount, 0)
      return (sum / this.searchedUsers.length).toFixed(2)
    },
    usersWithDiscountCount () {
      return this.searchedUsers.filter(user => user.discount < 1).length
    },
    paginatedOrders () {
      const start = (this.orderCurrentPage - 1) * this.orderPageSize
      const end = start + this.orderPageSize
      return this.userOrders.slice(start, end)
    },
    activeOrdersCount () {
      return this.userOrders.filter(order => order.status === 1).length
    },
    completedOrdersCount () {
      return this.userOrders.filter(order => order.status === 3).length
    }
  },
  created () {
    this.fetchUsers()
    this.fetchTotalOrders()
  },
  methods: {
    async fetchUsers () {
      this.loading = true
      try {
        const response = await this.$request.get('/admin/user')
        if (response.statusCode === 200) {
          this.users = response.data
          this.$message.success('Data loaded successfully')
        } else {
          throw new Error(response.message || 'Failed to fetch user data')
        }
      } catch (error) {
        console.error('Failed to fetch user data:', error)
        this.$message.error('Failed to fetch user data: ' + error.message)
      } finally {
        this.loading = false
      }
    },

    async showUserDetail (user) {
      try {
        const response = await this.$request.get(`/user/${user.id}`)
        if (response.statusCode === 200) {
          this.currentUser = response.data
          this.userDetailVisible = true
        } else {
          throw new Error(response.message || 'Failed to fetch user details')
        }
      } catch (error) {
        console.error('Failed to fetch user details:', error)
        this.$message.error('Failed to fetch user details: ' + error.message)
      }
    },

    async showUserOrders (user) {
      this.ordersLoading = true
      this.userOrdersVisible = true
      this.currentUser = user
      this.orderCurrentPage = 1
      try {
        const response = await this.$request.get(`/user/book/${user.id}`)
        if (response.statusCode === 200) {
          this.userOrders = response.data
          if (this.userOrders.length === 0) {
            this.$message.info('No orders found for this user')
          }
        } else {
          throw new Error(response.message || 'Failed to fetch user orders')
        }
      } catch (error) {
        console.error('Failed to fetch user orders:', error)
        this.$message.error('Failed to fetch user orders: ' + error.message)
      } finally {
        this.ordersLoading = false
      }
    },

    async adjustDiscount (user, change) {
      const newDiscount = Math.round((user.discount + change) * 100) / 100

      if (newDiscount >= 0.5 && newDiscount <= 1.0) {
        try {
          const response = await this.$request.put(`/admin/user/discount/${user.id}/${newDiscount}`)
          if (response.statusCode === 200) {
            const updatedUser = response.data
            const index = this.users.findIndex(u => u.id === updatedUser.id)
            if (index !== -1) {
              this.users[index] = updatedUser
            }

            if (this.currentUser && this.currentUser.id === updatedUser.id) {
              this.currentUser = updatedUser
            }

            this.$message.success(`Successfully adjusted ${user.name}'s discount to ${newDiscount * 10}% OFF`)
          } else {
            throw new Error(response.message || 'Failed to adjust discount')
          }
        } catch (error) {
          console.error('Failed to adjust discount:', error)
          this.$message.error('Failed to adjust discount: ' + error.message)
        }
      }
    },

    showDiscountDialog (user) {
      this.discountDialogUser = user
      this.discountDialogType = 'frequent'
      this.discountDialogHours = 24
      this.discountDialogVisible = true
    },

    formatDate (dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString()
    },

    getDiscountTagType (discount) {
      if (discount < 0.8) return 'danger'
      if (discount < 0.9) return 'warning'
      if (discount < 1.0) return 'success'
      return 'info'
    },

    getBatteryColor (power) {
      if (power > 80) return '#67C23A'
      if (power > 50) return '#E6A23C'
      return '#F56C6C'
    },

    handleSearch () {
      this.currentPage = 1
    },

    handleSearchClear () {
      this.searchQuery = ''
      this.currentPage = 1
    },

    handleSizeChange (val) {
      this.pageSize = val
      this.currentPage = 1
    },

    handleCurrentChange (val) {
      this.currentPage = val
    },

    handleRowClick (row) {
      this.showUserDetail(row)
    },

    formatCardNumber (cardNumber) {
      if (!cardNumber) return ''
      return `**** **** **** ${cardNumber.slice(-4)}`
    },

    getOrderStatusText (status) {
      const statusMap = {
        1: 'Active',
        3: 'Completed',
        4: 'Cancelled'
      }
      return statusMap[status] || 'Unknown'
    },

    getOrderStatusType (status) {
      const typeMap = {
        1: 'warning',
        3: 'success',
        4: 'danger'
      }
      return typeMap[status] || 'info'
    },

    getOrderStatusIcon (status) {
      const iconMap = {
        1: 'el-icon-time',
        3: 'el-icon-check',
        4: 'el-icon-close'
      }
      return iconMap[status] || 'el-icon-info'
    },

    handleOrderSizeChange (val) {
      this.orderPageSize = val
      this.orderCurrentPage = 1
    },

    handleOrderCurrentChange (val) {
      this.orderCurrentPage = val
    },

    saveDiscountSettings () {
      this.$message.success('Discount settings saved (local only)')
    },

    async applyUserDiscount () {
      if (!this.discountDialogUser) return
      try {
        const response = await this.$request.post('/admin/discount', {
          userId: this.discountDialogUser.id,
          discount: this.discountSettings[this.discountDialogType],
          discountHours: this.discountDialogHours
        })
        if (response.statusCode === 200) {
          const updatedUser = response.data
          const idx = this.users.findIndex(u => u.id === updatedUser.id)
          if (idx !== -1) {
            this.$set(this.users, idx, updatedUser)
          }
          this.discountDialogVisible = false
          this.$message.success('Discount applied to user')
        } else {
          throw new Error(response.message || 'Failed to apply discount')
        }
      } catch (error) {
        this.$message.error('Failed to apply discount: ' + error.message)
      }
    },

    getDiscountText (discount) {
      return `${((1 - discount) * 100).toFixed(0)}% OFF`
    },

    async fetchTotalOrders () {
      try {
        const response = await this.$request.get('/admin/book/all')
        if (response.statusCode === 200) {
          this.totalOrders = Array.isArray(response.data) ? response.data.length : 0
        } else {
          throw new Error(response.message || 'Failed to fetch orders')
        }
      } catch (error) {
        this.$message.error('Failed to fetch orders: ' + error.message)
        this.totalOrders = 0
      }
    }
  }
}
</script>

<style scoped>
.user-management {
  padding: 20px;
  background-color: #fff;
  min-height: 100%;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  transition: all 0.3s;
  height: 90px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, currentColor, rgba(255,255,255,0.5));
}

.stat-card.hover-effect:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  position: relative;
  overflow: hidden;
}

.stat-icon::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: currentColor;
  opacity: 0.1;
  border-radius: inherit;
}

/* 使用彩虹色系 */
.stat-card--red {
  color: #FF6B6B;  /* 红色系 */
}

.stat-card--cyan {
  color: #4ECDC4;  /* 青色系 */
}

.stat-card--blue {
  color: #45B7D1;  /* 蓝色系 */
}

.stat-card--orange {
  color: #FFA940;  /* 橙色系 */
}

.stat-info {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
  font-weight: 500;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: inherit;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.1);
}

.search-section {
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  background: white;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}

.search-input {
  width: 300px;
}

.search-input :deep(.el-input__inner) {
  border-radius: 8px;
}

.refresh-btn {
  padding: 9px 20px;
  background: linear-gradient(135deg, #45B7D1, #4ECDC4);
  border: none;
  border-radius: 8px;
}

.refresh-btn:hover {
  background: linear-gradient(135deg, #4ECDC4, #45B7D1);
  transform: translateY(-1px);
}

.table-section {
  background-color: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}

.table-header {
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  position: relative;
  padding-left: 12px;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background: linear-gradient(to bottom, #45B7D1, #4ECDC4);
  border-radius: 2px;
}

.user-table {
  margin-bottom: 20px;
  border-radius: 8px;
  overflow: hidden;
}

/* 自定义表格样式 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background-color: #f8faff !important;
  color: #606266;
  font-weight: 600;
  border-bottom: 2px solid #ebeef5;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td) {
  background-color: #fafcff;
}

:deep(.el-table__body tr:hover > td) {
  background-color: #f0f7ff !important;
}

/* 标签样式 */
:deep(.el-tag) {
  border-radius: 6px;
  padding: 0 12px;
  height: 28px;
  line-height: 26px;
}

:deep(.el-tag--success) {
  background-color: #e1f3d8;
  border-color: #e1f3d8;
  color: #67c23a;
}

:deep(.el-tag--warning) {
  background-color: #fdf6ec;
  border-color: #fdf6ec;
  color: #e6a23c;
}

:deep(.el-tag--danger) {
  background-color: #fef0f0;
  border-color: #fef0f0;
  color: #f56c6c;
}

/* 按钮组样式 */
.el-button-group {
  border-radius: 8px;
  overflow: hidden;
}

.action-btn {
  padding: 8px 16px;
  transition: all 0.3s;
}

.action-btn:hover {
  transform: translateY(-1px);
}

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

:deep(.el-pagination.is-background .el-pager li:not(.disabled).active) {
  background: linear-gradient(135deg, #45B7D1, #4ECDC4);
  color: white;
}

:deep(.el-pagination.is-background .el-pager li:not(.disabled):hover) {
  color: #45B7D1;
}

/* 对话框样式 */
:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #f8faff, #f0f7ff);
  padding: 20px;
  margin: 0;
}

:deep(.el-dialog__title) {
  font-weight: 600;
  color: #303133;
  font-size: 18px;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

/* 用户详情样式 */
.user-detail {
  background: linear-gradient(135deg, #fff, #f8faff);
  border-radius: 12px;
  padding: 24px;
}

.user-profile {
  text-align: center;
  padding: 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}

.user-badges {
  margin-top: 16px;
  display: flex;
  justify-content: center;
  gap: 12px;
}

/* 订单表格样式 */
.orders-table {
  margin-top: 16px;
  border-radius: 8px;
  overflow: hidden;
}

.scooter-info {
  line-height: 1.8;
}

.battery-progress {
  margin: 0 8px;
  vertical-align: middle;
}

.total-cost {
  font-weight: 600;
  color: #FF6B6B;
}

.card-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.card-cell i {
  color: #909399;
  font-size: 16px;
}

.card-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-info i {
  color: #909399;
  font-size: 16px;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-name i {
  color: #909399;
  font-size: 16px;
}

.user-name span {
  font-weight: 500;
  color: #303133;
}

.user-profile {
  text-align: center;
  padding: 24px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.05);
}

.user-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-bottom: 8px;
}

.user-title i {
  font-size: 24px;
  color: #909399;
}

.user-title h2 {
  margin: 0;
  color: #303133;
  font-size: 20px;
}

.user-email {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin: 8px 0;
  color: #909399;
}

.user-email i {
  font-size: 16px;
}

.discount-cell {
  display: flex;
  justify-content: center;
  align-items: center;
}

.discount-controls {
  display: flex;
  flex-direction: column;
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  overflow: hidden;
  width: 20px;
  height: 28px;
}

.discount-controls .control-btn {
  margin: 0;
  padding: 0;
  height: 14px;
  width: 100%;
  line-height: 14px;
  font-size: 12px;
  color: #909399;
  border: none;
  background: transparent;
}

.discount-controls .control-btn + .control-btn {
  border-top: 1px solid #DCDFE6;
}

.discount-controls .control-btn:hover {
  color: #409EFF;
  background-color: #f5f7fa;
}

.discount-controls .control-btn[disabled] {
  color: #C0C4CC;
  cursor: not-allowed;
  background-color: #FFF;
}

.discount-detail {
  display: flex;
  align-items: center;
  gap: 8px;
}

.discount-detail .discount-controls {
  display: flex;
  flex-direction: column;
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  overflow: hidden;
  width: 20px;
  height: 28px;
}

.discount-detail .control-btn {
  margin: 0;
  padding: 0;
  height: 14px;
  width: 100%;
  line-height: 14px;
  font-size: 12px;
  color: #909399;
  border: none;
  background: transparent;
}

.discount-detail .control-btn + .control-btn {
  border-top: 1px solid #DCDFE6;
}

.discount-detail .control-btn:hover {
  color: #409EFF;
  background-color: #f5f7fa;
}

.discount-detail .control-btn[disabled] {
  color: #C0C4CC;
  cursor: not-allowed;
  background-color: #FFF;
}

/* 折扣调整对话框样式 */
.discount-dialog {
  border-radius: 8px;
  overflow: hidden;
}

.discount-dialog-content {
  padding: 20px;
  background: linear-gradient(135deg, #fff, #f8faff);
  border-radius: 8px;
}

.user-info-section {
  text-align: center;
  margin-bottom: 24px;
}

.user-info-section h3 {
  font-size: 20px;
  color: #303133;
  margin: 0 0 12px 0;
}

.user-info-section p {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

.discount-controls-section {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

.discount-adjust-controls {
  display: flex;
  gap: 16px;
}

.discount-adjust-controls .control-btn {
  padding: 12px 20px;
  font-size: 14px;
  border-radius: 6px;
  transition: all 0.3s;
}

.discount-adjust-controls .control-btn:hover {
  transform: translateY(-2px);
}

.discount-adjust-controls .control-btn:active {
  transform: translateY(0);
}

.discount-adjust-controls .control-btn[disabled] {
  transform: none;
  opacity: 0.6;
}

/* 修改折扣列样式 */
.discount-cell {
  display: flex;
  justify-content: center;
  align-items: center;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.action-buttons .el-button {
  margin: 0;
  padding: 8px 12px;
}

.action-buttons .el-button + .el-button {
  margin-left: 0;
}

.discount-settings-row {
  margin: 24px 0 18px 0;
  padding: 24px 32px;
  background: linear-gradient(90deg, #f8fafd 60%, #e8f5e9 100%);
  border-radius: 14px;
  box-shadow: 0 2px 12px rgba(76, 175, 80, 0.07);
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 80px;
}

.discount-settings-title {
  font-size: 20px;
  font-weight: 600;
  color: #2d3a4b;
  margin-right: 32px;
  letter-spacing: 0.5px;
}

.discount-settings-form {
  flex: 1;
}

.discount-save-btn-col {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.discount-save-btn {
  font-size: 16px;
  font-weight: 600;
  border-radius: 8px;
  padding: 10px 32px;
  background: linear-gradient(90deg, #43e97b 0%, #38f9d7 100%);
  border: none;
  box-shadow: 0 2px 8px rgba(67, 233, 123, 0.12);
  transition: background 0.3s;
}

.discount-save-btn:hover {
  background: linear-gradient(90deg, #38f9d7 0%, #43e97b 100%);
  color: #fff;
}

@media (max-width: 900px) {
  .discount-settings-row {
    flex-direction: column;
    align-items: flex-start;
    padding: 18px 10px;
  }
  .discount-settings-title {
    margin-bottom: 12px;
    margin-right: 0;
  }
  .discount-save-btn-col {
    justify-content: flex-start;
    margin-top: 10px;
  }
}

.vertical-radio-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 16px 0 0 0;
}
</style>
