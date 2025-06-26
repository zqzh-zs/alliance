<script setup>
import { reactive, ref } from "vue"
import request from "@/utils/request"
import { ElMessage, ElMessageBox } from "element-plus"
import AddCourseDialog from './AddCourseDialog.vue'
import EditCourseDialog from "../../components/Course/EditCourseDialog.vue"
import AddCourseCollectionDialog from '../../components/Course/AddCourseCollectionDialog.vue'

const addDialogRef = ref()
const editDialogRef = ref()
const addCourseCollectionDialogRef = ref()

const userStorage = JSON.parse(localStorage.getItem('alliance-user') || '{}')

const data = reactive({
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 4,
  formVisible: false,
  form: {},
  user: userStorage,
  userName: userStorage.username || '',
})

const openDialog = () => {
  addDialogRef.value.visible = true
}
const openAddCourseCollectionDialog = () => {
  addCourseCollectionDialogRef.value.openDialog()
}
const handleCourseAdded = () => {
  ElMessage.success('课程添加成功')
  loadCourses()
}
const handlePageChange = (page) => {
  data.pageNum = page
  search()
}

const search = () => {
  request.get('http://localhost:8080/selectAll', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      courseName: data.courseName || undefined,
      author_identity: data.user.role === 1 ? undefined : data.user.role,
      author: data.user.role === 1 ? undefined : data.userName
    }
  }).then(response => {
    const res = response.data
    if (res.code === 200 || res.code === '200') {
      data.tableData = res.data.list || []
      data.total = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '搜索失败')
    }
  }).catch(err => {
    ElMessage.error('请求失败，请稍后重试')
  })
}

const loadCourses = () => {
  request.get('http://localhost:8080/selectAll', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      courseName: data.courseName || undefined,
      author_identity: data.user.role === 1 ? undefined : data.user.role,
      author: data.user.role === 1 ? undefined : data.userName
    }
  }).then(response => {
    const res = response.data
    if (res.code === 200 || res.code === '200') {
      data.tableData = res.data.list || []
      data.total = res.data.total || 0
    } else {
      ElMessage.error(res.msg || '加载课程失败')
    }
  })
}

const reset = () => {
  data.courseName = ''
  data.userName = data.user.role === 1 ? '' : data.user.username
  data.pageNum = 1
  search()
}

const handleEdit = (row) => {
  editDialogRef.value.openDialog(row)
}

const del = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '确认删除', {
    type: 'warning'
  }).then(() => {
    request.delete(`http://localhost:8080/delete/${id}`)
      .then(response => {
        const res = response.data
        if (res.code === '200' || res.code === '201') {
          ElMessage.success('删除成功')
          loadCourses()
        } else {
          ElMessage.error(res.msg || '删除失败')
        }
      }).catch(() => {
        ElMessage.error('删除请求失败')
      })
  })
}

const pass_status_yhl = (id) => {
  ElMessageBox.confirm('确定要通过该课程审核吗？', '审核通过', {
    type: 'warning'
  }).then(() => {
    request.put(`http://localhost:8080/course/pass/${id}`)
      .then(response => {
        const res = response.data
        if (res.code === '200' || res.code === '201') {
          ElMessage.success('课程已通过')
          loadCourses()
        } else {
          ElMessage.error(res.msg || '审核操作失败')
        }
      }).catch(() => {
        ElMessage.error('审核请求失败')
      })
  })
}

const reject_status_yhl = (id) => {
  ElMessageBox.prompt('请输入驳回原因：', '驳回课程', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '驳回原因不能为空'
  }).then(({ value }) => {
    request.put(`http://localhost:8080/course/reject`, null, {
      params: { id, reason: value }
    }).then(response => {
      const res = response.data
      if (res.code === '200' || res.code === '201') {
        ElMessage.success('课程已驳回')
        loadCourses()
      } else {
        ElMessage.error(res.msg || '驳回失败')
      }
    }).catch(() => {
      ElMessage.error('请求失败，请稍后重试')
    })
  })
}

const save = () => {
  request.request({
    method: data.form.id ? 'PUT' : 'POST',
    url: data.form.id ? '/orders/update' : '/orders/add',
    data: data.form
  }).then(res => {
    if (res.code === '200') {
      ElMessage.success('保存成功')
      data.formVisible = false
      loadCourses()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const loadCollections = () => {
  // 预留：合集加载逻辑
}

loadCourses()
</script>

<template>
  <div>
    <AddCourseCollectionDialog ref="addCourseCollectionDialogRef" @saved="loadCollections" />
    <AddCourseDialog ref="addDialogRef" @added="handleCourseAdded" />
    <EditCourseDialog ref="editDialogRef" @saved="loadCourses" />

    <div class="card" style="margin-bottom: 10px;">
      <el-input
        prefix-icon="Search"
        v-model="data.courseName"
        placeholder="请输入课程名称"
        style="width: 200px; margin-right: 10px"
      />
      <el-input
        v-if="data.user.role === 1"
        v-model="data.userName"
        placeholder="请输入作者名称"
        style="width: 200px; margin-right: 10px"
      />
      <el-button type="primary" @click="search">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
      <el-button type="primary" @click="openDialog">添加课程</el-button>
      <el-button type="primary" @click="openAddCourseCollectionDialog">添加合集</el-button>
    </div>

    <div class="coursetable_div">
      <el-table :data="data.tableData">
        <el-table-column prop="course_name" label="课程名称" />
        <el-table-column prop="cover_image" label="课程封面" width="175">
          <template #default="scope">
            <el-image
              v-if="scope.row.cover_image"
              style="width: 150px; height: 100px; cursor: pointer"
              :src="'http://localhost:8080' + scope.row.cover_image"
              fit="cover"
              :preview-src-list="['http://localhost:8080' + scope.row.cover_image]"
              :preview-teleported="true"
            />
            <span v-else>暂无封面</span>
          </template>
        </el-table-column>
        <el-table-column prop="introduction" label="课程介绍" width="170" />
        <el-table-column prop="video_url" label="课程视频" width="145">
          <template #default="scope">
            <video
              v-if="scope.row.video_url"
              :src="'http://localhost:8080' + scope.row.video_url"
              controls
              width="120"
              height="100"
              style="border-radius: 6px; background-color: #000"
            />
            <span v-else>暂无视频</span>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="课程作者" width="80" />
        <el-table-column prop="status" label="状态" width="70">
          <template #default="{ row }">
            <span>
              {{
                row.status === 0
                  ? '待审核'
                  : row.status === 1
                  ? '已通过'
                  : row.status === 2
                  ? '未通过'
                  : '未知状态'
              }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="create_time" label="上传时间" width="97" />
        <el-table-column prop="update_time" label="更新时间" width="97" />
        <el-table-column prop="reject_reason" label="驳回原因" />
        <el-table-column fixed="right" label="操作" width="131">
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="del(scope.row.id)">删除</el-button>
            <br />
            <el-button
              v-if="data.user.role === 1 && scope.row.status !== 1"
              type="success"
              size="small"
              @click="pass_status_yhl(scope.row.id)"
              >通过</el-button
            >
            <el-button
              v-if="data.user.role === 1 && scope.row.status === 0"
              type="primary"
              size="small"
              @click="reject_status_yhl(scope.row.id)"
              >驳回</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="card" v-if="data.total">
      <div style="margin-bottom: 10px; color: #666; font-size: 14px">
        当前筛选条件：
        <template v-if="data.userName">名称包含 “{{ data.userName }}”</template>
        <template v-else>无</template>
      </div>

      <el-pagination
        background
        layout="prev, pager, next"
        @current-change="handlePageChange"
        :page-size="data.pageSize"
        v-model:current-page="data.pageNum"
        :total="data.total"
      />
    </div>

    <el-dialog v-model="data.formVisible" title="编辑课程信息" width="40%" destroy-on-close>
      <el-form :model="data.form" label-width="100px" style="padding-right: 50px">
        <el-form-item label="课程介绍"> </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>



<style scoped>
.coursetable_div {
  margin-bottom: 10px;
  height: 525px;
}
</style>
