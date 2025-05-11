<template>
  <div class="feedback-holder" :class="{ 'modal-open': showEditModal }">
    <div class="feedback-header">
      <div class="header-left">
        <div class="deadline-date">{{ feedback.createTime }}</div>
      </div>
    </div>
    <div class="feedback-card">
      <div class="feedback-body">
        <div class="feedback-content">
          <div class="id-and-tags">
            <div class="tags-container">
              <el-tag :type="getPriorityType(feedback.priority)">
                {{ getPriorityText(feedback.priority) }}
              </el-tag>
              <el-tag :type="feedback.status === '已处理' ? 'success' : 'warning'">
                {{ feedback.status }}
              </el-tag>
            </div>
            <div class="vehicle-info">
              <p class="vehicle-id"><strong>反馈ID: </strong>{{ feedback.id }}</p>
              <p class="scooter-id" v-if="feedback.book && feedback.book.scooter">
                <strong>电动车ID: </strong>{{ feedback.book.scooter.id }}
              </p>
            </div>
          </div>
          <div class="message-section">
            <p class="message"><strong>反馈内容: </strong>{{ feedback.message }}</p>
            <div class="result-section" v-if="feedback.result">
              <strong>处理结果:</strong>
              <div class="result-content">{{ feedback.result }}</div>
            </div>
          </div>
        </div>
        <div class="feedback-actions">
          <el-button type="info" size="mini" icon="el-icon-view"
                     @click="$emit('view', feedback)"
                     title="查看详情">
          </el-button>
          <el-button type="primary" size="mini" icon="el-icon-edit"
                     @click="$emit('edit', feedback)"
                     title="编辑反馈"
                     :disabled="feedback.status === '已处理'">
          </el-button>
          <el-button type="danger" size="mini" icon="el-icon-delete"
                     @click="$emit('delete', feedback)"
                     title="删除反馈">
          </el-button>
        </div>
      </div>
    </div>

    <!-- 编辑反馈的模态框 -->
    <transition name="modal">
      <div v-if="showEditModal" class="modal-overlay" @click.self="showEditModal = false">
        <div class="modal-content" :class="{ 'entering': showEditModal }">
          <div class="modal-header">
            <h3>编辑反馈</h3>
            <button class="modal-close" @click="showEditModal = false">&times;</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>优先级</label>
              <select v-model="editForm.priority" class="form-control">
                <option value="High">High</option>
                <option value="Medium">Medium</option>
                <option value="Low">Low</option>
              </select>
            </div>
            <div class="form-group">
              <label>状态</label>
              <select v-model="editForm.status" class="form-control">
                <option value="Completed">已完成</option>
                <option value="Uncompleted">未完成</option>
              </select>
            </div>
            <div class="form-group">
              <label>管理员评论</label>
              <textarea v-model="editForm.comment" class="form-control" rows="4" placeholder="请输入处理意见或备注信息"></textarea>
            </div>
          </div>
          <div class="modal-footer">
            <button class="btn-cancel" @click="showEditModal = false">取消</button>
            <button class="btn-submit" @click="submitEdit">确定</button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: 'FeedbackItem',
  props: {
    feedback: {
      type: Object,
      required: true
    }
  },
  data () {
    return {
      showEditModal: false,
      editForm: {
        priority: '',
        status: '',
        comment: ''
      }
    }
  },
  watch: {
    feedback: {
      handler (newVal) {
        this.editForm = {
          priority: newVal.priority,
          status: newVal.status,
          comment: newVal.comment || ''
        }
      },
      immediate: true
    }
  },
  methods: {
    getPriorityText (priority) {
      switch (priority) {
        case 1:
          return '高'
        case 2:
          return '中'
        case 3:
          return '低'
        default:
          return '未知'
      }
    },
    getPriorityType (priority) {
      switch (priority) {
        case 1:
          return 'danger'
        case 2:
          return 'warning'
        case 3:
          return 'info'
        default:
          return 'info'
      }
    },
    submitEdit () {
      this.$emit('update', {
        id: this.feedback.id,
        ...this.editForm
      })
      this.showEditModal = false
    }
  }
}
</script>

<style scoped>
.feedback-holder {
  margin-bottom: 20px;
  border: 1px solid #e8e8e8;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.feedback-holder:hover {
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.feedback-header {
  padding: 12px 20px;
  border-bottom: 1px solid #e8e8e8;
  background: #f8f9fa;
}

.deadline-date {
  font-size: 14px;
  color: #606266;
}

.feedback-card {
  padding: 20px;
}

.feedback-body {
  position: relative;
  padding-right: 120px;
}

.feedback-content {
  margin-bottom: 16px;
}

.id-and-tags {
  margin-bottom: 12px;
}

.tags-container {
  margin-bottom: 8px;
}

.tags-container .el-tag {
  margin-right: 8px;
}

.vehicle-info {
  font-size: 14px;
  color: #606266;
}

.vehicle-info p {
  margin: 4px 0;
}

.message-section {
  color: #303133;
}

.message {
  margin-bottom: 12px;
  line-height: 1.5;
}

.result-section {
  padding: 8px;
  background: #f8f9fa;
  border-radius: 4px;
}

.result-content {
  margin-top: 4px;
  color: #606266;
}

.feedback-actions {
  position: absolute;
  right: 0;
  top: 0;
  display: flex;
  gap: 8px;
}

.el-button [class^="el-icon-"] {
  margin-right: 0;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  z-index: 1002;
  padding-top: 80px;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(2px);
  -webkit-backdrop-filter: blur(2px);
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.2);
  position: relative;
  margin: 0 auto;
  border: none;
  transform: translateY(0);
  transition: transform 0.3s ease;
  will-change: transform;
}

.modal-content.entering {
  transform: translateY(-20px);
}

.modal-header {
  padding: 24px 30px;
  border-bottom: 4px solid #e8e8e8;
  position: relative;
  display: flex;
  align-items: center;
}

.modal-header h3 {
  font-size: 22px;
  margin: 0;
  flex-grow: 1;
}

.modal-close {
  position: absolute;
  top: 15px;
  right: 15px;
  background: none;
  border: none;
  font-size: 28px;
  color: #909399;
  cursor: pointer;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  padding: 0;
  margin: 0;
  line-height: 1;
}

.modal-close:hover {
  color: #F56C6C;
  transform: scale(1.1);
}

.modal-body {
  padding: 30px;
}

.form-group {
  margin-bottom: 30px;
}

.form-group label {
  display: block;
  margin-bottom: 12px;
  color: #2c3e50;
  font-weight: 600;
  font-size: 18px;
}

.form-control {
  width: 100%;
  padding: 12px 16px;
  border: 4px solid #dcdfe6;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  will-change: transform;
  transform: translateZ(0);
}

.form-control:focus {
  border-color: #409EFF;
  outline: none;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

select.form-control {
  height: 48px;
  padding-right: 36px;
  background-image: linear-gradient(45deg, transparent 50%, #606266 50%),
                    linear-gradient(135deg, #606266 50%, transparent 50%);
  background-position: calc(100% - 20px) calc(1em + 4px),
                      calc(100% - 15px) calc(1em + 4px);
  background-size: 6px 6px,
                  6px 6px;
  background-repeat: no-repeat;
  appearance: none;
  -webkit-appearance: none;
  font-weight: 500;
}

textarea.form-control {
  min-height: 100px;
  resize: vertical;
  line-height: 1.6;
}

.modal-footer {
  padding: 24px 30px;
}

.btn-cancel,
.btn-submit {
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  min-width: 100px;
}

.btn-cancel {
  background: #fff;
  border: 4px solid #dcdfe6;
  color: #606266;
}

.btn-submit {
  background: #409EFF;
  border: 4px solid #409EFF;
  color: white;
}

.btn-cancel:hover {
  background: #f0f2f5;
  border-color: #c0c4cc;
}

.btn-submit:hover {
  background: #66b1ff;
  border-color: #66b1ff;
}

@media screen and (max-width: 768px) {
  .feedback-body {
    padding-right: 0;
  }

  .feedback-actions {
    position: relative;
    justify-content: flex-end;
    margin-top: 15px;
  }

  .deadline-date {
    font-size: 20px;
  }

  .feedback-content p {
    font-size: 15px;
  }

  .id-and-tags {
    flex-direction: column;
    gap: 10px;
  }

  .tags-container {
    flex-direction: row;
    gap: 10px;
  }

  .modal-content {
    width: 95%;
    margin: 20px;
  }
}

/* 添加模态框动画 */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 0.3s ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-to,
.modal-leave-from {
  opacity: 1;
}
</style>
