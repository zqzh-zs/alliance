<template>
  <div style="width: 50%">
    <!-- 基本资料卡片 -->
    <div class="card">
      <el-form :model="data.user" label-width="100px" style="padding-right: 50px">
        <el-form-item label="头像">
          <el-upload
              :show-file-list="false"
              class="avatar-uploader"
              action="http://localhost:8080/files/upload"
              :on-success="handleFileUpload">
            <img v-if="data.user.avatar" :src="data.user.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>

        <el-form-item label="账号">
          <el-input v-model="data.user.username" disabled />
        </el-form-item>

        <el-form-item label="昵称" v-if="data.user.role === 2">
          <el-input v-model="data.user.nickname" />
        </el-form-item>

        <el-form-item label="性别">
          <el-radio-group v-model="data.user.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="手机">
          <el-input v-model="data.user.phone" />
        </el-form-item>

        <el-form-item label="邮箱">
          <el-input v-model="data.user.email" />
        </el-form-item>

        <el-form-item label="注册时间">
          <el-tag>{{ data.user.createTime }}</el-tag>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="saveUserInfo">保存资料</el-button>
          <el-button type="warning" @click="data.pwdVisible = true">修改密码</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 修改密码弹窗 -->
    <el-dialog v-model="data.pwdVisible" title="修改密码" width="30%">
      <el-form :model="data.pwdForm" label-width="100px">
        <el-form-item label="旧密码">
          <el-input type="password" v-model="data.pwdForm.oldPassword" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input type="password" v-model="data.pwdForm.newPassword" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input type="password" v-model="data.pwdForm.confirmPassword" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="data.pwdVisible = false">取消</el-button>
        <el-button type="primary" @click="savePassword">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive } from "vue"
import request from "@/utils/request"
import { ElMessage } from "element-plus"

// 判断身份：role === 1 是管理员，2 是企业用户
const data = reactive({
  user: JSON.parse(localStorage.getItem("alliance-user") || "{}"),
  pwdVisible: false,
  pwdForm: {
    oldPassword: "",
    newPassword: "",
    confirmPassword: ""
  }
})
const emit = defineEmits(['updateUser'])  // <--- 添加这行
// 接口前缀：根据角色动态决定
const prefix = data.user.role === 1 ? "/admin" : "/user"

// 加载个人信息（附带 token）
request.get(`${prefix}/info`, {
  headers: {
    Authorization: "Bearer " + data.user.token
  }
}).then(res => {
  if (res.code === 200) {
    data.user = { ...data.user, ...res.data }
    localStorage.setItem("alliance-user", JSON.stringify(data.user))
  }
})

// 上传头像成功后设置头像 URL
const handleFileUpload = (file) => {
  data.user.avatar = file.data
  localStorage.setItem("alliance-user", JSON.stringify(data.user))
  emit("updateUser")   // <--- 通知父组件刷新头像
}

// 保存基本资料
const saveUserInfo = () => {
  request.put(`${prefix}/update`, data.user, {
    headers: {
      Authorization: "Bearer " + data.user.token
    }
  }).then(res => {
    if (res.code === 200) {
      ElMessage.success("资料更新成功")
      localStorage.setItem("alliance-user", JSON.stringify(data.user))
    } else {
      ElMessage.error(res.msg)
    }
  })
}

// 修改密码
const savePassword = () => {
  if (data.pwdForm.newPassword !== data.pwdForm.confirmPassword) {
    ElMessage.error("两次密码不一致")
    return
  }

  request.put(`${prefix}/password`, data.pwdForm, {
    headers: {
      Authorization: "Bearer " + data.user.token
    }
  }).then(res => {
    if (res.code === 200) {
      ElMessage.success("密码修改成功")
      data.pwdVisible = false
      data.pwdForm = { oldPassword: "", newPassword: "", confirmPassword: "" }
    } else {
      ElMessage.error(res.msg)
    }
  })
}
</script>

<style scoped>
.avatar {
  width: 120px;
  height: 120px;
  display: block;
}
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}
.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
}
</style>
