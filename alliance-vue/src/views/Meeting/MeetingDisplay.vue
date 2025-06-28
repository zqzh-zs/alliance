<template>
  <div>
    <el-card>
      <!-- 会议筛选 -->
      <el-select
        v-model="filters.type"
        placeholder="选择会议类型"
        style="width: 200px; margin-left: 20px;"
        @change="fetchMeetings"
      >
        <el-option label="全部" value="" />
        <el-option label="会议研讨" value="SEMINAR" />
        <el-option label="标准定制" value="STANDARD" />
        <el-option label="技术培训" value="TRAINING" />
        <el-option label="工具研发" value="TOOL_DEV" />
        <el-option label="公益行动" value="PUBLIC_WELFARE" />
      </el-select>

      <el-input v-model="filters.keyword" placeholder="搜索会议名称" style="width: 200px; margin-left: 20px;" clearable />
      <el-input v-model="filters.organizer" placeholder="输入创建人" style="width: 200px; margin-left: 20px;" clearable />
      <el-date-picker
        v-model="filters.dateRange"
        type="daterange"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        style="margin-left: 20px;"
      />

      <el-button type="primary" style="margin-left: 20px;" @click="fetchMeetings">搜索</el-button>
      <el-button style="margin-left: 20px;" @click="resetFilters">重置</el-button>
      <el-button type="primary" style="margin-left: 20px;" @click="openCreateDialog">创建会议</el-button>

      <!-- 会议列表 -->
      <el-table :data="meetings" style="width: 100%; margin-top: 20px;">
        <el-table-column prop="title" label="会议名称" />
        <el-table-column prop="organizer" label="创建人" />
        <el-table-column prop="start_time" label="开始时间" />
        <el-table-column prop="end_time" label="结束时间" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="text" @click="openDetailDialog(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        background
        layout="prev, pager, next"
        :page-size="pageSize"
        :current-page="page"
        :total="total"
        @current-change="pageChange"
        style="margin-top: 20px;"
      />
    </el-card>

    <!-- 创建会议对话框 -->
    <el-dialog v-model="showCreateDialog" title="创建会议">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="会议名称" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="会议类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择会议类型">
            <el-option label="会议研讨" value="SEMINAR" />
            <el-option label="标准定制" value="STANDARD" />
            <el-option label="技术培训" value="TRAINING" />
            <el-option label="工具研发" value="TOOL_DEV" />
            <el-option label="公益行动" value="PUBLIC_WELFARE" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="start_time">
          <el-date-picker v-model="form.start_time" type="datetime" placeholder="选择开始时间" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="结束时间" prop="end_time">
          <el-date-picker v-model="form.end_time" type="datetime" placeholder="选择结束时间" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="地点" prop="location">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="内容" prop="summary">
          <el-input type="textarea" :rows="3" v-model="form.summary" />
        </el-form-item>
        <el-form-item label="会议封面">
          <el-upload
            class="avatar-uploader"
            action="/api/meeting/upload"
            name="cover"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="form.imageUrl" :src="form.imageUrl" class="avatar" />
            <el-icon v-else><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreateDialog = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">提交</el-button>
      </template>
    </el-dialog>

    <!-- 会议详情对话框 -->
    <MeetingDetailDialog v-model:visible="showDetailDialog" :detail="detail" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from "vue"
import { ElMessage } from "element-plus"
import { Plus } from "@element-plus/icons-vue"
import request from "@/utils/request"
import MeetingDetailDialog from "./MeetingDetailDialog.vue"
import dayjs from "dayjs"

const user = JSON.parse(localStorage.getItem("alliance-user") || "{}")
const isAdmin = user.role === 1

const page = ref(1)
const pageSize = 10
const total = ref(0)
const meetings = ref([])

const filters = reactive({
  keyword: "",
  type: "",
  organizer: "",
  dateRange: []
})

const form = reactive({
  title: "",
  start_time: "",
  end_time: "",
  location: "",
  summary: "",
  imageUrl: "",
  organizer: user.username,
  status: isAdmin ? "APPROVED" : "PENDING",
  type: "SEMINAR"
})

const rules = {
  title: [{ required: true, message: "请输入会议名称", trigger: "blur" }],
  start_time: [{ required: true, message: "请选择开始时间", trigger: "change" }],
  end_time: [{ required: true, message: "请选择结束时间", trigger: "change" }],
  location: [{ required: true, message: "请输入地点", trigger: "blur" }],
  summary: [{ required: true, message: "请输入会议内容", trigger: "blur" }],
  type: [{ required: true, message: "请选择会议类型", trigger: "change" }]
}

const formRef = ref(null)
const showCreateDialog = ref(false)
const showDetailDialog = ref(false)
const detail = ref(null)

function handleUploadSuccess(res) {
  console.log(res)
  if (res.code === 200 && res.data.code === 200) {
    form.imageUrl = res.data.data.url
    ElMessage.success("上传成功")
  } else {
    ElMessage.error(res.msg || "上传失败")
  }
}


function beforeUpload(file) {
  const isImage = file.type.startsWith("image/")
  if (!isImage) {
    ElMessage.error("只能上传图片文件")
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error("上传图片大小不能超过 2MB!")
  }
  return isImage && isLt2M
}

function openCreateDialog() {
  showCreateDialog.value = true
}

function fetchMeetings() {
  let startDate = ""
  let endDate = ""

  if (filters.dateRange?.[0]) {
    startDate = filters.dateRange[0].toISOString().slice(0, 10)
  }
  if (filters.dateRange?.[1]) {
    endDate = filters.dateRange[1].toISOString().slice(0, 10)
  }

  request
    .get("/api/meeting/approved", {
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
    .then(res => {
      if (res.code === 200 && res.data?.code === 200) {
        meetings.value = res.data.data.list
        total.value = res.data.data.total
      } else {
        ElMessage.error(res.msg || "加载失败")
      }
    })
    .catch(() => {
      ElMessage.error("加载失败")
    })
}

function pageChange(val) {
  page.value = val
  fetchMeetings()
}

function handleCreate() {
  formRef.value.validate(valid => {
    if (!valid) return

    form.status = user.role === 1 ? "APPROVED" : "PENDING"

    const payload = {
      ...form,
      start_time: dayjs(form.start_time).format("YYYY-MM-DD HH:mm:ss"),
      end_time: dayjs(form.end_time).format("YYYY-MM-DD HH:mm:ss")
    }

    request.post("/api/meeting/create", payload, {
      params: { role: user.role.toString() }
    }).then(res => {
      if (res.code === 200 && res.data?.code === 200) {
        if (user.role === 1) {
          ElMessage.success("管理员创建会议请求已通过")
        } else {
          ElMessage.success("创建成功，等待审核！")
        }
        showCreateDialog.value = false
        fetchMeetings()
        Object.assign(form, {
          title: "",
          start_time: "",
          end_time: "",
          location: "",
          summary: "",
          imageUrl: "",
          status: user.role === 1 ? "APPROVED" : "PENDING",
          organizer: user.username,
          type: filters.type || "SEMINAR"
        })
      } else {
        ElMessage.error(res.msg || "创建失败")
      }
    }).catch(() => {
      ElMessage.error("创建失败")
    })
  })
  console.log("提交数据", form)
}

function resetFilters() {
  filters.keyword = ""
  filters.organizer = ""
  filters.dateRange = []
  filters.type = ""
  page.value = 1
  fetchMeetings()
}

function openDetailDialog(row) {
  request.get(`/api/meeting/detail/${row.id}`).then(res => {
    if (res.code === 200 && res.data?.code === 200) {
      detail.value = res.data.data
      showDetailDialog.value = true
    } else {
      ElMessage.error(res.msg || "获取详情失败")
    }
  }).catch(() => {
    ElMessage.error("获取详情失败")
  })
}

onMounted(() => {
  fetchMeetings()
})
</script>

<style scoped>
.avatar-uploader {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar {
  width: 100%;
  height: 100%;
  display: block;
}
</style>
