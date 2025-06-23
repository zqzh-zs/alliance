<template>
  <div class="page-container">
    <CourseCollectionDetail />
    <router-view></router-view>
  </div>
</template>

<script setup lang="ts">
import CourseCollectionDetail from '../../components/Course/CourseCollectionDetail.vue'
</script>

<style scoped>
.page-container {
  background-color: #ffffff; /* 设置页面背景为白色 */
  min-height: 100vh;         /* 保证页面最少占满一屏高度 */
  padding: 20px;             /* 可选：加点内边距避免贴边 */
}
</style>
