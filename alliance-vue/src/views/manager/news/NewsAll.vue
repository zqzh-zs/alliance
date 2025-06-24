<template>
  <div>
    <el-card>
      <!-- 查询区域 -->
      <div
          style="
          margin-bottom: 10px;
          display: flex;
          gap: 10px;
          flex-wrap: wrap;
          align-items: center;
        "
      >
        <el-input v-model="query.title" placeholder="标题关键词" clearable style="width: 150px" />
        <el-input v-model="query.author" placeholder="作者关键词" clearable style="width: 150px" />
        <el-input v-model="query.summary" placeholder="摘要关键词" clearable style="width: 200px" />

        <!-- 管理员才可见状态选择 -->
        <el-select v-if="user.role === 1" v-model="query.status" placeholder="选择状态" clearable style="width: 150px">
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已驳回" :value="2" />
        </el-select>

        <el-date-picker
            v-model="queryTime"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 200px"
        />

        <el-button type="primary" @click="load">查询</el-button>
        <el-button @click="reset">重置</el-button>
      </div>

      <!-- 动态列表 -->
      <el-table :data="list" style="width: 100%">
        <el-table-column label="封面" width="100">
          <template #default="scope">
            <img
                :src="scope.row.cover || defaultCover"
                style="width: 80px; height: 60px; object-fit: cover; display: block"
            />
          </template>
        </el-table-column>

        <el-table-column prop="title" label="标题">
          <template #default="scope">
            <el-link type="primary" :underline="false" @click="$router.push(`/news/detail/${scope.row.id}`)">
              {{ scope.row.title }}
            </el-link>
          </template>
        </el-table-column>

        <el-table-column prop="author" label="作者" />
        <el-table-column prop="summary" label="摘要" />
        <el-table-column label="发布时间">
          <template #default="scope">
            {{ formatDateTime(scope.row.createTime) }}
          </template>
        </el-table-column>

        <el-table-column v-if="user.role === 1" prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getTagType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>

        <el-table-column v-if="user.role === 1" label="操作" width="200">
          <template #default="scope">
            <el-button v-if="scope.row.status === 0" size="small" type="success" @click="changeStatus(scope.row.id, 1)">通过</el-button>
            <el-button v-if="scope.row.status === 0" size="small" type="danger" @click="rejectNews(scope.row.id)">驳回</el-button>
            <el-button
                size="small"
                type="primary"
                @click="setTop(scope.row.id, scope.row.isTop === 1 ? 0 : 1)"
            >
              {{ scope.row.isTop === 1 ? '取消' : '置顶' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
          background
          layout="prev, pager, next"
          :current-page="query.pageNum"
          :page-size="query.pageSize"
          :total="total"
          @current-change="handlePageChange"
          style="margin-top: 10px"
      />
    </el-card>

    <!-- 驳回弹窗 -->
    <el-dialog v-model="rejectVisible" title="请输入驳回原因" width="30%">
      <el-input v-model="rejectReason" type="textarea" placeholder="请填写原因" />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确定驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from "vue";
import { ElMessage } from "element-plus";
import request from "@/utils/request";

const user = reactive(JSON.parse(localStorage.getItem("alliance-user") || "{}"));

const setTop = (id, isTop) => {
  request.post("/news/top", null, {
    params: { id, isTop }
  }).then(() => {
    ElMessage.success("操作成功")
    load()
  })
}

const query = reactive({
  title: "",
  author: "",
  summary: "",
  status: null,
  startTime: "",
  endTime: "",
  pageNum: 1,
  pageSize: 10
});

const queryTime = ref([]);
watch(queryTime, (val) => {
  if (val && val.length === 2) {
    query.startTime = val[0];
    query.endTime = val[1];
  } else {
    query.startTime = "";
    query.endTime = "";
  }
});

const list = ref([]);
const total = ref(0);
const defaultCover = "/default.jpg";

const load = () => {
  const params = {
    ...query,
    orderBy: 'is_top,create_time',
    orderDir: 'desc'
  }
  if (user.role === 2) {
    params.status = 1;
  }

  request
      .get("/news/list", {
        params,
        headers: {
          Authorization: "Bearer " + user.token
        }
      })
      .then((res) => {
        const newsList = res.data?.list || [];
        list.value = newsList.map((item) => {
          item.cover = item.attachments?.[0]?.fileUrl || item.newsImage || "";
          return item;
        });
        total.value = res.data?.total || 0;
      })
      .catch((err) => {
        console.error("加载失败", err);
        list.value = [];
        total.value = 0;
      });
};

const handlePageChange = (val) => {
  query.pageNum = val;
  load();
};

const formatDateTime = (value) => {
  if (!value) return '';
  const date = new Date(value);
  const pad = (n) => String(n).padStart(2, '0');
  const y = date.getFullYear();
  const m = pad(date.getMonth() + 1);
  const d = pad(date.getDate());
  const h = pad(date.getHours());
  const min = pad(date.getMinutes());
  return `${y}-${m}-${d}`;
};

const reset = () => {
  query.title = "";
  query.author = "";
  query.summary = "";
  query.status = null;
  query.startTime = "";
  query.endTime = "";
  queryTime.value = [];
  query.pageNum = 1;
  load();
};

onMounted(load);

const getStatusLabel = (status) => {
  return status === 0 ? "待审核" : status === 1 ? "已通过" : "已驳回";
};

const getTagType = (status) => {
  return status === 0 ? "warning" : status === 1 ? "success" : "danger";
};

const changeStatus = (id, status) => {
  request.post("/news/audit", { id, status }).then(() => {
    ElMessage.success("操作成功");
    load();
  });
};

const rejectVisible = ref(false);
const rejectReason = ref("");
const rejectId = ref(null);

const rejectNews = (id) => {
  rejectId.value = id;
  rejectVisible.value = true;
};

const confirmReject = () => {
  request.post("/news/audit", {
    id: rejectId.value,
    status: 2,
    reason: rejectReason.value
  }).then(() => {
    ElMessage.success("已驳回");
    rejectVisible.value = false;
    rejectReason.value = "";
    load();
  });
};
</script>

<style scoped>
img {
  border-radius: 4px;
}
</style>
