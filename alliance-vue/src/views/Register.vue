<template>
  <div class="register-container">
    <div class="register-box">
      <h2 class="title">企业注册</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-position="top">

        <el-form-item label="企业名称" prop="companyName">
          <el-input v-model="form.companyName" placeholder="请输入企业名称" />
        </el-form-item>

        <el-form-item label="企业联系方式" prop="companyPhone">
          <el-input v-model="form.companyPhone" placeholder="请输入联系方式" />
        </el-form-item>

        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入账号" prefix-icon="User" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入密码" show-password prefix-icon="Lock" />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="form.confirmPassword" placeholder="请确认密码" show-password prefix-icon="Lock" />
        </el-form-item>

        <el-form-item label="验证码" prop="code">
          <div style="display: flex; align-items: center;">
            <el-input v-model="form.code" placeholder="请输入验证码" style="flex: 1; margin-right: 10px;" />
            <img :src="captchaUrl" @click="refreshCaptcha" alt="验证码" style="height: 32px; cursor: pointer; border: 1px solid #ccc;" />
          </div>
        </el-form-item>


        <el-form-item>
          <el-button type="primary" style="width: 100%" @click="register">注册</el-button>
        </el-form-item>

        <div style="text-align: right">
          已有账号？<router-link to="/login">去登录</router-link>
        </div>

      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import { ElMessage } from 'element-plus';
import request from '@/utils/request';
import { useRouter } from 'vue-router';

const router = useRouter();
const formRef = ref();

const form = reactive({
  companyName: '',
  companyPhone: '',
  username: '',
  password: '',
  confirmPassword: '',
});

const rules = {
  companyName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  companyPhone: [{ required: true, message: '请输入联系方式', trigger: 'blur' }],
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [
    {
      validator: (rule, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur',
    },
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
};

const register = () => {
  formRef.value.validate().then(() => {
    request.post('/auth/company/register', form).then(res => {
      console.log("注册响应结构:", res)
      if (res.code === 200) {
        ElMessage.success(res.data.message || '注册成功');
        router.push('/login');
      } else {
        ElMessage.error(res.errMsg || '注册失败');
        refreshCaptcha(); // 验证失败后刷新验证码
      }
    }).catch(err => {
      console.error('请求出错:', err);
      ElMessage.error('系统错误，请稍后再试');
    });
  });
};


import { onMounted } from 'vue'

form.code = '';

const captchaUrl = ref('');

const refreshCaptcha = () => {
  captchaUrl.value = `/auth/checkCode?t=${Date.now()}`; // 防止缓存
};

onMounted(() => {
  refreshCaptcha();
});

</script>

<style scoped>
.register-container {
  height: 100vh;
  background: url('@/assets/imgs/bg.jpg') no-repeat center center;
  background-size: cover;
  display: flex;
  justify-content: center;
  align-items: center;
}

.register-box {
  width: 420px;
  background-color: rgba(255, 255, 255, 0.95);
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.1);
}

.title {
  text-align: center;
  margin-bottom: 25px;
  font-size: 24px;
  color: #1450aa;
  font-weight: bold;
}
</style>
