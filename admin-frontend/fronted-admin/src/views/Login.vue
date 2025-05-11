<!-- eslint-disable no-multi-spaces -->
<!-- eslint-disable no-multi-spaces -->
<template>
  <div class="login-bg">
    <div class="login-card">
      <div class="login-card-left">
        <img src="@/assets/login1.png" alt="" class="login-img">
      </div>
      <div class="login-card-right">
        <el-form :model="user" :rules="rules" ref="loginRef" class="login-form">
          <div class="login-title">Welcome to Admin System</div>
          <el-form-item prop="username">
            <el-input prefix-icon="el-icon-user" size="medium" placeholder="Please enter email" v-model="user.email" class="login-input"></el-input>
          </el-form-item>
          <el-form-item prop="password">
            <el-input prefix-icon="el-icon-lock" size="medium" show-password placeholder="Please enter password" v-model="user.password" class="login-input"></el-input>
          </el-form-item>
          <el-form-item prop="code">
            <div class="login-code-row">
              <el-input placeholder="Enter code" prefix-icon="el-icon-circle-check" size="medium" v-model="user.code" class="login-input"></el-input>
              <div class="login-code-img">
                <valid-code @update:value="getCode" />
              </div>
            </div>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-btn" @click="login">Login</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <el-dialog title="Forgot Password" :visible.sync="forgetPassDialogVis" width="350px">
      <el-form :model="forgetUserForm" label-width="80px" style="padding-right: 20px">
        <el-form-item label="Username">
          <el-input v-model="forgetUserForm.username" autocomplete="off" placeholder="Enter username"></el-input>
        </el-form-item>
        <el-form-item label="Phone">
          <el-input v-model="forgetUserForm.phone" autocomplete="off" placeholder="Enter phone"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="forgetPassDialogVis = false">Cancel</el-button>
        <el-button type="primary" @click="resetPassword">Confirm</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import ValidCode from '@/components/ValidCode'

export default {
  name: 'Login',
  components: {
    ValidCode
  },
  data () {
    // 验证码校验
    const validateCode = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('Please enter the verification code'))
      } else if (value.toLowerCase() !== this.code) {
        callback(new Error('Verification code is incorrect'))
      } else {
        callback()
      }
    }

    return {
      forgetUserForm: {}, // 忘记密码的表单数据
      forgetPassDialogVis: false,
      code: '', // 验证码组件传递过来的code
      user: {
        code: '', // 表单里用户输入的code 验证码
        email: '',
        password: '',
        role: 'admin'
      },
      rules: {
        email: [
          { required: true, message: 'Please enter your email', trigger: 'blur' }
        ],
        password: [
          { required: true, message: 'Please enter your password', trigger: 'blur' }
        ],
        code: [
          { validator: validateCode, trigger: 'blur' }
        ]
      }
    }
  },
  created () {

  },
  methods: {
    handleForgetPass () { //  初始化表单的数据
      this.forgetUserForm = {}
      this.forgetPassDialogVis = true
    },
    resetPassword () {
      this.$request.put('/password', this.forgetUserForm).then(res => {
        if (res.code === '200') {
          this.$message.success('Password reset successful')
          this.forgetPassDialogVis = false
        } else {
          this.$message.error(res.msg)
        }
      })
    },
    getCode (code) {
      this.code = code.toLowerCase()
    },
    login () {
      this.$refs.loginRef.validate((valid) => {
        if (valid) {
          // Remove code field before request
          const userData = { ...this.user }
          delete userData.code
          console.log(userData)

          // Validation passed
          this.$request.post('/user/login', userData).then(res => {
            console.log('Login response:', res)
            if (res.statusCode === 200) {
              localStorage.setItem('user', JSON.stringify(res.data.user))
              localStorage.setItem('token', res.data.token)
              this.$router.push('/')
              this.$message.success('Login successful')
            } else {
              this.$message.error(res.message || 'Login failed')
            }
          }).catch(error => {
            console.error('Login error:', error)
            this.$message.error('Login failed, please check your network connection')
          })
        }
      })
    }
  }
}
</script>

<style scoped>
.login-bg {
  min-height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, #43cea2 0%, #185a9d 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-card {
  display: flex;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.18);
  overflow: hidden;
  width: 700px;
  max-width: 95vw;
  min-height: 400px;
}
.login-card-left {
  flex: 1;
  background: linear-gradient(135deg, #43cea2 0%, #185a9d 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}
.login-img {
  width: 90%;
  max-width: 220px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.08);
}
.login-card-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 24px;
}
.login-form {
  width: 100%;
  max-width: 320px;
}
.login-title {
  font-size: 26px;
  font-weight: bold;
  color: #185a9d;
  text-align: center;
  margin-bottom: 28px;
  letter-spacing: 1px;
}
.login-input {
  border-radius: 8px;
  font-size: 16px;
}
.login-code-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.login-code-img {
  flex-shrink: 0;
  height: 36px;
  display: flex;
  align-items: center;
}
.login-btn {
  width: 100%;
  height: 42px;
  font-size: 18px;
  border-radius: 8px;
  font-weight: 600;
  letter-spacing: 1px;
}
.login-footer-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}
.forget-link {
  color: #43cea2;
  cursor: pointer;
  font-size: 15px;
  transition: color 0.2s;
}
.forget-link:hover {
  color: #185a9d;
  text-decoration: underline;
}
</style>
