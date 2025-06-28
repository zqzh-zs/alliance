<template>
  <div>
    <el-card>
      <div style="margin-bottom: 20px; display: flex; align-items: center; flex-wrap: wrap;">
        <el-select v-model="filters.type" placeholder="选择会议类型" style="width: 200px; margin-left: 20px; margin-bottom: 10px;" @change="handleTypeChange">
          <el-option label="全部" value="" />
          <el-option label="会议研讨" value="SEMINAR" />
          <el-option label="标准定制" value="STANDARD" />
          <el-option label="技术培训" value="TRAINING" />
          <el-option label="工具研发" value="TOOL_DEV" />
          <el-option label="公益行动" value="PUBLIC_WELFARE" />
        </el-select>

        <el-input v-model="filters.keyword" placeholder="搜索会议名称" style="width: 200px; margin-left: 20px; margin-bottom: 10px;" clearable />
        <el-input v-model="filters.organizer" placeholder="输入创建人" style="width: 200px; margin-left: 20px; margin-bottom: 10px;" clearable />

        <el-date-picker
          v-model="filters.dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="margin-left: 20px; margin-bottom: 10px;"
          clearable
        />

        <el-button type="primary" style="margin-left: 20px; margin-bottom: 10px;" @click="fetchMeetings">搜索</el-button>
        <el-button style="margin-left: 20px; margin-bottom: 10px;" @click="resetFilters">重置</el-button>
      </div>

      <!-- 会议表格 -->
      <el-table :data="tableData" style="width: 100%;">
        <el-table-column prop="title" label="会议名称" />
        <el-table-column prop="organizer" label="会议创建人" />
        <el-table-column prop="start_time" label="开始时间" />
        <el-table-column prop="end_time" label="结束时间" />
        <el-table-column prop="status" label="会议状态">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">
              {{ statusDisplayName(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-button type="warning" size="small" @click="openReviewDialog(row)">审核</el-button>
            <el-button type="danger" size="small" @click="deleteMeeting(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="margin-top: 20px; text-align: right;"
        background
        layout="prev, pager, next"
        :page-size="pageSize"
        :total="total"
        :current-page="page"
        @current-change="handlePageChange"
      />
    </el-card>

    <!-- 审核弹窗 -->
    <MeetingReviewDialog
      v-model:visible="reviewDialogVisible"
      :meeting="currentMeeting"
      @approved="fetchMeetings"
      @rejected="fetchMeetings"
    />

    <!-- 编辑会议弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑会议" width="600px">
      <el-form :model="editForm" label-width="100px" ref="editFormRef">
        <el-form-item label="会议名称" required>
          <el-input v-model="editForm.title" />
        </el-form-item>

        <el-form-item label="会议简介">
          <el-input v-model="editForm.summary" type="textarea" />
        </el-form-item>

        <el-form-item label="开始时间" required>
          <el-date-picker v-model="editForm.start_time" type="datetime" placeholder="选择开始时间" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="结束时间" required>
          <el-date-picker v-model="editForm.end_time" type="datetime" placeholder="选择结束时间" style="width: 100%;" />
        </el-form-item>

        <el-form-item label="会议地点">
          <el-input v-model="editForm.location" />
        </el-form-item>

        <el-form-item label="会议封面">
          <div style="margin-bottom: 10px;">
            <img :src="editForm.cover" alt="封面" style="max-width: 100%; height: auto; border: 1px solid #ddd;" v-if="editForm.cover" />
          </div>
          <el-upload
            :show-file-list="false"
            :before-upload="handleImageUpload"
            accept="image/*"
          >
            <el-button size="small">更换封面</el-button>
          </el-upload>
        </el-form-item>

        <el-form-item label="会议类型" required>
          <el-select v-model="editForm.type" placeholder="选择会议类型">
            <el-option label="会议研讨" value="SEMINAR" />
            <el-option label="标准定制" value="STANDARD" />
            <el-option label="技术培训" value="TRAINING" />
            <el-option label="工具研发" value="TOOL_DEV" />
            <el-option label="公益行动" value="PUBLIC_WELFARE" />
          </el-select>
        </el-form-item>

        <el-form-item label="会议状态">
          <el-input :value="statusDisplayName(editForm.status)" disabled />
        </el-form-item>

        <el-form-item label="创建时间">
          <el-input :value="editForm.create_time" disabled />
        </el-form-item>

        <el-form-item label="创建人">
          <el-input :value="editForm.organizer" disabled />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import request from "@/utils/request"
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import dayjs from 'dayjs'
import MeetingReviewDialog from './MeetingReviewDialog.vue'

const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = 10

const filters = reactive({
  type: '',
  keyword: '',
  organizer: '',
  dateRange: []
})

const reviewDialogVisible = ref(false)
const currentMeeting = ref<any>({})

const editDialogVisible = ref(false)
const editForm = reactive({
  id: 0,
  title: '',
  summary: '',
  start_time: '',
  end_time: '',
  location: '',
  organizer: '',
  cover: '',
  type: '',
  status: '',
  create_time: ''
})

const fetchMeetings = async () => {
  let startDate = ''
  let endDate = ''
  if (filters.dateRange?.[0]) {
    startDate = dayjs(filters.dateRange[0]).format('YYYY-MM-DD HH:mm:ss')
  }
  if (filters.dateRange?.[1]) {
    endDate = dayjs(filters.dateRange[1]).format('YYYY-MM-DD HH:mm:ss')
  }

  try {
    const res = await request({
      url: '/api/meeting/search',
      method: 'get',
      params: {
        page: page.value,
        pageSize,
        type: filters.type,
        keyword: filters.keyword,
        organizer: filters.organizer,
        startDate,
        endDate
      }
    })

    console.log(res) // 打印响应结果，确认返回的数据

    // 修改判断条件
    if (res.code === 200 && res.data?.list) {
      tableData.value = res.data.list // 设置会议列表数据
      total.value = res.data.total // 设置总条数
    } else {
      ElMessage.error('获取会议列表失败') // 如果没有符合条件的list，显示错误
    }
  } catch (e) {
    console.error('获取会议信息失败:', e) // 输出错误信息
    ElMessage.error('获取会议列表失败') // 捕获异常并显示错误提示
  }
}


const handlePageChange = (val) => {
  page.value = val
  fetchMeetings()
}

const handleTypeChange = () => {
  page.value = 1
  fetchMeetings()
}

const deleteMeeting = (id) => {
  ElMessageBox.confirm('确定删除该会议？', '提示', { type: 'warning' })
    .then(async () => {
      await request({
        url: `/api/meeting/search/${id}`,
        method: 'delete'
      })
      ElMessage.success('删除成功')
      fetchMeetings()
    })
    .catch(() => {})
}

const statusDisplayName = (status) => {
  switch (status) {
    case 'PENDING': return '待审核'
    case 'APPROVED': return '已通过'
    case 'REJECTED': return '已驳回'
    case 'OVER': return '已结束'
    default: return '未知'
  }
}

const statusTagType = (status) => {
  switch (status) {
    case 'PENDING': return 'info'
    case 'APPROVED': return 'success'
    case 'REJECTED': return 'danger'
    case 'OVER': return 'warning'
    default: return ''
  }
}

const resetFilters = () => {
  filters.type = ''
  filters.keyword = ''
  filters.organizer = ''
  filters.dateRange = []
  page.value = 1
  fetchMeetings()
}

// 编辑会议相关
const openEditDialog = async (row) => {
  try {
    const res = await request({
      url: `/api/meeting/detail/${row.id}`,
      method: 'get'
    })
    if (res.code === 200 && res.data.code === 200) {
      Object.assign(editForm, res.data.data)
      editDialogVisible.value = true
    } else {
      ElMessage.error('获取会议详情失败')
    }
  } catch (e) {
    ElMessage.error('获取会议详情失败')
  }
}

const handleImageUpload = async (file) => {
  const formData = new FormData()
  formData.append('cover', file)  

  try {
    const res = await request({
      url: '/api/meeting/upload',
      method: 'post',
      data: formData,
    })

    if (res.code === 200 && res.data.code === 200) {
      editForm.cover = res.data.data.url
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error('图片上传失败')
    }
  } catch (e) {
    ElMessage.error('图片上传失败')
  }
  return false 
}

const submitEdit = async () => {
  try {
    await request({
      url: `/api/meeting/update/${editForm.id}`,
      method: 'put',
      data: {
        title: editForm.title,
        summary: editForm.summary,
        start_time: editForm.start_time,
        end_time: editForm.end_time,
        location: editForm.location,
        imageUrl: editForm.cover,
        type: editForm.type,
      }
    })
    ElMessage.success('编辑成功')
    editDialogVisible.value = false
    fetchMeetings()
  } catch (e) {
    ElMessage.error('编辑失败')
  }
}

const openReviewDialog = async (row) => {
  try {
    const res = await request({
      url: `/api/meeting/detail/${row.id}`,
      method: 'get'
    })
    if (res.code === 200 && res.data.code === 200) {
      currentMeeting.value = res.data.data
      reviewDialogVisible.value = true
    } else {
      ElMessage.error(res.data.msg || '获取会议详情失败')
    }
  } catch (e) {
    ElMessage.error('获取会议详情失败')
  }
}

onMounted(() => fetchMeetings())
</script>

<style scoped>
.el-card {
  padding: 20px;
}
</style>
