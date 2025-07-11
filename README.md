# 🌐 alliance测盟汇系统

## 🧾 一、项目介绍

我国“电子质量管理协会计算机软硬件和信息系统质量测评分会”每年举办多场会议，旨在促进会员单位之间的信息交流、决策计划、组织事务处理、吸引新成员以及社交网络建立。然而，在组织大会过程中，会议组织者面临以下挑战：

### 1）会议注册工作效率低且容易出错

当前基于电子邮件的会议注册方式工作量大、及时性差，参会者信息更新困难，且容易在转换过程中操作出错。

**解决方案：**  开发一个在线会议注册系统，参会者可以直接在系统中填写注册信息，并随时更新行程。系统自动记录和更新参会者信息，减少人工操作出错的可能性，提高工作效率。



### 2）会议通知和新闻发布散乱，不易查阅

目前基于微信群的会议通知和新闻发布方式不够有序，重要信息容易被其他回复和点赞淹没，会员单位成员难以查阅历届会议的相关信息。

**解决方案：**  建立一个 Web 端行业动态管理子系统专用的会议管理平台，整合通知、新闻发布、资料等功能。平台应具备分类、搜索等功能，便于成员查阅和回顾新闻信息。



### 3）优秀会议报告无法在线回放

一些优秀的会议报告只能在现场收看，无法在线回放，影响了知识的传播和共享。

**解决方案：**  在会议管理平台中增加在线回放功能，对经授权的优秀会议报告进行录制和上传，供会员单位成员随时查看和学习。此外，还可以考虑增加会议直播功能，以便无法到场参会的会员单位成员实时收看会议报告。


通过以上解决方案，可以有效解决会议组织过程中遇到的痛点问题，提高会议组织效率，增强会员单位成员的参与度和满意度。

本系统的设计是基于 **B/S 架构** 下实现的 Web 项目及移动端项目。用户可以通过 PC 端的主流浏览器使用本系统的管理端，通过移动端程序（安卓 APP）搜索 **测盟汇** 访问手机端。本系统采用主流的开发框架构建系统服务端以及客户端，采用主流数据库实现数据持久化。

## 🧰 二、项目技术
- **后端框架**：Spring Boot 3.3.4
- **前端技术**：Vue 3 + Element Plus
- **数据库**：MySQL 8.4
- **ORM框架**：MyBatis Plus
- **文件存储**：本地磁盘（支持扩展为 OSS 等对象存储）

## 🚀 三、项目功能
1. Web 端登录和用户管理子系统（已上线）
- **登录：** 企业下的用户、超级管理员可以通过账号和密码登录到系统。
- **注册：** 企业可以填写企业名称和企业联系方式、账号、密码、验证码等信息进行系统注册。
- **个人信息：** 可以对用户的基本资料，用户昵称，手机号码，邮箱，性别等进行修改；输入旧密码，新密码和确认密码进行密码修改；可以查看用户的基本信息，创建日期等信息
- **用户管理：** 超级管理员可以查看所有用户列表，列表支持通过用户名称，手机号码，状态等进行模糊查询；超级管理员可以创建和修改用户信息，包括用户名称，用户状态，用
  户信息等。超级管理员根据用户的权限，控制企业用户对系统的访问和操作。
2. Web 端行业动态管理子系统（已上线）

- **发布动态：** 企业用户和超级管理员可发布新的行业动态，填写标题、封面图片、摘要、内容、作者、附件等信息。发布后可进行封面图和附件上传。
- **浏览动态：** 用户可以分页浏览已发布的行业动态列表，支持按标题、作者、摘要、状态（待审核/已通过/已驳回）进行模糊查询和组合筛选。
- **动态详情：** 点击动态标题可进入详情页，查看完整内容，包括发布时间、作者、附件等信息。
- **编辑动态：** 支持对已发布动态进行修改，包括标题、内容、摘要、封面、附件等信息。编辑后的动态重新进入待审核状态。
- **删除动态：** 企业用户可删除自己发布的动态，超级管理员可以删除所有不需要的动态。删除操作前提供确认弹窗以避免误删。
- **审核功能：** 企业用户发布的动态需由超级管理员审核，审核操作包括“通过”或“驳回”，驳回时需填写驳回原因。审核后动态状态将变更为“已通过”或“已驳回”。
- **权限分配：**
  - 超级管理员：可查看和管理所有企业用户发布的动态，具备发布、编辑、删除、审核等权限。
  - 企业用户：仅可查看和管理自己发布的动态，包括发布、编辑、删除等操作，无法审核他人动态。

3. Web 端课程管理子系统（已上线）

- **课程浏览：** 可查看各个课程合集，并能查看各个课程合集的介绍信息、合集中的各个课程视频信息并播放课程视频
- **课程编辑：** 可对课程进行增删改查,可以上传课程视频、课程封面等课程信息，可进行删除、修改课程信息。如果是管理员，还可以“通过”或“驳回”课程申请。
4. Web 端会议管理子系统（已上线）
* **会议创建与管理：**支持创建新会议，根据用户角色自动设置审核状态（管理员直接通过，普通用户需审核）；提供会议更新功能，可修改会议各项信息；支持会议删除操作
* **会议审核流程：**管理员可批准或驳回待审核会议；会议状态自动更新（待审核/已通过/已驳回）
* **会议查询功能：**获取单个会议完整详情（包含自动补全图片URL功能）；查询已审核通过的会议列表（支持分页）；支持多种筛选条件：会议类型、关键词、组织者、日期范围等
* **搜索功能：**通用会议搜索（支持分页和多种筛选条件）；用户个人会议搜索（按组织者筛选）；所有搜索接口均返回分页数据（列表+总数）

5. 移动端会议注册子系统（已上线）

- **列表展示：** 按会议研讨、标准定制、技术培训、工具研发、公益行动等分类展示信息，包含名称、模块简介、时间、地点等基础信息，支持分类筛选和快速浏览，上拉加载更多功能。
- **会议详情查看：** 点击会议进入详情页，展示完整信息：名称、详细描述、时间、举办地点、主办单位、议程、嘉宾介绍等。
- **填写参会回执：** 提供表单填写参会信息，包括单位名称、姓名、性别、手机号码、电子邮箱、到达方式、到达车次、到达时间等字段，支持表单验证和提交。
- **用户行为分析：** 统计热门会议访问量、回执提交率等数据，并将收集的数据提交到web端，仅管理员可以查看。
6. 移动端行业动态子系统（已上线）
- **行业动态列表展示与搜索：** 支持按动态名称、动态简介等字段进行模糊搜索查询，集成下拉刷新功能，实时获取最新行业动态数据，实现上拉加载更多机制，支持海量数据的无限滚动浏览，列表展示包含动态标题、简介、发布时间、缩略图等核心信息
- **动态详情展示：** 完整展示动态名称、详细简介、富文本内容、发布时间等信息，支持图片资源的适配展示和富文本格式转换渲染，提供内容分享、收藏等互动功能入口，优化长文本阅读体验，支持图文混排内容的流畅展示
- **用户行为分析：** 统计热门动态访问量、内容分享率、用户点赞量等指标，并将收集的数据提交到web端，仅管理员可以查看。
## 🛠 四、项目部署指南
### 1.克隆本项目到本地
### 2.创建数据库
```sql
-- 创建企业表
CREATE TABLE `companies` (
                             `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '企业主键ID',
                             `company_name` varchar(100) NOT NULL COMMENT '企业名称',
                             `contact_person` varchar(50) NOT NULL COMMENT '联系人姓名',
                             `contact_phone` varchar(20) NOT NULL COMMENT '联系人电话',
                             `contact_email` varchar(100) DEFAULT NULL COMMENT '联系人邮箱',
                             `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `idx_company_name` (`company_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='企业表';


-- 创建用户表
CREATE TABLE `users` (
                         `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户主键ID',
                         `username` varchar(50) NOT NULL COMMENT '账号',
                         `password` varchar(100) NOT NULL COMMENT '密码（加密后）',
                         `role` tinyint(4) NOT NULL DEFAULT 2 COMMENT '用户类型（1=超级管理员，2=企业用户）',
                         `company_id` int(11) DEFAULT NULL COMMENT '所属企业ID',
                         `company_name` varchar(100) DEFAULT NULL COMMENT '企业名称（冗余）',
                         `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
                         `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
                         `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                         `gender` tinyint(4) DEFAULT 0 COMMENT '性别（0=未知，1=男，2=女）',
                         `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
                         `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '用户状态（1=启用，0=禁用）',
                         PRIMARY KEY (`id`),
                         UNIQUE KEY `idx_username` (`username`),
                         KEY `idx_role` (`role`),
                         KEY `idx_status` (`status`),
                         KEY `idx_company_id` (`company_id`),
                         CONSTRAINT `fk_users_company` FOREIGN KEY (`company_id`) REFERENCES `companies`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 创建管理员表
CREATE TABLE `admins` (
                          `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户主键ID',
                          `username` varchar(50) NOT NULL COMMENT '账号',
                          `password` varchar(100) NOT NULL COMMENT '密码（加密后）',
                          `role` tinyint(4) NOT NULL DEFAULT 2 COMMENT '用户类型（1=超级管理员，2=企业用户）',
                          `phone` varchar(20) DEFAULT NULL COMMENT '手机号码',
                          `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
                          `gender` tinyint(4) DEFAULT 0 COMMENT '性别（0=未知，1=男，2=女）',
                          `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '用户状态（1=启用，0=禁用）',
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `idx_username` (`username`),
                          KEY `idx_role` (`role`),
                          KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

INSERT INTO admins (username, password, role, phone, email, gender, avatar, create_time, status)
VALUES (
           'admin',
           '49e3b15e9db848828aa6f0c43095877fd7e34cebdf7242f94415d1080e9a869f', -- 生成的加密密码
           1,
           '13800000000',
           'admin@alliance.com',
           1,
           NULL,
           NOW(),
           1
       );

-- 移除原有的外键约束
ALTER TABLE users DROP FOREIGN KEY fk_users_company;

-- 重新添加外键约束，并设置为级联删除
ALTER TABLE users
    ADD CONSTRAINT fk_users_company
        FOREIGN KEY (company_id) REFERENCES companies(id)
            ON DELETE CASCADE;

-- 创建课程表
CREATE TABLE `course` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `course_name` varchar(100) NOT NULL,
                          `cover_image` varchar(255) ,
                          `introduction` text,
                          `sort_order` int(11) NOT NULL,
                          `video_url` varchar(255),
                          `author` varchar(50) NOT NULL,
                          `status` int(11) NOT NULL DEFAULT 0 COMMENT '0-待审核，1-已通过，2-未通过',
                          `create_time` datetime NOT NULL,
                          `update_time` datetime NOT NULL,
                          `reject_reason` varchar(255) DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          KEY `idx_course_name` (`course_name`),
                          KEY `idx_sort_order` (`sort_order`),
                          KEY `idx_author` (`author`),
                          KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `course`
    ADD COLUMN `author_identity` ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER' COMMENT '添加人身份: ADMIN, USER';

-- 创建课程合集表
CREATE TABLE `course_collection` (
                                     `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
                                     `collection_name` VARCHAR(100) NOT NULL COMMENT '合集名称',
                                     `description` TEXT COMMENT '合集描述',
                                     `cover_image` VARCHAR(255) COMMENT '合集封面',
                                     `create_time` DATETIME NOT NULL,
                                     `update_time` DATETIME NOT NULL,
                                     PRIMARY KEY (`id`),
                                     KEY `idx_collection_name` (`collection_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建课程-课程合集关系表
CREATE TABLE `course_collection_relation` (
                                              `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
                                              `collection_id` BIGINT(20) NOT NULL COMMENT '合集ID',
                                              `course_id` BIGINT(20) NOT NULL COMMENT '课程ID',
                                              `sort_order` INT(11) DEFAULT 0 COMMENT '合集中的排序',
                                              PRIMARY KEY (`id`),
                                              UNIQUE KEY `uk_collection_course` (`collection_id`, `course_id`),
                                              KEY `idx_collection_id` (`collection_id`),
                                              KEY `idx_course_id` (`course_id`),
                                              CONSTRAINT `fk_collection_id` FOREIGN KEY (`collection_id`) REFERENCES `course_collection` (`id`) ON DELETE CASCADE,
                                              CONSTRAINT `fk_course_id` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 创建行业动态表
CREATE TABLE `news_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '新闻主键ID',
  `title` varchar(255) NOT NULL COMMENT '标题',
  `news_image` varchar(500) DEFAULT NULL COMMENT '新闻图片',
  `content` text COMMENT '内容',
  `summary` text COMMENT '摘要',
  `author` varchar(100) DEFAULT NULL COMMENT '作者',
  `status` int DEFAULT '0',
  `reject_reason` varchar(255) DEFAULT NULL COMMENT '驳回原因',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` int DEFAULT '0',
  `create_user_id` int DEFAULT NULL COMMENT '创建者ID',
  `is_top` tinyint DEFAULT '0' COMMENT '是否置顶（0-否，1-是）',
  `view_count` int DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='新闻信息表';

-- 创建动态附件表
CREATE TABLE `news_attachment` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '附件主键ID',
  `news_id` bigint NOT NULL COMMENT '所属新闻ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件名',
  `file_url` varchar(500) NOT NULL COMMENT '文件URL',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`),
  KEY `news_id` (`news_id`),
  CONSTRAINT `news_attachment_ibfk_1` FOREIGN KEY (`news_id`) REFERENCES `news_info` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='新闻附件表';
```
### 3.Springboot的application.yml配置
``` yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/alliance?characterEncoding=utf-8&&userSSL=false
    username: 写数据库的用户名
    password: 写数据库的密码
    driver-class-name: com.mysql.cj.jdbc.Driver
  mvc:
    static-path-pattern: /**   # 允许访问 static 映射
  servlet:
    multipart:
      max-file-size: 1000MB      # 设置为你想的上传文件最大限制
      max-request-size: 1000MB   # 设置为你想的上传文件最大限制

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

mybatis:
  mapper-locations: [classpath:mapper/**Mapper.xml, classpath:generator/**Mapper.xml]
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    map-underscore-to-camel-case: false  # 关闭驼峰

upload:
  image-path: E:/uploads/images        # 设置为你的图片上传路径
  video-path: E:/uploads/videos/       # 设置为你的视频上传路径
  static-path: /static/                # 设置为你的静态资源访问路径

```

### 4.运行项目
>>>>>>> faa059ef9276c08abe27d1aaf3b1f0446be68db0
