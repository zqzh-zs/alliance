package com.neu.alliance.service;

import com.neu.alliance.dto.NewsTracker;
import com.neu.alliance.entity.NewsInfo;

import java.util.List;

public interface TrackingService {
    // 安卓端上传行为数据
    void processTrackers(List<NewsTracker> trackers);

    // Web 端获取所有新闻行为数据
    List<NewsInfo> getAllBehaviors();
}