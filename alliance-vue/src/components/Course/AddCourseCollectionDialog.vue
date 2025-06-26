<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const visible = ref(false)
const emit = defineEmits(['saved'])

const collectionData = ref({
  collection_name: '',
  description: '',
  cover_image: ''
})

const coverFile = ref(null)
const coverPreview = ref('')

// 打开对话框
const openDialog = () => {
  collectionData.value = {
    collection_name: '',
    description: '',
    cover_image: ''
  }
  coverFile.value = null
  coverPreview.value = ''
  visible.value = true
}

// 上传图片预览
const handleCoverChange = (event) => {
  const file = event.target.files[0]
  if (file) {
    coverFile.value = file
    coverPreview.value = URL.createObjectURL(file)
  }
}

// 提交合集信息
const handleSave = () => {
  if (!collectionData.value.collection_name) {
    ElMessage.warning('请输入合集名称')
    return
  }

  const formData = new FormData()
  formData.append('collection_name', collectionData.value.collection_name)
  formData.append('description', collectionData.value.description || '')
  if (coverFile.value) {
    formData.append('cover_image', coverFile.value)
  }

  request.post('http://localhost:8080/course-collection/add', formData)
    .then(res => {
      const result = res.data
      if (result.code === '200') {
        ElMessage.success('添加成功')
        visible.value = false
        emit('saved') // 通知父组件刷新
      } else {
        ElMessage.error(result.msg || '添加失败')
      }
    })
    .catch(err => {
      console.error('添加失败:', err)
      ElMessage.error('请求失败，请检查服务器连接')
    })
}

defineExpose({
  openDialog
})
</script>

<template>
  <el-dialog v-model="visible" title="添加课程合集" width="40%">
    <div style="max-width: 450px; margin: 0 auto;">
      <el-form label-width="50px" label-position="top">
        <!-- 封面预览 -->
        <el-row :gutter="18">
          <el-col :span="8">
            <el-form-item label="预览封面：">
              <img
                v-if="coverPreview"
                :src="coverPreview"
                alt="封面预览"
                style="width: 100%; border-radius: 6px; border: 1px solid #ccc;"
              />
              <p v-else>未选择封面图片</p>
            </el-form-item>
          </el-col>

          <el-col :span="16">
            <!-- 合集名称 -->
            <el-form-item label="合集名称：">
              <el-input v-model="collectionData.collection_name" placeholder="请输入合集名称" clearable />
            </el-form-item>

            <!-- 合集简介 -->
            <el-form-item label="合集简介：">
              <el-input
                v-model="collectionData.description"
                placeholder="请输入合集介绍"
                type="textarea"
                :rows="3"
              />
            </el-form-item>

            <!-- 合集封面上传 -->
            <el-form-item label="合集封面：">
              <div class="compact-upload">
                <span class="file-name">
                  {{ coverFile ? coverFile.name : '未选择任何文件' }}
                </span>
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
          </el-col>
        </el-row>

        <!-- 按钮区域，右对齐 -->
        <el-row>
          <el-col :span="24" style="text-align: right;">
            <el-button type="primary" @click="handleSave">保存</el-button>
            <el-button @click="visible = false">取消</el-button>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </el-dialog>
</template>

<style scoped>
.compact-upload {
  display: flex;
  align-items: center;
  width: 100%;
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

.el-form-item__content .el-input,
.el-form-item__content .el-textarea,
.compact-upload {
  width: 100%;
}
</style>
