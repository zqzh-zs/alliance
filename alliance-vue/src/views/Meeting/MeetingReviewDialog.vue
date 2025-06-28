<template>
  <el-dialog :model-value="visible" title="会议详情" width="500px" @close="handleDialogClose">
    <el-form label-width="100px" class="meeting-form">
      <el-form-item label="会议名称">
        <span>{{ meeting.title }}</span>
      </el-form-item>
      <el-form-item label="简介">
        <span>{{ meeting.summary }}</span>
      </el-form-item>
      <el-form-item label="开始时间">
        <span>{{ meeting.start_time }}</span>
      </el-form-item>
      <el-form-item label="结束时间">
        <span>{{ meeting.end_time }}</span>
      </el-form-item>
      <el-form-item label="地点">
        <span>{{ meeting.location }}</span>
      </el-form-item>
      <el-form-item label="创建人">
        <span>{{ meeting.organizer }}</span>
      </el-form-item>
      <el-form-item label="会议类型">
        <span>{{ typeDisplayName(meeting.type) }}</span>
      </el-form-item>
      <el-form-item label="状态">
        <el-tag :type="statusTagMap[meeting.status]" class="meeting-status-tag">
          {{ statusDisplayName(meeting.status) }}
        </el-tag>
      </el-form-item>
      <el-form-item label="会议图片">
        <div class="image-container">
          <img
            v-if="meeting.imageUrl && meeting.imageUrl.trim() !== ''"
            :src="meeting.imageUrl"
            alt="会议图片"
            class="meeting-image"
            @error="onImageError"
          />
          <span v-else class="no-image">无图片</span>
        </div>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleDialogClose" class="dialog-close-btn">关闭</el-button>
        <el-button
          v-if="meeting.status === 'PENDING'"
          type="success"
          @click="approveMeeting"
          class="dialog-approve-btn"
        >
          通过
        </el-button>
        <el-button
          v-if="meeting.status === 'PENDING'"
          type="danger"
          @click="rejectMeeting"
          class="dialog-reject-btn"
        >
          驳回
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import { ElMessage } from "element-plus"
import request from "@/utils/request"

// 定义组件接收的 props
const props = defineProps({
  visible: Boolean,
  meeting: {
    type: Object,
    default: () => ({})
  }
})

// 定义 emit 事件，用来通知父组件关闭弹窗
const emit = defineEmits(['update:visible', 'approved', 'rejected'])

// 关闭弹窗的处理函数
const handleDialogClose = () => {
  emit("update:visible", false) // 发出事件通知父组件关闭弹窗
}

// 通过会议的处理函数
const approveMeeting = () => {
  request.post(`/api/meeting/approve/${props.meeting.id}`).then(res => {
    if (res.code === 200) {
      ElMessage.success("会议已通过")
      handleDialogClose() // 关闭弹窗
      emit("approved") // 通知父组件
    } else {
      ElMessage.error(res.msg || "操作失败")
    }
  }).catch(() => {
    ElMessage.error("操作失败")
  })
}

// 驳回会议的处理函数
const rejectMeeting = () => {
  request.post(`/api/meeting/reject/${props.meeting.id}`).then(res => {
    if (res.code === 200) {
      ElMessage.success("会议已驳回")
      handleDialogClose() // 关闭弹窗
      emit("rejected") // 通知父组件
    } else {
      ElMessage.error(res.msg || "操作失败")
    }
  }).catch(() => {
    ElMessage.error("操作失败")
  })
}

// 状态的显示名称
const statusDisplayName = (status: string) => {
  switch (status) {
    case 'PENDING': return '待审核'
    case 'APPROVED': return '已通过'
    case 'REJECTED': return '已驳回'
    case 'OVER': return '已结束'
    default: return '未知'
  }
}

// 会议类型的显示名称
const typeDisplayName = (type: string) => {
  switch (type) {
    case 'SEMINAR': return '会议研讨'
    case 'STANDARD': return '标准定制'
    case 'TRAINING': return '技术培训'
    case 'TOOL_DEV': return '工具研发'
    case 'PUBLIC_WELFARE': return '公益行动'
    default: return '未知'
  }
}

// 状态标签映射
const statusTagMap: Record<string, string> = {
  PENDING: 'warning',
  APPROVED: 'success',
  REJECTED: 'danger',
  OVER: 'info'
}

// 图片加载失败时处理函数
const onImageError = (event: Event) => {
  const target = event.target as HTMLImageElement
  target.style.display = 'none' // 如果图片加载失败，则隐藏图片
}
</script>

<style scoped>
.meeting-image {
  width: 100%;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.no-image {
  color: #999;
}

.dialog-footer {
  display: flex;
  justify-content: space-between;
}

.dialog-close-btn {
  background-color: #f4f4f4;
  color: #333;
  border-radius: 5px;
}

.dialog-approve-btn,
.dialog-reject-btn {
  transition: all 0.3s ease;
}

.dialog-approve-btn:hover,
.dialog-reject-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
}
</style>
