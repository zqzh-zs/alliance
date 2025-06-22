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
          :body-style="{ padding: '0px' }"
          @click="goToCollection(item.id)"
        >
          <img :src="getFullImageUrl(item.cover_image)" class="collection-image" />
          <div class="collection-info">
            <span class="collection-name">{{ item.collection_name }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const collections = ref([])
const router = useRouter()

const getFullImageUrl = (path) => {
  return path?.startsWith('http') ? path : `http://localhost:8080${path}`
}

onMounted(async () => {
  const res = await axios.get('http://localhost:8080/course-collection/listAll')
  collections.value = res.data.data.data || []
})

function goToCollection(id) {
  router.push(`/collection/${id}`)
}
</script>

<style scoped>
.collection-list {
  padding: 20px;
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
  border: 2px solid #ddd; /* ✅ 添加边框 */
  border-radius: 12px;
  transition: transform 0.2s ease, box-shadow 0.3s ease;
  cursor: pointer;
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
</style>
