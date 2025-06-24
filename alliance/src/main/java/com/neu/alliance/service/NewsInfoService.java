package com.neu.alliance.service;

import com.neu.alliance.dto.NewsQueryDTO;
import com.neu.alliance.entity.NewsInfo;
import com.neu.alliance.entity.User;

import java.util.List;

public interface NewsInfoService {
    void create(NewsInfo newsInfo);
    void update(NewsInfo newsInfo, User user);
    void delete(Long id, User user);
    NewsInfo getById(Long id);
    List<NewsInfo> listByQuery(NewsQueryDTO query);
    int countByQuery(NewsQueryDTO query);
    void audit(Long id, Integer status, String reason);
    void saveAttachments(Long newsId, List<com.neu.alliance.entity.NewsAttachment> attachments);
    List<com.neu.alliance.entity.NewsAttachment> getAttachments(Long newsId);
    void deleteAttachments(Long newsId);
    void updateCover(NewsInfo newsInfo);
    void deleteAttachment(Long id, User user);
    void incrementViewCount(Long id);
    void updateWithoutTime(NewsInfo news, User user);
}