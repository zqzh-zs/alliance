<template>
  <div>
    <!-- 欢迎卡片 -->
    <div class="card" style="line-height: 30px; margin-bottom: 10px">
      <div>欢迎您，<span style="color: #1450aa">{{ user.username }}</span>，祝您今天过得开心！</div>
    </div>

    <!-- 系统简介卡片 -->
    <el-card class="intro-card" shadow="hover">
      <template #header>
        <div class="intro-title">📘 系统简介</div>
      </template>

      <div class="intro-content">
        <p>测盟汇系统是一款基于 <strong>B/S 架构</strong> 的行业会议管理平台，包含 Web 管理端与移动端应用（支持安卓app）。系统服务端和客户端均采用主流开发框架构建，配合主流数据库实现高效的数据持久化。</p>

        <p>本系统旨在为“电子质量管理协会计算机软硬件和信息系统质量测评分会”的会议组织提供信息化支持，有效解决以下痛点：</p>

        <ul>
          <li><strong>注册效率低、易出错</strong>：采用在线注册与信息更新机制，提升工作效率，减少人工失误。</li>
          <li><strong>通知散乱、查阅不便</strong>：整合新闻公告与资料，提供分类、搜索功能，便于快速查阅。</li>
          <li><strong>报告回放缺失</strong>：支持会议直播与精彩报告回放，实现知识沉淀与共享。</li>
        </ul>

        <p>通过“测盟汇”平台，会员单位可轻松完成会议注册、信息发布、会议资料共享等操作，大幅提高会议组织效率，增强参与感与满意度。</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ElCard } from 'element-plus'
const user = JSON.parse(localStorage.getItem('alliance-user') || '{}')
</script>

<style scoped>
.intro-card {
  margin-top: 20px;
  font-size: 15px;
  line-height: 1.8;
  background: #f9fbfe;
  border: 1px solid #e0e6f1;
  border-radius: 12px;
}
.intro-title {
  font-size: 18px;
  font-weight: bold;
  color: #1450aa;
}
.intro-content ul {
  padding-left: 1.2em;
  list-style-type: disc;
}
</style>
