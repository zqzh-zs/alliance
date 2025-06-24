<template>
  <div>
    <el-card>
      <!-- 查询区域 -->
      <div class="query-area">
        <el-input v-model="query.title" placeholder="标题关键词" clearable class="input" />
        <el-input v-model="query.summary" placeholder="摘要关键词" clearable class="input" />
        <el-select v-model="query.status" placeholder="选择状态" clearable class="select">
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已驳回" :value="2" />
        </el-select>
        <el-date-picker v-model="queryTime" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" class="date-picker" style="width: 190px" />
        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="reset">重置</el-button>
        <el-button type="success" @click="openAddDialog">发布动态</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="list" style="width: 100%">
        <el-table-column label="封面" width="100">
          <template #default="scope">
            <img
                :src="scope.row.newsImage || defaultCover"
                style="width: 80px; height: 60px; object-fit: cover; display: block; border-radius: 4px"
            />
          </template>
        </el-table-column>
        <el-table-column label="标题">
          <template #default="scope">
            <el-link @click="goDetail(scope.row.id)">{{ scope.row.title }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" />
        <el-table-column prop="summary" label="摘要" />
        <el-table-column label="发布时间">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag v-if="scope.row.status === 0" type="warning">待审核</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success">已通过</el-tag>
            <el-tag v-else type="danger">已驳回</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" @click="edit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="remove(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination background layout="prev, pager, next" :current-page="query.pageNum" :page-size="query.pageSize" :total="total" @current-change="handlePageChange" />
      </div>
    </el-card>

    <!-- 发布/编辑弹窗 -->
    <el-dialog v-model="addDialog" :title="form.id ? '编辑动态' : '发布动态'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" /></el-form-item>
        <el-form-item label="内容"><el-input type="textarea" v-model="form.content" :rows="5" /></el-form-item>

        <!-- 编辑时显示附件 -->
        <el-form-item label="附件" v-if="form.id && attachments.length">
          <ul>
            <li v-for="item in attachments" :key="item.id">
              <el-link :href="item.fileUrl" target="_blank">{{ item.fileName }}</el-link>
              <el-button type="text" size="small" @click="deleteAttachment(item.id)">删除</el-button>
            </li>
          </ul>
        </el-form-item>
        <el-form-item>
          <div style="color: #999; font-size: 12px;">
            点击<strong>保存</strong>按钮后将进入上传封面与附件的界面
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialog = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="uploadDialog" title="上传封面与附件" width="500px">
      <el-space direction="vertical" fill style="width: 100%">
        <!-- 上传封面 -->
        <el-upload
            :action="uploadUrl(form.id)"
            :data="{ newsId: form.id, type: 'cover' }"
            :on-success="handleUploadSuccess"
            :show-file-list="false"
            :before-upload="() => (uploadingCover = true)"
            @change="() => (uploadingCover = true)"
        >
          <el-button type="primary" icon="Upload">上传封面</el-button>
        </el-upload>

        <!-- 封面图片展示（按钮之间） -->
        <div
            v-if="form.newsImage"
            style="display: flex; justify-content: center; margin: 20px 0;"
        >
          <el-image
              :src="form.newsImage"
              fit="cover"
              style="width: 100%; max-width: 400px; height: auto; border-radius: 10px; border: 1px solid #dcdfe6;"
          />
        </div>

        <!-- 上传附件 -->
        <el-upload
            ref="uploadAttachmentRef"
            :action="uploadUrl(form.id)"
            :data="{ newsId: form.id, type: 'attachment' }"
            :on-success="handleAttachSuccess"
            multiple
            :limit="5"
        >
          <el-button type="warning" icon="Paperclip">上传附件</el-button>
        </el-upload>
      </el-space>

      <!-- 底部按钮 -->
      <template #footer>
        <el-button @click="uploadDialog = false" type="info" icon="Close">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import request from '@/utils/request'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { nextTick } from 'vue'

const router = useRouter()
const query = reactive({
  title: '',
  summary: '',
  status: null,
  startTime: '',
  endTime: '',
  pageNum: 1,
  pageSize: 10,
  onlyMine: true
})
const queryTime = ref([])
const list = ref([])
const total = ref(0)
const defaultCover = '/default.jpg'

const form = reactive({ id: null, title: '', summary: '', content: '', newsImage: '', isTop: 0, viewCount: 0 })
const addDialog = ref(false)
const uploadDialog = ref(false)
const uploadingCover = ref(false)
const saving = ref(false)
const attachments = ref([])
const uploadAttachmentRef = ref(null)

watch(queryTime, (val) => {
  if (val && val.length === 2) {
    query.startTime = val[0]
    query.endTime = val[1]
  } else {
    query.startTime = ''
    query.endTime = ''
  }
})

const load = () => {
  request.get('/news/list', { params: query }).then((res) => {
    list.value = res.data?.list || []
    total.value = res.data?.total || 0
  })
}

const reset = () => {
  Object.assign(query, { title: '', summary: '', status: null, startTime: '', endTime: '', pageNum: 1 })
  queryTime.value = []
  load()
}

const handlePageChange = (val) => { query.pageNum = val; load() }

const remove = (id) => {
  request.delete(`/news/${id}`).then(() => {
    ElMessage.success('删除成功')
    load()
  })
}

const goDetail = (id) => { router.push(`/news/detail/${id}`) }
const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
const openAddDialog = () => {
  Object.assign(form, {
    id: null,
    title: '',
    summary: '',
    content: '',
    newsImage: '',
    isTop: 0,
    viewCount: 0,
    author: currentUser.nickname || currentUser.username || `用户${currentUser.id}` || '匿名用户'
  })

  attachments.value = []

  // 清空附件上传组件
  nextTick(() => {
    uploadAttachmentRef.value?.clearFiles?.()
  })

  addDialog.value = true
}

const edit = async (row) => {
  Object.assign(form, { ...row })
  const res = await request.get(`/news/list/${row.id}`)
  attachments.value = res.data || []
  addDialog.value = true
}

const submit = () => {
  if (!form.title || !form.content) return ElMessage.warning('标题和内容不能为空')
  saving.value = true

  // 提交前清除无效字段，避免干扰后端赋值
  const { author, ...cleanForm } = form

  const payload = {
    ...cleanForm,
    status: 0,
    rejectReason: ''
  }

  const req = form.id
      ? request.put(`/news/${form.id}`, payload)
      : request.post('/news', payload)

  req.then(res => {
    form.id = form.id || res.data?.id || res.id
    ElMessage.success('保存成功')
    addDialog.value = false
    uploadDialog.value = true
    load()
  }).catch(() => {
    ElMessage.error('保存失败')
  }).finally(() => {
    saving.value = false
  })
}

const formatDateTime = (value) => {
  if (!value) return ''
  const date = new Date(value)
  const pad = (n) => String(n).padStart(2, '0')
  const y = date.getFullYear()
  const m = pad(date.getMonth() + 1)
  const d = pad(date.getDate())
  const h = pad(date.getHours())
  const min = pad(date.getMinutes())
  const s = pad(date.getSeconds())
  return `${y}-${m}-${d}`
}

const uploadUrl = (newsId) => `/news/upload/${newsId}`

// const handleUploadSuccess = (res) => {
//   uploadingCover.value = false
//   form.newsImage = res.fileUrl || res.data?.fileUrl || ''
//   request.put(`/news/${form.id}`, { newsImage: form.newsImage }).then(() => {
//     ElMessage.success('封面已更新')
//     load()
//   })
// }

const handleUploadSuccess = (res) => {
  uploadingCover.value = false
  form.newsImage = res.fileUrl || res.data?.fileUrl || ''
  ElMessage.success('封面已更新')
  load()
}

const handleAttachSuccess = () => { ElMessage.success('附件上传成功') }

const deleteAttachment = (id) => {
  request.delete(`/news/attachment/${id}`).then(() => {
    ElMessage.success('附件删除成功')
    attachments.value = attachments.value.filter(item => item.id !== id)
  })
}

onMounted(load)
</script>

<style scoped>
.query-area {
  margin-bottom: 10px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  align-items: center;
}
.input { width: 200px; }
.select { width: 150px; }
.date-picker { width: 250px; }
.pagination { margin-top: 10px; text-align: right; }

</style>