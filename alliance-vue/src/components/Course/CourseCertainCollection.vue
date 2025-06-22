<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { useRoute } from 'vue-router'
import dayjs from 'dayjs'

const route = useRoute()
const collectionId = Number(route.params.id)

const collection = reactive({
  collection_name: '',
  cover_image: '',
  description: ''
})

const courses = ref([])
const videoDialogVisible = ref(false)
const currentVideo = ref('')
const sortAsc = ref(true)

const getFullImageUrl = (path) => {
  if (!path) return ''
  return path.startsWith('http') ? path : `http://localhost:8080${path}`
}

const formatDate = (date) => {
  return date ? dayjs(date).format('YYYY-MM-DD') : '未知'
}

async function fetchCollectionDetail() {
  try {
    const res = await axios.get(`http://localhost:8080/course-certain-collection/${collectionId}/detail`)
    if (res.data.code === 200) {
      const data = res.data.data.data
      if (data.collection) {
        Object.assign(collection, data.collection)
      }

      if (Array.isArray(data.coursesPage)) {
        courses.value = [...data.coursesPage]
        sortCourses()
      } else {
        console.warn("⚠️ 未返回课程列表或格式错误", data.coursesPage)
      }
    } else {
      console.error('❌ 获取合集详情失败: ', res.data.msg || res.data.errMsg)
    }
  } catch (error) {
    console.error('🚨 请求合集详情异常: ', error)
  }
}

function toggleSortOrder() {
  sortAsc.value = !sortAsc.value
  sortCourses()
}

function sortCourses() {
  courses.value.sort((a, b) =>
    sortAsc.value ? a.sort_order - b.sort_order : b.sort_order - a.sort_order
  )
}

function playVideo(url) {
  currentVideo.value = url
  videoDialogVisible.value = true
}

async function changeSortOrder(course) {
  try {
    await axios.put(`http://localhost:8080/course-certain-collection/${collectionId}/course/${course.id}/sortOrder`, null, {
      params: { sortOrder: course.sort_order }
    })
    await fetchCollectionDetail()
  } catch (error) {
    console.error('排序更新失败', error)
  }
}

onMounted(() => {
  fetchCollectionDetail()
})
</script>

<template>
  <div class="collection-detail">
    <!-- 🌟 合集信息卡片 -->
	
    <el-card shadow="always" class="collection-card">
	<h1 class="collection-title">{{ collection.collection_name }}</h1>
      <el-row :gutter="20" align="middle">
        <el-col :span="8" class="cover-container">
		  
          <img
            v-if="getFullImageUrl(collection.cover_image)"
            :src="getFullImageUrl(collection.cover_image)"
            alt="合集封面"
            class="cover-image"
          />
          <div v-else class="cover-placeholder">暂无封面</div>
        </el-col>

        <el-col :span="16">
          <p class="collection-description">{{ collection.description || '暂无简介' }}</p>
        </el-col>
      </el-row>
    </el-card>

<el-row align="middle" justify="space-between" style="margin-bottom: 20px; flex-wrap: nowrap;">
  <!-- 左侧标题 -->
  <el-col :span="12" style="flex: 1;">
    <h3 style="margin: 0;">课程视频列表</h3>
  </el-col>

  <!-- 右侧按钮 -->
  <el-col :span="12" style="text-align: right;">
    <el-button type="primary" size="small" @click="toggleSortOrder">
      {{ sortAsc ? '升序' : '降序' }}
    </el-button>
  </el-col>
</el-row>



    <div v-for="course in courses" :key="course.id" class="course-card">
      <el-card shadow="hover">
        <el-row gutter="20">
          <el-col :span="6">
            <img
              :src="getFullImageUrl(course.cover_image)"
              alt="课程封面"
              class="video-cover"
            />
            <h4 class="center-title">{{ course.course_name }}</h4>
          </el-col>

          <el-col :span="1" />

          <el-col :span="15">
            <p><strong>介绍：</strong>{{ course.introduction || '暂无介绍' }}</p>
            <p><strong>作者：</strong>{{ course.author || '未知' }}</p>
            <p><strong>上传时间：</strong>{{ formatDate(course.create_time) }}</p>
            <p><strong>更新时间：</strong>{{ formatDate(course.update_time) }}</p>

            <div style="margin-top: 10px;">
              <el-button size="small" type="primary" @click="playVideo(course.video_url)">播放</el-button>
            </div>
          </el-col>
        </el-row>
      </el-card>
    </div>


	
	  <el-dialog
	    v-model="videoDialogVisible"
	    width="60%"
	    title="视频播放"
	    :destroy-on-close="true"
	  >
	    <!-- 右上角自定义操作区域 -->
	    <template #header>
	      <div style="display: flex; justify-content: space-between; align-items: center;">
	        <span>视频播放</span>
	        <el-button
	          type="text"
	          icon="el-icon-full-screen"
	          @click="enterFullscreen"
	          style="font-size: 16px;"
	          title="全屏播放"
	        />
	      </div>
	    </template>
	
	    <p style="font-size: 12px; color: gray; margin-bottom: 10px;">
	      当前视频地址：{{ getFullImageUrl(currentVideo) }}
	    </p>
	
	    <video
	      v-if="videoDialogVisible"
	      ref="videoRef"
	      :key="currentVideo"
	      :src="getFullImageUrl(currentVideo)"
	      controls
	      autoplay
	      style="width: 100%; border-radius: 8px;"
	    />
	  </el-dialog>
  </div>
</template>

<style scoped>
.collection-detail {
  padding: 20px;
}

.collection-card {
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.cover-container {
  display: flex;
  justify-content: center;
  align-items: center;
}

.cover-image {
  width: 100%;
  max-width: 100%;
  max-height: 200px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
}

.cover-placeholder {
  width: 100%;
  height: 200px;
  background-color: #f2f2f2;
  color: #aaa;
  font-size: 14px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-style: italic;
  border: 1px dashed #ccc;
}

.collection-title {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #333;
}

.collection-description {
  font-size: 16px;
  color: #666;
  line-height: 1.6;
}

.course-card {
  margin-bottom: 30px;
}

.video-cover {
  width: 100%;
  border-radius: 8px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}

.center-title {
  text-align: center;
  margin-top: 10px;
  font-size: 18px;
  color: #333;
}
</style>
