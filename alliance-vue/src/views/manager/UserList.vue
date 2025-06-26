<template>
  <div>
    <!-- 查询条件 -->
    <el-form :inline="true" :model="filters" class="filter-form" @submit.native.prevent>
      <el-form-item label="用户名">
        <el-input v-model="filters.name" placeholder="输入用户名" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="filters.phone" placeholder="输入手机号" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="filters.status" placeholder="请选择状态" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchUsers">查询</el-button>
        <el-button @click="resetFilters">重置</el-button>
      </el-form-item>
    </el-form>



    <!-- 新增按钮 -->
    <div style="margin-bottom: 10px">
      <el-button type="primary" @click="openAddDialog">新增</el-button>
    </div>

    <!-- 用户列表 -->
    <el-table :data="users" style="width: 100%" :loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="gender" label="性别" />
      <el-table-column label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="头像" width="80">
        <template #default="{ row }">
          <el-avatar :src="row.avatar || DEFAULT_AVATAR" :size="40" />
        </template>
      </el-table-column>

      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button type="primary"  @click="editUser(row)">编辑</el-button>
          <el-button type="danger"  @click="deleteUser(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 筛选条件提示 -->
    <div style="margin-bottom: 10px; color: #666;">
      当前筛选条件：
      <span v-if="filters.name">用户名包含：{{ filters.name }}；</span>
      <span v-if="filters.phone">手机号包含：{{ filters.phone }}；</span>
      <span v-if="filters.status !== null">
        状态：{{ filters.status === 1 ? '启用' : '禁用' }}；
      </span>
      <span v-if="!filters.name && !filters.phone && filters.status === null">无</span>
    </div>
    <!-- 分页 -->
    <div style="margin-top: 15px; text-align: right;">
      <el-pagination
          :current-page="page"
          :page-size="pageSize"
          :total="total"
          @current-change="handlePageChange"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="isEdit ? '编辑用户' : '新增用户'" v-model="showAddDialog">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option label="未知" :value="0" />
            <el-option label="男" :value="1" />
            <el-option label="女" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
              class="avatar-uploader"
              :action="'/files/upload'"
              name="file"
              :data="{ oldAvatar: form.avatar }"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
          >
            <img :src="form.avatar || DEFAULT_AVATAR" class="avatar" />
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="saveUser">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { Plus } from '@element-plus/icons-vue'


const users = ref([])
const loading = ref(false)
const showAddDialog = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const DEFAULT_AVATAR = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'

const page = ref(1)
const pageSize = ref(5)
const total = ref(0)

const filters = reactive({
  name: '',
  phone: '',
  status: null
})

const form = reactive({
  id: null,
  username: '',
  phone: '',
  status: 1,
  avatar: '',
  email: '',
  gender: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    //        /^1[3-9]\d{9}$/ 是匹配中国大陆手机号的正则表达式。
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号', trigger: 'blur' }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' },
    { required: false }
  ],
  gender: [{ required: false }]
}

const fetchUsers = () => {
  loading.value = true
  request.get('/user/selectAll', {
    params: {
      ...filters,
      page: page.value,
      pageSize: pageSize.value
    }
  }).then(res => {
    if (res.code === 200) {
      users.value = res.data.list || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '获取用户失败')
    }
  }).finally(() => {
    loading.value = false
  })
}

const resetFilters = () => {
  filters.name = ''
  filters.phone = ''
  filters.status = null
  page.value = 1
  fetchUsers()
}

const handlePageChange = (val) => {
  page.value = val
  fetchUsers()
}

const openAddDialog = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    username: '',
    phone: '',
    status: 1,
    avatar: '',
    email: '',
    gender: ''
  })
  showAddDialog.value = true
}

const editUser = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  showAddDialog.value = true
}

const saveUser = () => {
  formRef.value.validate(valid => {
    if (!valid) return

    // 设置 nickname = username，新增时密码默认 "123456"
    const payload = {
      ...form,
      nickname: form.username
    }

    const url = isEdit.value ? `/user/update/${form.id}` : '/user/add'
    const method = isEdit.value ? request.put : request.post

    // 仅新增时加上默认密码
    if (!isEdit.value) {
      payload.password = '123456'
    }

    method(url, payload).then(res => {
      if (res.code === 200) {
        ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
        showAddDialog.value = false
        fetchUsers()
      } else {
        ElMessage.error(res.msg || '操作失败')
      }
    })
  })
}

const deleteUser = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复 确定要删除该用户吗？', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(() => {
    request.delete(`/user/delete/${id}`).then(res => {
      if (res.code === 200) {
        ElMessage.success('删除成功')
        fetchUsers()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    })
  }).catch(() => {
    // 取消操作
  })
}

const handleAvatarSuccess = (res) => {
  if (res.code === 200) {
    form.avatar = res.data
  } else {
    ElMessage.error(res.msg || '上传失败')
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.filter-form {
  margin-bottom: 15px;
}
.avatar-uploader .avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #999;
  width: 80px;
  height: 80px;
  border: 1px dashed #d9d9d9;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
}
</style>
