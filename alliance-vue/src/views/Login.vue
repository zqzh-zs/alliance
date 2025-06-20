<template>
  <div class="login-container">
    <div class="login-box">
      <div class="title">登录系统</div>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input
              v-model="form.username"
              placeholder="请输入账号"
              :prefix-icon="User"
              size="large"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="form.password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              size="large"
          />
        </el-form-item>

        <el-form-item prop="role">
          <el-select v-model="form.role" placeholder="请选择登录身份" size="large">
            <el-option label="企业用户" value="USER" />
            <el-option label="超级管理员" value="ADMIN" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
      <div class="bottom-tip">
        没有账号？<router-link to="/register">点击注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue"
import { User, Lock } from "@element-plus/icons-vue"
import { ElMessage } from "element-plus"
import request from "@/utils/request"
import router from "@/router"

const form = reactive({
  username: "",
  password: "",
  role: "USER"  // 默认企业用户
})

const rules = {
  username: [{ required: true, message: "请输入账号", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  role: [{ required: true, message: "请选择登录身份", trigger: "change" }]
}

const formRef = ref()

const handleLogin = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      request.post("/auth/login", form).then((res) => {
        if (res.code === 200) {
          ElMessage.success("登录成功")
          localStorage.setItem("alliance-user", JSON.stringify(res.data))
          router.push("/")
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: url("@/assets/imgs/bg.jpg") no-repeat center center;
  background-size: cover;
}

.login-box {
  width: 380px;
  padding: 40px 30px;
  border-radius: 10px;
  background-color: rgba(255, 255, 255, 0.9);
  box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  font-size: 24px;
  font-weight: bold;
  color: #1450aa;
  margin-bottom: 30px;
}

.bottom-tip {
  text-align: right;
  margin-top: 10px;
}
</style>
