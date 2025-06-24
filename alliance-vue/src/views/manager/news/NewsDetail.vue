<template>
  <div class="detail-container">
    <el-button type="primary" @click="$router.back()">
      <template #icon>
        <el-icon><ArrowLeft /></el-icon>
      </template>
      返回
    </el-button>
    <el-card style="margin-top: 20px" v-if="loaded">
      <h2>{{ info.title }}</h2>

      <p style="font-size: 12px; color: #888;">
        作者：{{ info.author }} ｜
        创建时间：{{ formatDate(info.createTime) }} ｜
        更新时间：{{ formatDate(info.updateTime) }} ｜
        是否置顶：{{ info.isTop === 1 ? '是' : '否' }} ｜
        浏览次数：{{ info.viewCount }}
      </p>

      <div class="news-detail-wrapper">
        <!-- 摘要作为正文前导文字 -->
        <p class="summary-inline">{{ info.summary }}</p>

        <!-- 正文 -->
        <div class="content-text" v-html="info.content"></div>
      </div>

      <div v-if="info.newsImage" class="cover-wrapper">
        <div class="section-label">封面图片</div>
        <el-image :src="info.newsImage" style="max-width: 280px; border-radius: 6px;" />
      </div>

      <div v-if="info.attachments?.length" class="attachment-wrapper">
        <div class="section-label">附件下载</div>
        <ul class="attachment-list">
          <li v-for="a in info.attachments" :key="a.id">
            <a :href="a.fileUrl" target="_blank" download>{{ a.fileName }}</a>
          </li>
        </ul>
      </div>

      <div v-if="info.author === user.username" class="status-wrapper">
        <p class="status-line">
          状态：
          <el-tag :type="statusTag(info.status)" class="status-tag">
            {{ statusText(info.status) }}
          </el-tag>
        </p>
        <p v-if="info.status === 2" class="reject-reason">驳回原因：{{ info.rejectReason }}</p>
      </div>
    </el-card>

    <el-skeleton v-else animated rows="6" />
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue"
import { ArrowLeft } from '@element-plus/icons-vue'
import { useRoute } from "vue-router"
import request from "@/utils/request"
import dayjs from "dayjs"

const route = useRoute()
const id = route.params.id
const user = JSON.parse(localStorage.getItem("alliance-user") || "{}")

const info = ref({})
const loaded = ref(false)

const formatDate = (val) => {
  return val ? dayjs(val).format("YYYY-MM-DD HH:mm:ss") : ""
}

const statusText = (status) => {
  return status === 0 ? "待审核" : status === 1 ? "已通过" : "已驳回"
}

const statusTag = (status) => {
  return status === 0 ? "warning" : status === 1 ? "success" : "danger"
}

onMounted(() => {
  request.get(`/news/${id}`).then(res => {
    info.value = res.data
    loaded.value = true

    // ✅ 这里是调试的关键位置
    console.log("user", user)
    console.log("user.username", user.username)
    console.log("info.author", res.data.author)
    console.log("是否相等:", user.username == res.data.author)

    console.log('详情页数据：', res.data)
  })
})
</script>

<style scoped>
.detail-container {
  max-width: 800px;
  margin: 0 auto;
}
.news-detail-wrapper {
  max-width: 800px;
  margin: 20px auto;
  padding: 0 15px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  color: #333;
  font-size: 16px;
  line-height: 1.7;
}

.summary-inline {
  font-size: 14px;
  color: #777;
  font-style: italic;
  margin-bottom: 24px;
  white-space: pre-wrap;
  border-left: 3px solid #f9b44c;
  padding-left: 10px;
}

/* 正文内容 */
.content-text p {
  margin-bottom: 1.4em;
}

.content-text img {
  max-width: 100%;
  border-radius: 6px;
  margin: 15px 0;
  display: block;
}

.content-text a {
  color: #f9b44c;
  text-decoration: underline;
}

.content-text h2,
.content-text h3,
.content-text h4 {
  margin: 1.6em 0 0.8em 0;
  font-weight: 600;
  color: #f9b44c;
}

.section-label {
  font-size: 13px;
  color: #999;
  margin-bottom: 6px;
  user-select: none;
  font-weight: 500;
}

.cover-wrapper {
  margin-bottom: 20px;
}

.attachment-wrapper {
  margin-bottom: 20px;
}

.attachment-list {
  list-style: none;
  padding-left: 0;
  margin: 0;
  font-size: 14px;
  color: #555;
}

.attachment-list li {
  margin-bottom: 6px;
}

.attachment-list a {
  color: #666;
  text-decoration: none;
  transition: color 0.3s;
}

.attachment-list a:hover {
  color: #f9b44c;
  text-decoration: underline;
}

.status-wrapper {
  margin-top: 12px;
  font-size: 14px;
  color: #666;
}

.status-line {
  margin-bottom: 6px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-tag {
  font-size: 12px;
  padding: 2px 8px;
  font-weight: 500;
}

.reject-reason {
  color: #b85c5c;
  font-style: italic;
  font-size: 13px;
  margin-left: 12px;
}
</style>