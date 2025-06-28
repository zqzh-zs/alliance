import {createRouter, createWebHistory} from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'Manager',
      component: () => import('@/views/Manager.vue'),
      redirect: '/home',
      children: [
        { path: 'home', name: 'Home', component: () => import('@/views/manager/Home.vue')},
        { path: 'admin', name: 'Admin', component: () => import('@/views/manager/Admin.vue')},
        { path: 'userInfo', name: 'UserInfo', component: () => import('@/views/manager/UserInfo.vue')},
        { path: 'userList', name: 'UserList', component: () => import('@/views/manager/UserList.vue')},

        {
          path: '/news',
          name: 'News',
          component: () => import('../views/News.vue'),  // 父级容器页（需创建 News.vue）
          redirect: '/news/all',
          children: [
            { path: 'all', name: 'NewsAll', component: () => import('../views/news/NewsAll.vue') },
            { path: 'mine', name: 'NewsMine', component: () => import('../views/news/NewsMine.vue') },
            { path: 'detail/:id', name: 'NewsDetail', component: () => import('../views/news/NewsDetail.vue') }
          ]
        },

		{ path:"/scancourse",name:"scancourse",component:()=>import("../views/Course/ScanCourse.vue")},
		{ path:"/editcourse",name:"editcourse",component:()=>import("../views/Course/EditCourse.vue")},
		{ path: '/collection/:id',name: 'collectionDetail',component: () => import('../components/Course/CourseCertainCollection.vue')}	,
	
		{ path: '/meeting/display',name: 'meetingdisplay',component: () => import('../views/Meeting/MeetingDisplay.vue')},
		{ path: '/meeting/admin/task',name: 'meetingtask_admin',component: () => import('../views/Meeting/MeetingTaskAdmin.vue')},
		{ path: '/meeting/user/task',name: 'meetingtask_user',component: () => import('../views/Meeting/MeetingTaskUser.vue')},

      ]
    },
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
    },
    {
      path: '/register',
      name: 'Register',
      component: () => import('@/views/Register.vue'),
    },
  ]
})

export default router
