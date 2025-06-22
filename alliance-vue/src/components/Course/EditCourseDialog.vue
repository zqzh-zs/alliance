<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

// 表单数据
const visible = ref(false)
// const courseData = ref({})
const courseData = ref({
  id: null,
  course_name: '',
  introduction: '',
  author: '',
  cover_image: '',
  video_url: ''
})
const originalData = ref({}) // 实现深拷贝恢复原始数据，需要再添加一个变量来保存原始数据
const cover_image = ref('')
// 文件上传相关
const coursecoverUrl = ref('')
const coursevideoUrl = ref('')
const coursecoverFile = ref(null)
const coursevideoFile = ref(null)

// 编辑完成后通知父组件刷新数据
const emit = defineEmits(['saved'])


// 打开对话框的方法，接收 row 数据
const openDialog = (row) => {
	
	  if (!row || !row.id) {
	    ElMessage.warning('无效的数据行')
	    return
	  }

  originalData.value = JSON.parse(JSON.stringify(row)) // 保存原始数据用于重置
  courseData.value = JSON.parse(JSON.stringify(row))   // 深拷贝避免影响原数据
  // 设置封面和视频预览
  if (courseData.value.cover_image) {
    coursecoverUrl.value = 'http://localhost:8080' + courseData.value.cover_image
  }
  if (courseData.value.video_url) {
    coursevideoUrl.value = 'http://localhost:8080' + courseData.value.video_url
  }

  visible.value = true
}

// 处理封面文件选择
const handleCoverChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    coursecoverFile.value = file
    coursecoverUrl.value = URL.createObjectURL(file)
  }
}

// 处理视频文件选择
const handleVideoChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    coursevideoFile.value = file
    coursevideoUrl.value = URL.createObjectURL(file)
  }
}

// 提交修改
const handleSave = () => {
  const formData = new FormData()

  // 原始字段
  formData.append('id', courseData.value.id)
  formData.append('course_name', courseData.value.course_name || '')
  formData.append('introduction', courseData.value.introduction || '')
  formData.append('author', courseData.value.author || '')

  // 新增文件字段
  if (coursecoverFile.value) {
    formData.append('cover_image', coursecoverFile.value)
  }
  if (coursevideoFile.value) {
    formData.append('video_url', coursevideoFile.value)
  }

  // 发送请求更新课程信息
  request.put('http://localhost:8080/updateCourse', formData, {
    // headers: {
    //   'Content-Type': 'multipart/form-data'
    // }
	
  }).then(response => {
	  const res = response.data
    if (res.code === '200') {
      ElMessage.success('修改成功')
      visible.value = false
      emit('saved') // 触发 saved 事件，通知父组件刷新数据
    } else {
      ElMessage.error(res.msg || '修改失败')
    }
  }).catch(error => {
    console.error('修改请求失败:', error)
    if (error.response) {
      // 服务器响应了错误状态码
      ElMessage.error(`服务器错误: ${error.response.status} ${error.response.data}`)
    } else if (error.request) {
      // 请求已发送但无响应
      ElMessage.error('后端服务无响应，请检查是否运行')
    } else {
      // 请求配置错误
      ElMessage.error(`请求配置错误: ${error.message}`)
    }
  })
}

// 定义对外暴露的方法
defineExpose({
  openDialog
})

// 重置表单
const resetForm = () => {
  courseData.value = JSON.parse(JSON.stringify(originalData.value)) // 恢复原始数据
  coursecoverFile.value = null
  coursevideoFile.value = null
}
</script>


<template>
	
	<el-dialog v-model="visible" title="编辑课程信息" width="50%" destory-on-close>
	  <el-row :gutter="20">
		  <el-col :span="1"/>
	    <!-- 左侧：封面预览 -->
	    <el-col :span="10">
	      <div class="preview-box2">
	        <h4>目前封面</h4>
	        <div class="image-preview">
	          <img :src="coursecoverUrl || cover_image || 'https://via.placeholder.com/200x150?text=暂无图片'" alt="封面预览" width="250"/>
	        </div>
	      </div>
			
			
			<div class="preview-box" >
							<h4>目前视频</h4>
							<div style="margin-top: 16px;">
							  <video
								v-if="coursevideoUrl"
								controls
								width="250"
								:src="coursevideoUrl"
								v-show="coursevideoUrl"
								style="border: 1px solid #ccc; border-radius: 6px"
							  ></video>
							  <p v-else>请选择一个本地视频进行预览</p>
							</div>
			</div>
	    </el-col>
	
	    <!-- 中间：表单内容 -->
	    <el-col :span="13">
			<h2>修改课程信息</h2>
	      <div class="form-container">
	        <el-form label-width="120px" class="editcourse-form">
	          <el-form-item label="课程名称：">
	            <el-input v-model="courseData.course_name" placeholder="请输入课程名称" clearable class="input-width"/>
	          </el-form-item>
	          <el-form-item label="课程介绍：">
	            <el-input v-model="courseData.introduction" placeholder="请输入课程介绍" type="textarea" :rows="3" class="input-width"/>
	          </el-form-item>
	          <el-form-item label="课程作者：">
	            <el-input v-model="courseData.author" placeholder="请输入课程作者" class="input-width"/>
	          </el-form-item>

				<el-form-item label="课程视频：">
					  <div class="compact-video-upload">
							<!-- 文件名显示区域 -->
							<span class="file-name">
							  {{ coursevideoFile ? coursevideoFile.name : '未选择任何文件' }}
							</span>
	
							<!-- 上传按钮 -->
							<el-button type="primary" @click="$refs.videoInput.click()">选择视频</el-button>
							<input
							  ref="videoInput"
							  type="file"
							  accept="video/*"
							  @change="handleVideoChange"
							  style="display: none;"
							/>
					  </div>
				</el-form-item>
	
				<el-form-item label="课程封面：">
					  <div class="compact-cover-upload">
							<!-- 文件名显示区域 -->
							<span class="file-name">
							  {{ coursecoverFile ? coursecoverFile.name : '未选择任何文件' }}
							</span>
	
							<!-- 上传按钮 -->
							<el-button type="primary" @click="$refs.coverInput.click()">选择图片</el-button>
							<input
							  ref="coverInput"
							  type="file"
							  accept="image/*"
							  @change="handleCoverChange"
							  style="display: none;"
							/>
					  </div>
				</el-form-item>
				
				<div class="form-footer-center">
				  <el-button type="primary" @click="handleSave">修改</el-button>
				  <el-button @click="resetForm">重置</el-button>
				</div>
	        </el-form>
	      </div>
	    </el-col>
	
	
	  </el-row>
	
	</el-dialog>
</template>



<style scoped>
.form-container {
  padding: 10px;
}
.input-width {
  width: 300px;
}
.preview-box, .preview-box2 {
  background-color: #f9f9f9;
  padding: 3px;
  text-align: center;
  border-radius: 8px;
}
.image-preview img {
  max-width: 100%;
  height: auto;
  border-radius: 6px;
}
.compact-video-upload {
  display: flex;
  align-items: center;
  width: 300px;
}
.compact-cover-upload {
  display: flex;
  align-items: center;
  width: 300px;
}
.file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 10px;
  font-size: 14px;
  color: #606266;
}
.form-footer-center {
  display: flex;
  justify-content: center; /* 水平居中 */
  margin-top: 20px; /* 增加顶部间距 */
}
h2{
	display: flex;
	justify-content: center; /* 水平居中 */
	margin-top: 20px; /* 增加顶部间距 */
}
</style>
