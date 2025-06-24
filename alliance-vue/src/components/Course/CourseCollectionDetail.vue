<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import request from '@/utils/request'
import { useRouter } from 'vue-router'
import { reactive } from 'vue'


const collections = ref([])
const router = useRouter()
const user = JSON.parse(localStorage.getItem('alliance-user') || '{}')
// 编辑功能
const editDialogVisible = ref(false)
const editForm = reactive({
  id: null,
  collection_name: '',
  description: ''
})


const getFullImageUrl = (path) => {
  return path?.startsWith('http') ? path : `http://localhost:8080${path}`
}

async function deleteCollection(id) {
  try {
    await request.delete(`http://localhost:8080/course-collection/${id}`)
    ElMessage.success('删除成功')
    await fetchCollections()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

async function submitEdit() {
  try {
    await request.put('http://localhost:8080/course-collection', editForm)
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    await fetchCollections()
  } catch (e) {
    console.error('修改失败:', e)
    ElMessage.error('修改失败')
  }
}

function handleMenuCommand(command, item) {
  if (command === 'edit') {
    Object.assign(editForm, item)
    editDialogVisible.value = true
  } else if (command === 'delete') {
    ElMessageBox.confirm('确认删除该合集及其课程关联关系吗？', '警告', {
      type: 'warning'
    })
      .then(() => deleteCollection(item.id))
      .catch(() => {})
  }
}

function goToCollection(id) {
  router.push(`/collection/${id}`)
}

async function fetchCollections() {
  try {
    const res = await request.get('/course-collection/listAll')
    collections.value = res.data.data || []
  } catch (error) {
    console.error('获取合集失败:', error)
    ElMessage.error('获取合集失败')
  }
}


onMounted(() => {
  fetchCollections()
})

</script>



<template>
  <div class="collection-list">
    <h2 class="page-title">课程合集</h2>

    <el-row :gutter="20">
      <el-col
        v-for="item in collections"
        :key="item.id"
        :span="6"
        class="collection-col"
      >
        <el-card
          shadow="hover"
          class="collection-card"
          :body-style="{ padding: '0px', position: 'relative' }"
          @click="goToCollection(item.id)"
        >
          <img :src="getFullImageUrl(item.cover_image)" class="collection-image" />

          <!-- 操作菜单（仅 ADMIN） -->
          <div
            v-if="user.role === 1"
            class="options-menu"
            @click.stop
          >
            <!-- <el-dropdown trigger="click" @command="handleMenuCommand(item)"> -->
			<el-dropdown trigger="click" @command="(command) => handleMenuCommand(command, item)">

              <span class="el-dropdown-link">
				<el-icon><MoreFilled /></el-icon>

              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="edit">编辑</el-dropdown-item>
                  <el-dropdown-item command="delete">删除</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>

          <div class="collection-info">
            <span class="collection-name">{{ item.collection_name }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 编辑 Dialog -->
    <el-dialog v-model="editDialogVisible" title="编辑合集" width="400px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="合集名称">
          <el-input v-model="editForm.collection_name" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="editForm.description" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>




<style scoped>
.collection-list {
  padding: 20px;
  /* background: #f9f9f9; */
  min-height: 100vh;
}

.page-title {
  font-size: 28px;
  text-align: center;
  margin-bottom: 30px;
  font-weight: 600;
  color: #333;
}

.collection-col {
  margin-bottom: 20px;
}

.collection-card {
  border: 2px solid #ddd;
  border-radius: 12px;
  transition: transform 0.2s ease, box-shadow 0.3s ease;
  cursor: pointer;
  position: relative;
}

.collection-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.collection-image {
  width: 100%;
  height: 180px;
  object-fit: cover;
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
  transition: transform 0.3s ease;
}

.collection-card:hover .collection-image {
  transform: scale(1.02);
}

.collection-info {
  padding: 14px;
  text-align: center;
}

.collection-name {
  font-size: 16px;
  font-weight: 500;
  color: #444;
  display: inline-block;
}

/* 三个点菜单样式 */
.options-menu {
  position: absolute;
  bottom: 10px;
  right: 10px;
  z-index: 1;
}
</style>
