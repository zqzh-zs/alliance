<template>
  <div>
    <div style="height: 60px; background-color: #fff; display: flex; align-items: center; border-bottom: 1px solid #ddd">
      <div style="flex: 1">
        <div style="padding-left: 20px; display: flex; align-items: center">
          <img src="@/assets/imgs/img.png" alt="" style="width: 40px">
          <div style="font-weight: bold; font-size: 24px; margin-left: 5px; color: #F9B44C">测盟会系统</div>
        </div>
      </div>
      <div style="width: fit-content; padding-right: 10px; display: flex; align-items: center;">
        <img :src="data.user.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" alt="" style="width: 40px; height: 40px;border-radius: 50%">
        <span style="margin-left: 5px">{{data.user.username}}</span>
      </div>
    </div>

    <div style="display: flex">
      <div style="width: 200px; border-right: 1px solid #ddd; min-height: calc(100vh - 60px)">
        <el-menu
            router
            style="border: none"
            :default-active="$route.path"
            :default-openeds="['/home', '2']"
        >
          <el-menu-item index="/home">
            <el-icon><HomeFilled /></el-icon>
            <span>系统首页</span>
          </el-menu-item>
<<<<<<< HEAD
          <el-sub-menu index="3">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>行业动态</span>
            </template>
            <el-menu-item index="/news/all">
              <el-icon><View /></el-icon>
              <span>全部动态</span>
            </el-menu-item>
            <el-menu-item index="/news/mine">
              <el-icon><Edit /></el-icon>
              <span>我的动态</span>
            </el-menu-item>
          </el-sub-menu>
=======
		  <el-sub-menu index="2">
		    <template #title>
		  		<span class="iconfont"> &#xe614; &nbsp;</span>
		        <span>课程管理</span>
		    </template>
		    <el-menu-item index="/scancourse">
		  		<span class="iconfont"> &#xe61d; </span>
		  		<span>课程浏览</span>
		    </el-menu-item>
		  	<el-menu-item index="/editcourse">
		  		<el-icon><Edit /></el-icon>
		  		<span>课程编辑</span>
		  	</el-menu-item>
		  </el-sub-menu>
>>>>>>> main
          <el-sub-menu index="2" v-if="data.user.role === 1">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/userList">
              <el-icon><UserFilled /></el-icon>
              <span>用户信息</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item index="/userInfo">
            <el-icon><User /></el-icon>
            <span>个人资料</span>
          </el-menu-item>
          <el-menu-item index="logout" @click="logout">
            <el-icon><SwitchButton /></el-icon>
            <span>退出系统</span>
          </el-menu-item>
        </el-menu>
      </div>

      <div style="flex: 1; width: 0; background-color: #f8f8ff; padding: 10px">
        <router-view @updateUser="updateUser"/>
      </div>
    </div>

  </div>
</template>

<script setup>
import { useRoute } from 'vue-router'
import {User, UserFilled} from "@element-plus/icons-vue";
import { reactive } from 'vue'
const $route = useRoute()
console.log($route.path)

const data = reactive({
  user : JSON.parse(localStorage.getItem('alliance-user') || '{}')
})


import { useRouter } from 'vue-router'
const router = useRouter()

const logout = () => {
  localStorage.removeItem('alliance-user')
  router.push('/login') // ✅ 使用绝对路径跳转到 /login
}

const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem('alliance-user') || '{}')

}

</script>

<style scoped>
.el-menu-item.is-active {
  background-color: #e0e4ff !important;
}
.el-menu-item:hover {
  background-color: #e9eeff !important;
  color: #1450aa;
}
.deep th {
  color: #333;
}

</style>