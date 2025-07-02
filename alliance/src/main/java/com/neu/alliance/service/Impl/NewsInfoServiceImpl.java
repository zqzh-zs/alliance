package com.neu.alliance.service.Impl;

import com.neu.alliance.dto.NewsQueryDTO;
import com.neu.alliance.entity.NewsAttachment;
import com.neu.alliance.entity.NewsInfo;
import com.neu.alliance.entity.User;
import com.neu.alliance.mapper.NewsAttachmentMapper;
import com.neu.alliance.mapper.NewsInfoMapper;
import com.neu.alliance.service.NewsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewsInfoServiceImpl implements NewsInfoService {

    @Autowired
    private NewsInfoMapper newsInfoMapper;

    @Autowired
    private NewsAttachmentMapper newsAttachmentMapper;

    @Override
    public void create(NewsInfo newsInfo) {
        newsInfo.setStatus(0);
        newsInfo.setViewCount(0);
        newsInfo.setDeleted(0);
        newsInfo.setIsTop(0);
        newsInfoMapper.insert(newsInfo);

        if (newsInfo.getAttachments() != null && !newsInfo.getAttachments().isEmpty()) {
            saveAttachments(newsInfo.getId(), newsInfo.getAttachments());
        }
    }

//    @Override
//    public void update(NewsInfo newsInfo, User user) {
//        NewsInfo db = newsInfoMapper.selectById(newsInfo.getId());
//        if (user.getRole() != 1 && !user.getId().equals(db.getCreateUserId())) {
//            throw new RuntimeException("无权限编辑该动态");
//        }
//
//        // 只有当明确设置了 updateTime 时，才保留它
//        if (newsInfo.getUpdateTime() == null) {
//            // 避免默认值进入 mapper 导致 update_time 被更新
//            newsInfo.setUpdateTime(null);
//        }
//
//        newsInfoMapper.update(newsInfo);
//
//        // 只有当前端传了附件列表，才更新附件
//        if (newsInfo.getAttachments() != null) {
//            // 先删
//            deleteAttachments(newsInfo.getId());
//            // 再新增
//            if (!newsInfo.getAttachments().isEmpty()) {
//                saveAttachments(newsInfo.getId(), newsInfo.getAttachments());
//            }
//        }
//        // 如果 attachments 是 null，说明前端没传附件信息，不改动附件表
//    }

    @Override
    public void update(NewsInfo newsInfo, User user) {
        NewsInfo db = newsInfoMapper.selectById(newsInfo.getId());

        // 显式检查已删除状态
        if (db == null) {
            throw new RuntimeException("动态不存在");
        }
        if (db.getDeleted() == 1) {
            throw new RuntimeException("动态已删除");
        }

        if (user.getRole() != 1 && !user.getId().equals(db.getCreateUserId())) {
            throw new RuntimeException("无权限编辑该动态");
        }

        // 这是“主编辑方法”，一定更新时间
        //
        // newsInfo.setUpdateTime(LocalDateTime.now());
        System.out.println("调用了 update 方法，newsInfo = " + newsInfo);
        newsInfoMapper.update(newsInfo);

        updateAttachments(newsInfo);
    }

    @Override
    public void updateWithoutTime(NewsInfo news, User user) {
        // 权限校验等逻辑不动
        newsInfoMapper.updateWithoutTime(news);
    }

    void updateAttachments(NewsInfo newsInfo) {
        if (newsInfo.getAttachments() != null) {
            deleteAttachments(newsInfo.getId());
            if (!newsInfo.getAttachments().isEmpty()) {
                saveAttachments(newsInfo.getId(), newsInfo.getAttachments());
            }
        }
    }

    @Override
    public void delete(Long id, User user) {
        NewsInfo db = newsInfoMapper.selectById(id);
        if (db == null || db.getDeleted() == 1) return;
        if (user.getRole() != 1 && !user.getId().equals(db.getCreateUserId())) {
            throw new RuntimeException("无权限删除该动态");
        }
        db.setDeleted(1);
        newsInfoMapper.update(db);

        // 删除附件
        deleteAttachments(id);
    }

    @Override
    public NewsInfo getById(Long id) {
        NewsInfo info = newsInfoMapper.selectById(id);
        System.out.println("getById called, info = " + info);
        if (info != null) {
            // 加载附件
            List<NewsAttachment> attachments = newsAttachmentMapper.selectByNewsId(id);
            info.setAttachments(attachments);
        }
        return info;
    }

    @Override
    public List<NewsInfo> listByQuery(NewsQueryDTO query) {
        // 只有两个参数都不为null时才计算offset
        if (query.getPageNum() != null && query.getPageSize() != null) {
            query.setOffset((query.getPageNum() - 1) * query.getPageSize());
        }
        return newsInfoMapper.selectByQuery(query);
    }

    @Override
    public int countByQuery(NewsQueryDTO query) {
        return newsInfoMapper.countByQuery(query);
    }

    @Override
    public void audit(Long id, Integer status, String reason) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("status", status);
        params.put("reason", reason);
        newsInfoMapper.auditNews(params);
    }

    @Override
    public void saveAttachments(Long newsId, List<NewsAttachment> attachments) {
        for (NewsAttachment a : attachments) {
            a.setNewsId(newsId);
            int rows = newsAttachmentMapper.insert(a);
            System.out.println("插入附件，newsId=" + newsId + ", fileName=" + a.getFileName() + ", 返回插入行数=" + rows);
        }
    }

    @Override
    public List<NewsAttachment> getAttachments(Long newsId) {
        return newsAttachmentMapper.selectByNewsId(newsId);
    }

    @Override
    public void deleteAttachments(Long newsId) {
        newsAttachmentMapper.deleteByNewsId(newsId);
    }

    @Override
    public void updateCover(NewsInfo newsInfo) {
        updateWithoutTime(newsInfo, null);  // 不校验权限，专用封面更新
        System.out.println("updateCover title = " + newsInfo.getTitle());
        System.out.println("update attachments = " + newsInfo.getAttachments());
    }

    public void deleteAttachment(Long id, User user) {
        // 可选：校验权限（附件是否属于该用户）
        NewsAttachment attachment = newsAttachmentMapper.selectById(id);
        if (attachment == null) {
            throw new RuntimeException("附件不存在");
        }

        // 这里可以加入权限校验，比如 attachment.newsId -> news.createUserId == user.getId()

        newsAttachmentMapper.deleteById(id);
    }

    public void incrementViewCount(Long id) {
        NewsInfo news = newsInfoMapper.selectById(id);
        if (news != null) {
            Integer currentViewCount = news.getViewCount();
            if (currentViewCount == null) {
                currentViewCount = 0;
            }
            currentViewCount += 1;
            news.setViewCount(currentViewCount);
            newsInfoMapper.updateViewCount(news.getId(), currentViewCount);
        }
    }
}