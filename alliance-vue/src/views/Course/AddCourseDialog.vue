<!-- AddCourseDialog.vue -->
<script setup lang="ts">
import { ref, defineExpose, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

// 定义事件
const emit = defineEmits(['added'])

// 控制弹窗显示
const visible = ref(false)
defineExpose({ visible })

// 表单字段
const id = ref('')
const course_name = ref('')
const cover_image = ref('')
const introduction = ref('')
const sort_order = ref('')
const video_url = ref('')
const author = ref('')

const selectedCollectionId = ref('') // 新增：选中的合集ID
const collectionOptions = ref([])    // 新增：合集列表
const courseCollections = ref([])
const selectedCollectionIds = ref<number[]>([])  // 多选合集


// 封面图片上传预览
const coursecoverFile = ref<File | null>(null)
const coursecoverUrl = ref('') // 用于本地预览

// 视频上传预览
const coursevideoFile = ref<File | null>(null)
const coursevideoUrl = ref('')

// 清空表单
function resetForm() {
  id.value = ''
  course_name.value = ''
  cover_image.value = ''
  introduction.value = ''
  sort_order.value = ''
  video_url.value = ''
  author.value = ''
  coursecoverUrl.value = ''
  coursecoverFile.value = null
  coursevideoFile.value = null
  coursevideoUrl.value = ''
  // selectedCollectionId.value = ''
  selectedCollectionIds.value = []
}

// 视频选择与预览
function handleVideoChange(e: Event) {
  const target = e.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    const file = target.files[0]
    coursevideoFile.value = file

    if (coursevideoUrl.value) {
      URL.revokeObjectURL(coursevideoUrl.value)
    }
    coursevideoUrl.value = URL.createObjectURL(file)
	
  } else {
    if (coursevideoUrl.value) {
      URL.revokeObjectURL(coursevideoUrl.value)
    }
    coursevideoFile.value = null
    coursevideoUrl.value = ''
  }
}

// 图片选择与预览
function handleCoverChange(e: Event) {
  const target = e.target as HTMLInputElement
  if (target.files && target.files.length > 0) {
    const file = target.files[0]
    coursecoverFile.value = file
    if (coursecoverUrl.value) {
      URL.revokeObjectURL(coursecoverUrl.value)
    }
    coursecoverUrl.value = URL.createObjectURL(file)
  }
}

// 获取全部合集
async function loadCollections() {
  const res = await axios.get('http://localhost:8080/course-collection/listAll');
  console.log('合集接口返回:', res.data);

  // 这里取两层 data，最里面的是合集数组
  const list = res.data?.data?.data;

  if (Array.isArray(list)) {
    collectionOptions.value = list.map((c: any) => ({
      label: c.collection_name,
      value: c.id
    }));
	console.log("选项为：",collectionOptions.value)
  } else {
    collectionOptions.value = [];
    console.warn('合集数据格式异常，未获取到合集列表');
  }
}


async function handleSave() {
  const formData = new FormData();

  formData.append("id", id.value);
  formData.append("course_name", course_name.value);
  formData.append("introduction", introduction.value);
  formData.append("sort_order", sort_order.value);
  formData.append("author", author.value);

  if (!coursecoverFile.value || !coursevideoFile.value) {
    ElMessage.warning('请上传课程封面和视频');
    return;
  }

  formData.append("coverImage", coursecoverFile.value);
  formData.append("videoFile", coursevideoFile.value);

  // // ✅ 添加合集 ID 到 formData（后端期望的是 collectionIds: List<Long>）
  // if (selectedCollectionIds.value && selectedCollectionIds.value.length > 0) {
  //   selectedCollectionIds.value.forEach(id => {
  //     formData.append("collectionIds", id.toString());
  //   });
  // }
  
  // 将合集 ID 列表追加到 FormData
  selectedCollectionIds.value.forEach(cid => formData.append('collectionIds', cid.toString()))
  
  try {
	  // 1. 提交课程数据
    const res = await axios.post('http://localhost:8080/addcourse', formData);
    console.log('响应数据', res.data);

    if (res.data.code == 200 || res.data.code === '200') {
      ElMessage.success(res.data.msg || '添加成功');
	  
      // 2. 如果选择了合集，则分配课程到合集
      const courseId = res.data.data; // 假设后端返回新课程的ID（建议后端 addcourse 返回 courseId）
	if (selectedCollectionId.value && courseId) {
		console.log('新增课程ID:', courseId);

	  await axios.post(
	    'http://localhost:8080/course-collection-relation/assign',
		          {
		            // collectionId: selectedCollectionId.value,
		            // courseIds: [courseId]
					      collectionId: Number(selectedCollectionId.value), // 👈 显式转换为数字
					      courseIds: [Number(courseId)] // 👈 防止后端因类型不匹配出错
		          }
		
	  );
	  console.log('collectionId:', selectedCollectionId.value, typeof selectedCollectionId.value);
	  console.log('courseId:', courseId, typeof courseId);
	  console.log('课程成功分配到合集');
	}

      resetForm();
      visible.value = false;
      emit('added');
    } else {
      ElMessage.error(res.data.msg || '添加失败');
    }
  } catch (error) {
    console.error('请求失败:', error);
    ElMessage.error('请求出错');
  }
}

onMounted(() => {
  loadCollections()
})
</script>

<template>
  <el-dialog v-model="visible" title="添加课程" width="70%">
    <el-row :gutter="20">
      <!-- 左侧：封面预览 -->
      <el-col :span="10">
        <div class="preview-box2">
          <h4>封面预览</h4>
          <div class="image-preview">
            <img :src="coursecoverUrl || cover_image || 'https://via.placeholder.com/200x150?text=暂无图片'" alt="封面预览" width="250"/>
          </div>
        </div>
		
		
		<div class="preview-box" >
						<h4>视频预览</h4>
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
      <el-col :span="11">
		<h2>课程信息</h2>
        <div class="form-container">
          <el-form label-width="120px" class="addcourse-form">
            <el-form-item label="课程名称：">
              <el-input v-model="course_name" placeholder="请输入课程名称" clearable class="input-width"/>
            </el-form-item>
            <el-form-item label="课程介绍：">
              <el-input v-model="introduction" placeholder="请输入课程介绍" type="textarea" :rows="3" class="input-width"/>
            </el-form-item>
            <el-form-item label="课程作者：">
              <el-input v-model="author" placeholder="请输入课程作者" class="input-width"/>
            </el-form-item>
            <el-form-item label="课程排序：">
              <el-input v-model="sort_order" placeholder="请输入课程排序" class="input-width"/>
            </el-form-item>
            <el-form-item label="课程合集：">
<!--                 <el-select v-model="selectedCollectionId" placeholder="请选择合集">
                    <el-option v-for="item in collectionOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select> -->
				<el-select v-model="selectedCollectionIds" multiple placeholder="请选择合集">
				  <el-option
				    v-for="item in collectionOptions"
				    :key="item.value"
				    :label="item.label"
				    :value="item.value"
				/>
				</el-select>

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
			  <el-button type="primary" @click="handleSave">添加</el-button>
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
  padding: 20px;
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
  /* gap: 8px; */
}
.compact-cover-upload {
  display: flex;
  align-items: center;
  width: 300px;
  /* gap: 8px; */
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
