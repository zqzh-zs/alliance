<template>
  <el-dialog v-model="visible" title="分配课程到合集">
    <el-select v-model="selectedCollectionId" placeholder="请选择合集" style="width: 100%">
      <el-option
        v-for="item in collections"
        :key="item.id"
        :label="item.collection_name"
        :value="item.id"
      />
    </el-select>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="assign">确认分配</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, defineExpose } from 'vue'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const visible = ref(false)
const selectedCollectionId = ref(null)
const courseId = ref(null)
const collections = ref([])

const openDialog = async (course) => {
  courseId.value = course.id
  selectedCollectionId.value = null
  visible.value = true
  const res = await request.get('/course-collection/list') // 需要后端提供合集列表接口
  if (res.code === '200') {
    collections.value = res.data
  } else {
    ElMessage.error('加载合集失败')
  }
}

const assign = () => {
  if (!selectedCollectionId.value) {
    ElMessage.warning('请选择一个合集')
    return
  }
  request.post('/course-collection/assign', null, {
    params: {
      courseId: courseId.value,
      collectionId: selectedCollectionId.value
    }
  }).then(res => {
    if (res.code === '200') {
      ElMessage.success('分配成功')
      visible.value = false
    } else {
      ElMessage.error(res.msg || '分配失败')
    }
  })
}

defineExpose({ openDialog })
</script>
