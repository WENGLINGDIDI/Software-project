<template>
    <!-- Main container containing all content -->
    <div id="main" class="container" v-loading="loading"
         element-loading-text="Loading..."
         element-loading-spinner="el-icon-loading"
         element-loading-background="rgba(255, 255, 255, 0.8)">

        <!-- Navigation tabs -->
        <ul class="nav nav-tabs" style="display: flex; align-items: center;">
            <!-- "All" tab for displang all feedback -->
            <li class="nav-item">
                <a href="#all_feedbacks" class="nav-link" :class="{ active: currentTab === 'all_feedbacks' }"
                    @click="switchTab('all_feedbacks')">All</a> <!-- Switch tab when clicked -->
            </li>
            <!-- "Completed" tab for displaying completed feedback -->
            <li class="nav-item">
                <a href="#completed" class="nav-link" :class="{ active: currentTab === 'completed' }"
                    @click="switchTab('completed')">Completed</a> <!-- Switch tab when clicked -->
            </li>
            <li class="nav-item">
                <a href="#uncompleted" class="nav-link" :class="{ active: currentTab === 'uncompleted' }"
                    @click="switchTab('uncompleted')">Uncompleted</a> <!-- Switch tab when clicked -->
            </li>
            <li class="nav-item ml-auto" style="margin-left: auto; display: flex; align-items: center;">
                <div id="searchContainer">
                    <!-- Input field for user to enter search keywords -->
                    <input type="text" v-model="searchInput" placeholder="Search for title" class="form-control"
                        @keyup.enter="handleSearch" />
                    <!-- Search button, triggers searchfeedbacks() method when clicked -->
                    <button id="enter_button" aria-label="search button" @click="handleSearch">
                        <i class="fas fa-search"></i> <!-- FontAwesome search icon -->
                    </button>
                </div>
                <el-button
                    :type="highPriorityFilter ? 'danger' : 'default'"
                    :plain="!highPriorityFilter"
                    size="mini"
                    style="margin-left: 16px; margin-top: 4px; min-width: 120px;; font-weight: bold; background-color: #f56c6c; color:white"
                    @click="toggleHighPriorityFilter">
                    <template v-if="!highPriorityFilter">View High Priority</template>
                    <template v-else>Show All</template>
                </el-button>
                <el-button
                    :type="sortType === 'time' ? 'primary' : 'default'"
                    :plain="sortType !== 'time'"
                    size="mini"
                    style="margin-left: 8px; margin-top: 4px; min-width: 120px;"
                    @click="setSortType('time')">
                    Sort by Time
                </el-button>
                <el-button
                    :type="sortType === 'priority' ? 'primary' : 'default'"
                    :plain="sortType !== 'priority'"
                    size="mini"
                    style="margin-left: 8px; margin-top: 4px; min-width: 120px;"
                    @click="setSortType('priority')">
                    Sort by Priority
                </el-button>
            </li>
        </ul>

        <!-- Error message -->
        <div v-if="error" class="error-message">
            {{ error }}
        </div>

        <!-- Feedback content tab container -->
        <div class="tab-content">
            <div v-if="currentTab === 'all_feedbacks'" class="feedback-list">
                <div v-for="feedback in paginatedUsers"
                    :key="feedback.id"
                     class="feedback-item">
                    <div class="feedback-header">
                        <div class="feedback-time">{{ formatDateTime(feedback.createTime) }}</div>
                        <div class="feedback-tags">
                            <el-tag :type="feedback.priority === 1 ? 'danger' : feedback.priority === 2 ? 'warning' : 'success'">
                                {{ getPriorityText(feedback.priority) }}
                            </el-tag>
                            <el-tag :type="getStatusText(feedback.status) === 'Processed' ? 'success' : 'warning'">
                                {{ getStatusText(feedback.status) }}
                            </el-tag>
                        </div>
                    </div>

                    <div class="feedback-content feedback-content-row">
                        <div class="feedback-content-col-left">
                            <div class="feedback-id">
                                <strong>Feedback ID:</strong> {{ feedback.id }}
                            </div>
                            <div class="feedback-scooter">
                                <strong>Scooter ID:</strong> {{ feedback.book?.scooter?.id || 'No associated scooter' }}
                            </div>
                        </div>
                        <div class="feedback-content-col-right">
                            <div class="feedback-message">
                                <strong>Feedback Content:</strong> {{ feedback.message }}
                            </div>
                            <div v-if="feedback.result" class="feedback-result">
                                <strong>Processing:</strong> {{ feedback.result }}
                            </div>
                        </div>
                    </div>

                    <div class="feedback-actions">
                        <el-button
                            size="small"
                            type="primary"
                            icon="el-icon-view"
                            @click="viewDetails(feedback)">
                            View
                        </el-button>
                        <el-button
                            size="small"
                            type="success"
                            icon="el-icon-edit"
                            @click="openEditModal(feedback)"
                            :disabled="feedback.status === 'Processed'">
                            Process
                        </el-button>
                        <el-button
                            size="small"
                            type="warning"
                            icon="el-icon-star-off"
                            @click="openPriorityDialog(feedback)">
                            Priority
                        </el-button>
                    </div>
                </div>
            </div>

            <div v-if="currentTab === 'completed'" class="feedback-list">
                <div v-for="feedback in sortedCompletedFeedbacks"
                    :key="feedback.id"
                    class="feedback-item">
                    <div class="feedback-header">
                        <div class="feedback-time">{{ formatDateTime(feedback.createTime) }}</div>
                        <div class="feedback-tags">
                            <el-tag :type="feedback.priority === 1 ? 'danger' : feedback.priority === 2 ? 'warning' : 'success'">
                                {{ getPriorityText(feedback.priority) }}
                            </el-tag>
                            <el-tag :type="getStatusText(feedback.status) === 'Processed' ? 'success' : 'warning'">
                                {{ getStatusText(feedback.status) }}
                            </el-tag>
                        </div>
                    </div>

                    <div class="feedback-content feedback-content-row">
                        <div class="feedback-content-col-left">
                            <div class="feedback-id">
                                <strong>Feedback ID:</strong> {{ feedback.id }}
                            </div>
                            <div class="feedback-scooter">
                                <strong>Scooter ID:</strong> {{ feedback.book?.scooter?.id || 'No associated scooter' }}
                            </div>
                        </div>
                        <div class="feedback-content-col-right">
                            <div class="feedback-message">
                                <strong>Feedback Content:</strong> {{ feedback.message }}
                            </div>
                            <div v-if="feedback.result" class="feedback-result">
                                <strong>Processing:</strong> {{ feedback.result }}
                            </div>
                        </div>
                    </div>

                    <div class="feedback-actions">
                        <el-button
                            size="small"
                            type="primary"
                            icon="el-icon-view"
                            @click="viewDetails(feedback)">
                            View
                        </el-button>
                        <el-button
                            size="small"
                            type="success"
                            icon="el-icon-edit"
                            @click="openEditModal(feedback)"
                            :disabled="feedback.status === 'Processed'">
                            Process
                        </el-button>
                        <el-button
                            size="small"
                            type="warning"
                            icon="el-icon-star-off"
                            @click="openPriorityDialog(feedback)">
                            Priority
                        </el-button>
                    </div>
                </div>
            </div>

            <div v-if="currentTab === 'uncompleted'" class="feedback-list">
                <div v-for="feedback in sortedUncompletedFeedbacks"
                    :key="feedback.id"
                    class="feedback-item">
                    <div class="feedback-header">
                        <div class="feedback-time">{{ formatDateTime(feedback.createTime) }}</div>
                        <div class="feedback-tags">
                            <el-tag :type="feedback.priority === 1 ? 'danger' : feedback.priority === 2 ? 'warning' : 'success'">
                                {{ getPriorityText(feedback.priority) }}
                            </el-tag>
                            <el-tag :type="getStatusText(feedback.status) === 'Processed' ? 'success' : 'warning'">
                                {{ getStatusText(feedback.status) }}
                            </el-tag>
                        </div>
                    </div>

                    <div class="feedback-content feedback-content-row">
                        <div class="feedback-content-col-left">
                            <div class="feedback-id">
                                <strong>Feedback ID:</strong> {{ feedback.id }}
                            </div>
                            <div class="feedback-scooter">
                                <strong>Scooter ID:</strong> {{ feedback.book?.scooter?.id || 'No associated scooter' }}
                            </div>
                        </div>
                        <div class="feedback-content-col-right">
                            <div class="feedback-message">
                                <strong>Feedback Content:</strong> {{ feedback.message }}
                            </div>
                            <div v-if="feedback.result" class="feedback-result">
                                <strong>Processing:</strong> {{ feedback.result }}
                            </div>
                        </div>
                    </div>

                    <div class="feedback-actions">
                        <el-button
                            size="small"
                            type="primary"
                            icon="el-icon-view"
                            @click="viewDetails(feedback)">
                            View
                        </el-button>
                        <el-button
                            size="small"
                            type="success"
                            icon="el-icon-edit"
                            @click="openEditModal(feedback)"
                            :disabled="feedback.status === 'Processed'">
                            Process
                        </el-button>
                        <el-button
                            size="small"
                            type="warning"
                            icon="el-icon-star-off"
                            @click="openPriorityDialog(feedback)">
                            Priority
                        </el-button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Pagination container -->
        <div class="pagination-container" id="paginationContainer">
            <!-- Previous page button -->
            <button class="btn btn-secondary" :disabled="currentPage === 1" @click="changePage(-1)"
                aria-label="change the page to the previous">
                <i class="fas fa-angle-left"></i>
            </button>

            <!-- Page numbers -->
            <div id="pageNumbers" class="page-numbers">
                <button v-for="pageNum in currentTotalPages" :key="pageNum" class="page-button"
                    :class="{ active: currentPage === pageNum }" @click="goToPage(pageNum)">
                    <!-- Call goToPage method when button is clicked -->
                    {{ pageNum }} <!-- Display page number inside button -->
                </button>
            </div>

            <!-- Next page button -->
            <button class="btn btn-secondary" :disabled="currentPage === currentTotalPages" @click="changePage(1)"
                aria-label="change the page to the next">
                <i class="fas fa-angle-right"></i>
            </button>
        </div>

        <!-- Edit modal -->
        <el-dialog
            title="Process Feedback"
            :visible.sync="editDialogVisible"
            width="50%">
            <el-form :model="editForm" label-width="100px">
                <el-form-item label="Processing" prop="result">
                    <el-input
                        type="textarea"
                        v-model="editForm.result"
                        :rows="4"
                        placeholder="Please enter Result">
                    </el-input>
                </el-form-item>
            </el-form>
            <span slot="footer">
                <el-button @click="editDialogVisible = false">Cancel</el-button>
                <el-button type="primary" @click="handleEditSubmit">Confirm</el-button>
            </span>
        </el-dialog>

        <!-- Priority adjustment dialog -->
        <el-dialog
            title="Adjust Priority"
            :visible.sync="priorityDialogVisible"
            width="30%">
            <div class="priority-dialog-content">
              <div class="priority-row">
                <span class="priority-label">Priority</span>
                <el-radio-group v-model="priorityForm.priority" class="priority-radio-group">
                  <el-radio :label="1">High</el-radio>
                  <el-radio :label="2">Medium</el-radio>
                  <el-radio :label="3">Low</el-radio>
                </el-radio-group>
              </div>
            </div>
            <span slot="footer">
              <el-button @click="priorityDialogVisible = false">Cancel</el-button>
              <el-button type="primary" @click="handlePrioritySubmit">Confirm</el-button>
            </span>
        </el-dialog>

        <!-- Details modal -->
        <el-dialog
            title="Feedback Details"
            :visible.sync="detailsDialogVisible"
            width="50%">
            <div v-if="selectedFeedback" class="details-content details-row">
                <div class="details-col-left">
                    <p><strong>Feedback ID:</strong> {{ selectedFeedback.id }}</p>
                    <p><strong>Scooter ID:</strong> {{ selectedFeedback.book?.scooter?.id || 'Not associated' }}</p>
                </div>
                <div class="details-col-right">
                    <p><strong>Feedback Content:</strong> {{ selectedFeedback.message }}</p>
                    <p v-if="selectedFeedback.result"><strong>Result:</strong> {{ selectedFeedback.result }}</p>
                </div>
            </div>
        </el-dialog>
    </div>
</template>

<script>

export default {
  name: 'Feedback',
  data () {
    return {
      searchInput: '',
      editDialogVisible: false,
      editForm: {
        feedbackId: null,
        result: ''
      },
      detailsDialogVisible: false,
      selectedFeedback: null,
      priorityDialogVisible: false,
      priorityForm: {
        feedbackId: null,
        priority: 1
      },
      feedbacks: [],
      currentTab: 'all_feedbacks',
      loading: false,
      error: null,
      currentPage: 1,
      totalPages: 1,
      pageSize: 3,
      total: 0,
      highPriorityFilter: false,
      sortType: 'time'
    }
  },
  computed: {
    pageNumbers () {
      const pages = []
      for (let i = 1; i <= this.totalPages; i++) {
        pages.push(i)
      }
      return pages
    },
    normalizedFeedbacks () {
      return this.feedbacks.map(f => ({
        ...f,
        statusNorm: (f.status === '已处理' || f.status === 'Processed')
          ? 'Processed'
          : (f.status === '未处理' || f.status === 'Unprocessed')
              ? 'Unprocessed'
              : f.status
      }))
    },
    completedFeedbacks () {
      return this.normalizedFeedbacks.filter(f => f.statusNorm === 'Processed')
    },
    uncompletedFeedbacks () {
      return this.normalizedFeedbacks.filter(f => f.statusNorm === 'Unprocessed')
    },
    sortedFeedbacks () {
      console.log('Current tab:', this.currentTab)
      console.log('Feedback data:', this.feedbacks)
      console.log('Data length:', this.feedbacks ? this.feedbacks.length : 0)

      if (!this.feedbacks || this.feedbacks.length === 0) {
        console.log('No data to display')
        return []
      }

      return [...this.feedbacks].sort((a, b) => {
        console.log('Sorting data a:', a)
        console.log('Sorting data b:', b)

        const priorityA = typeof a.priority === 'number' ? a.priority : 3
        const priorityB = typeof b.priority === 'number' ? b.priority : 3

        return priorityA - priorityB
      })
    },
    sortedCompletedFeedbacks () {
      if (!this.completedFeedbacks || this.completedFeedbacks.length === 0) {
        return []
      }
      return [...this.completedFeedbacks].sort((a, b) => {
        if (!a.priority) a.priority = 3
        if (!b.priority) b.priority = 3
        return a.priority - b.priority
      })
    },
    sortedUncompletedFeedbacks () {
      if (!this.uncompletedFeedbacks || this.uncompletedFeedbacks.length === 0) {
        return []
      }
      return [...this.uncompletedFeedbacks].sort((a, b) => {
        if (!a.priority) a.priority = 3
        if (!b.priority) b.priority = 3
        return a.priority - b.priority
      })
    },
    getPriorityText () {
      return (priority) => {
        switch (priority) {
          case 1:
            return 'High'
          case 2:
            return 'Medium'
          case 3:
            return 'Low'
          default:
            return 'Unknown'
        }
      }
    },
    getStatusText () {
      return (status) => {
        if (status === '已处理' || status === 'Processed') return 'Processed'
        if (status === '未处理' || status === 'Unprocessed') return 'Unprocessed'
        return status
      }
    },
    filteredUsers () {
      const query = this.searchInput.toLowerCase()
      return this.feedbacks.filter(user =>
        user.book?.user?.name?.toLowerCase().includes(query) ||
        user.message?.toLowerCase().includes(query)
      )
    },
    currentFeedbacks () {
      if (this.currentTab === 'all_feedbacks') {
        return this.filteredUsers
      } else if (this.currentTab === 'completed') {
        return this.sortedCompletedFeedbacks
      } else if (this.currentTab === 'uncompleted') {
        return this.sortedUncompletedFeedbacks
      }
      return []
    },
    sortedCurrentFeedbacks () {
      const list = this.currentFeedbacks.slice()
      if (this.sortType === 'priority') {
        list.sort((a, b) => (a.priority || 3) - (b.priority || 3))
      } else if (this.sortType === 'time') {
        list.sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
      }
      return list
    },
    highPriorityFeedbacks () {
      return this.sortedCurrentFeedbacks.filter(f => f.priority === 1)
    },
    paginatedUsers () {
      const list = this.highPriorityFilter ? this.highPriorityFeedbacks : this.sortedCurrentFeedbacks
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return list.slice(start, end)
    },
    currentTotalPages () {
      const list = this.highPriorityFilter ? this.highPriorityFeedbacks : this.sortedCurrentFeedbacks
      return Math.ceil(list.length / this.pageSize) || 1
    }
  },
  methods: {
    // Get feedback list
    async fetchFeedbacks ({ page = 1, query = '', status = '' } = {}) {
      this.loading = true
      try {
        const response = await this.$request.get('/admin/feedback/all', {
          params: {
            page,
            size: this.pageSize,
            query,
            status
          }
        })

        if (response.statusCode === 200) {
          const rawData = response.data
          this.feedbacks = Array.isArray(rawData) ? rawData : (rawData.records || [])
          this.total = rawData.total || this.feedbacks.length
          this.totalPages = Math.ceil(this.total / this.pageSize)
          this.currentPage = page
        } else {
          throw new Error(response.message || 'Failed to fetch data')
        }
      } catch (error) {
        this.error = error.message
        this.$message.error('Failed to fetch data: ' + error.message)
      } finally {
        this.loading = false
      }
    },

    // Search feedback
    handleSearch () {
      this.currentPage = 1
      this.fetchFeedbacks({
        page: 1,
        query: this.searchInput.trim()
      })
    },

    // Clear search
    handleSearchClear () {
      this.searchInput = ''
      this.currentPage = 1
      this.fetchFeedbacks({ page: 1 })
    },

    // Delete feedback
    async handleDeleteFeedback (feedback) {
      try {
        await this.$confirm('Are you sure you want to delete this feedback?', 'Warning', {
          confirmButtonText: 'Confirm',
          cancelButtonText: 'Cancel',
          type: 'warning'
        })

        const response = await this.$request.delete(`/admin/feedback/${feedback.id}`)
        if (response.statusCode === 200) {
          this.$message.success('Successfully deleted')
          await this.fetchFeedbacks({ page: this.currentPage })
        } else {
          throw new Error(response.message || 'Deletion failed')
        }
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('Deletion failed: ' + error.message)
        }
      }
    },

    // Open edit dialog
    openEditModal (feedback) {
      this.editForm = {
        feedbackId: feedback.id,
        result: feedback.result || ''
      }
      this.editDialogVisible = true
    },

    // Submit edit
    async handleEditSubmit () {
      try {
        const response = await this.$request.post('/admin/feedback/edit', this.editForm)
        if (response.statusCode === 200) {
          const index = this.feedbacks.findIndex(f => f.id === this.editForm.feedbackId)
          if (index !== -1) {
            this.feedbacks[index] = response.data
          }
          this.editDialogVisible = false
          this.$message.success('Successfully processed')
          await this.fetchFeedbacks({ page: this.currentPage })
        } else {
          throw new Error(response.message || 'Processing failed')
        }
      } catch (error) {
        this.$message.error('Processing failed: ' + error.message)
      }
    },

    // Switch tab
    async switchTab (tab) {
      this.currentTab = tab
      this.currentPage = 1
      // Do not call fetchFeedbacks here!
    },

    // View details
    viewDetails (feedback) {
      this.selectedFeedback = feedback
      this.detailsDialogVisible = true
    },

    // Change page
    changePage (direction) {
      const newPage = this.currentPage + direction
      if (newPage >= 1 && newPage <= this.currentTotalPages) {
        this.currentPage = newPage
      }
    },

    // Go to specific page
    goToPage (pageNum) {
      if (pageNum >= 1 && pageNum <= this.currentTotalPages) {
        this.currentPage = pageNum
      }
    },

    // Open priority adjustment dialog
    openPriorityDialog (feedback) {
      this.priorityForm = {
        feedbackId: feedback.id,
        priority: feedback.priority
      }
      this.priorityDialogVisible = true
    },

    // Submit priority adjustment
    async handlePrioritySubmit () {
      try {
        const { feedbackId, priority } = this.priorityForm
        const response = await this.$request.put(`/admin/editPriority/${feedbackId}/${priority}`)

        if (response.statusCode === 200) {
          const index = this.feedbacks.findIndex(f => f.id === feedbackId)
          if (index !== -1) {
            this.feedbacks[index] = response.data
          }
          this.priorityDialogVisible = false
          this.$message.success('Priority successfully adjusted')
          await this.fetchFeedbacks({ page: this.currentPage })
        } else {
          throw new Error(response.message || 'Priority adjustment failed')
        }
      } catch (error) {
        this.$message.error('Priority adjustment failed: ' + error.message)
      }
    },
    toggleHighPriorityFilter () {
      this.highPriorityFilter = !this.highPriorityFilter
      this.currentPage = 1
    },
    setSortType (type) {
      this.sortType = type
      this.currentPage = 1
    },
    formatDateTime (dateStr) {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.getFullYear() + '-' +
        String(date.getMonth() + 1).padStart(2, '0') + '-' +
        String(date.getDate()).padStart(2, '0') + ' ' +
        String(date.getHours()).padStart(2, '0') + ':' +
        String(date.getMinutes()).padStart(2, '0') + ':' +
        String(date.getSeconds()).padStart(2, '0')
    }
  },
  created () {
    this.fetchFeedbacks() // Only fetch all feedbacks once, no status param
  }
}
</script>

<style scoped>
#main.container {
    margin-top: 20px;
    width: 100%;
    height: calc(100vh - 60px - 2.4rem);
    background-color: #ffffff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    border: none;
    padding: 20px;
}

/* Navigation tabs styles */
.nav-tabs {
    border-bottom: 1px solid #e4e7ed;
    margin-bottom: 20px;
}

.nav-tabs .nav-link {
    border: none;
    color: #606266;
    font-size: 14px;
    padding: 12px 20px;
    margin-right: 4px;
    border-radius: 4px;
    transition: all 0.3s;
}

.nav-tabs .nav-link:hover {
    color: #409EFF;
    background-color: #ecf5ff;
}

.nav-tabs .nav-link.active {
    color: #409EFF;
    background-color: #ecf5ff;
    border-bottom: 2px solid #409EFF;
}

/* Search box styles */
#searchContainer {
    display: flex;
    align-items: center;
    background-color: #f5f7fa;
    border-radius: 4px;
    padding: 4px 8px;
}

.form-control {
    border: none;
    background: transparent;
    padding: 8px 12px;
    font-size: 14px;
    color: #606266;
}

.form-control:focus {
    box-shadow: none;
    outline: none;
}

#enter_button {
    background: transparent;
    border: none;
    color: #909399;
    padding: 8px;
    transition: color 0.3s;
}

#enter_button:hover {
    color: #409EFF;
}

/* Loading state */
.loading-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 200px;
    background-color: #f5f7fa;
    border-radius: 4px;
}

/* Error message */
.error-message {
    color: #f56c6c;
    text-align: center;
    padding: 16px;
    background-color: #fef0f0;
    border-radius: 4px;
    margin: 20px 0;
    font-size: 14px;
}

/* Pagination styles */
.pagination-container {
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid #ebeef5;
    display: flex;
    justify-content: center;
    align-items: center;
    gap: 8px;
}

.page-button {
    min-width: 32px;
    height: 32px;
    line-height: 32px;
    text-align: center;
    border: 1px solid #dcdfe6;
    border-radius: 4px;
    background: #fff;
    color: #606266;
    transition: all 0.3s;
}

.page-button:hover {
    color: #409EFF;
    border-color: #c6e2ff;
    background-color: #ecf5ff;
}

.page-button.active {
    color: #409EFF;
    border-color: #409EFF;
    background-color: #ecf5ff;
}

.btn-secondary {
    background: transparent;
    border: 1px solid #dcdfe6;
    color: #606266;
    padding: 8px 12px;
    border-radius: 4px;
    transition: all 0.3s;
}

.btn-secondary:hover:not(:disabled) {
    color: #409EFF;
    border-color: #c6e2ff;
    background-color: #ecf5ff;
}

.btn-secondary:disabled {
    color: #c0c4cc;
    border-color: #e4e7ed;
    background-color: #f5f7fa;
    cursor: not-allowed;
}

/* Modal styles */
.el-dialog {
    border-radius: 8px;
    overflow: hidden;
}

.el-dialog__header {
    padding: 20px;
    border-bottom: 1px solid #e4e7ed;
}

.el-dialog__title {
    font-size: 16px;
    font-weight: 500;
    color: #303133;
}

.el-dialog__body {
    padding: 20px;
}

.el-dialog__footer {
    padding: 20px;
    border-top: 1px solid #e4e7ed;
}

.el-form-item__label {
    color: #606266;
    font-weight: 500;
}

.el-input__inner, .el-textarea__inner {
    border-color: #dcdfe6;
    border-radius: 4px;
    transition: all 0.3s;
}

.el-input__inner:focus, .el-textarea__inner:focus {
    border-color: #409EFF;
}

.el-select {
    width: 100%;
}

/* Responsive adjustments */
@media screen and (max-width: 768px) {
    #main.container {
        padding: 10px;
    }

    .nav-tabs .nav-link {
        padding: 8px 12px;
        font-size: 13px;
    }

    .page-button {
        min-width: 28px;
        height: 28px;
        line-height: 28px;
    }

    .feedback-item {
        padding: 16px;
    }

    .feedback-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 8px;
    }

    .feedback-actions {
        flex-wrap: wrap;
    }
}

.el-radio-group {
    display: flex;
    gap: 24px;
}

.el-tag {
    margin-left: 8px;
}

/* Loading state styles */
:deep(.el-loading-spinner .el-icon-loading) {
    font-size: 30px;
    color: #409EFF;
}

:deep(.el-loading-spinner .el-loading-text) {
    font-size: 14px;
    margin-top: 8px;
    color: #909399;
}

.feedback-list {
    padding: 20px;
}

.feedback-item {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    margin-bottom: 20px;
    box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
    transition: all 0.3s ease;
}

.feedback-item:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 15px 0 rgba(0,0,0,0.15);
}

.feedback-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
    padding-bottom: 10px;
    border-bottom: 1px solid #eee;
}

.feedback-time {
    color: #909399;
    font-size: 14px;
}

.feedback-tags .el-tag {
    margin-left: 10px;
}

.feedback-content {
    margin: 15px 0;
}

.feedback-content > div {
    margin-bottom: 10px;
    line-height: 1.5;
}

.feedback-content strong {
    color: #606266;
    margin-right: 8px;
}

.feedback-message {
    white-space: pre-wrap;
    word-break: break-all;
}

.feedback-actions {
    margin-top: 15px;
    display: flex;
    justify-content: flex-end;
    gap: 10px;
}

.pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: center;
}

@media screen and (max-width: 768px) {
    .feedback-list {
        padding: 10px;
    }

    .feedback-item {
        padding: 15px;
    }

    .feedback-header {
        flex-direction: column;
        align-items: flex-start;
    }

    .feedback-tags {
        margin-top: 10px;
    }

    .feedback-tags .el-tag {
        margin: 5px 5px 5px 0;
    }

    .feedback-actions {
        flex-wrap: wrap;
    }
}

.details-row {
    display: flex;
    gap: 32px;
    align-items: flex-start;
}
.details-col-left {
    flex: 0 0 180px;
    min-width: 140px;
    max-width: 220px;
    text-align: right;
    color: #444;
}
.details-col-right {
    flex: 1 1 0;
    word-break: break-word;
    color: #222;
}

.feedback-content-row {
    display: flex;
    gap: 32px;
    align-items: flex-start;
}
.feedback-content-col-left {
    flex: 0 0 180px;
    min-width: 140px;
    max-width: 220px;
    text-align: left !important;
    color: #444;
}
.feedback-content-col-right {
    flex: 1 1 0;
    word-break: break-word;
    color: #222;
    text-align: left !important;
}

.priority-dialog-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin: 24px 0 8px 0;
}
.priority-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 32px;
}
.priority-label {
  font-weight: 500;
  font-size: 16px;
  margin-right: 16px;
  color: #606266;
  min-width: 80px;
  text-align: right;
}
.priority-radio-group {
  display: flex;
  gap: 24px;
  font-size: 15px;
}
</style>
