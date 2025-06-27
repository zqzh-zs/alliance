<template>
  <el-dialog
    :model-value="visible"
    title="会议详情"
    width="500px"
    @close="handleClose"
    class="meeting-detail-dialog"
  >
<!--会议表单-->
    <el-descriptions column="1" border class="meeting-detail">
      <el-descriptions-item label="会议名称">{{ detail.title }}</el-descriptions-item>
      <el-descriptions-item label="会议类型">{{ typeMap[detail.type] || detail.type }}</el-descriptions-item>
      <el-descriptions-item label="创建人">{{ detail.organizer }}</el-descriptions-item>
      <el-descriptions-item label="开始时间">{{ detail.start_time }}</el-descriptions-item>
      <el-descriptions-item label="结束时间">{{ detail.end_time }}</el-descriptions-item>
      <el-descriptions-item label="地点">{{ detail.location }}</el-descriptions-item>
      <el-descriptions-item label="内容">{{ detail.summary }}</el-descriptions-item>
      <el-descriptions-item label="状态">
        <el-tag :type="statusTagMap[detail.status]" class="status-tag">
          {{ statusMap[detail.status] || detail.status }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="提交时间">{{ detail.create_time || '无' }}</el-descriptions-item>
      <el-descriptions-item label="会议封面">
        <div class="image-container">
          <!-- 使用 v-if 判断图片路径是否有效 -->
          <img
            v-if="detail.imageUrl && detail.imageUrl.trim() !== ''"
            :src="detail.imageUrl" 
            alt="会议封面"
            class="meeting-image"
            @error="onImageError"
          />
          <!-- v-else 确保与 v-if 配对 -->
          <span v-else class="no-image">暂无</span>
        </div>
      </el-descriptions-item>
    </el-descriptions>

    <template #footer>
      <el-button @click="handleClose" class="close-btn">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
const props = defineProps({
  visible: Boolean,
  detail: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(["update:visible"])

function handleClose() {
  emit("update:visible", false)
}

function onImageError(event) {
  // 图片加载失败时，隐藏图片并显示'暂无'
  event.target.style.display = 'none'
}

const typeMap = {
  SEMINAR: "会议研讨",
  STANDARD: "标准定制",
  TRAINING: "技术培训",
  TOOL_DEV: "工具研发",
  PUBLIC_WELFARE: "公益行动"
}

const statusMap = {
  PENDING: "待审核",
  APPROVED: "已通过",
  REJECTED: "已驳回"
}

const statusTagMap = {
  PENDING: "warning",
  APPROVED: "success",
  REJECTED: "danger"
}
</script>

<style scoped>
/* 强制标题栏居中 */
.meeting-detail-dialog .el-dialog__header {
  display: flex !important;
  justify-content: center !important;  /* 标题水平居中 */
  align-items: center !important;      /* 标题垂直居中 */
  background-color: #fafafa;
  padding: 10px 20px;
  border-radius: 8px 8px 0 0;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

/* 标题文本样式 */
.meeting-detail-dialog .el-dialog__title {
  font-size: 20px !important;
  font-weight: bold !important;
  color: #4d4d4d !important;
  margin: 0;
  padding: 0;
  text-align: center !important;      /* 标题文本居中 */
}

/* 整体样式 */
.meeting-detail {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 状态标签样式 */
.status-tag {
  font-weight: bold;
  font-size: 14px;
  padding: 5px 10px;
  border-radius: 5px;
}

/* 图片容器和样式 */
.image-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.meeting-image {
  width: 100%;
  max-width: 300px;
  height: auto;
  border-radius: 8px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.meeting-image:hover {
  transform: scale(1.05);
}

.no-image {
  font-style: italic;
  color: #999;
}

/* 按钮样式 */
.close-btn {
  background-color: #f4f4f4;
  color: #333;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

.close-btn:hover {
  background-color: #e4e4e4;
}
</style>
