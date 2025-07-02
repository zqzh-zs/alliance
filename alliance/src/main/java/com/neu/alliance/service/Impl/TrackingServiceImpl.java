package com.neu.alliance.service.Impl;

import com.neu.alliance.dto.NewsQueryDTO;
import com.neu.alliance.dto.NewsTracker;
import com.neu.alliance.entity.NewsInfo;
import com.neu.alliance.mapper.NewsInfoMapper;
import com.neu.alliance.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackingServiceImpl implements TrackingService {

    @Autowired
    private NewsInfoMapper newsInfoMapper; // 假设你使用 MyBatis 映射器

    @Override
    public void processTrackers(List<NewsTracker> trackers) {
        for (NewsTracker tracker : trackers) {
            // 直接调用增量更新方法
            newsInfoMapper.incrementBehaviorCounts(
                    tracker.getNewsId().longValue(),
                    tracker.getViewCount(),
                    tracker.getLikeCount(),
                    tracker.getSharedCount()
            );
        }
    }

    @Override
    public List<NewsInfo> getAllBehaviors() {
        return newsInfoMapper.selectByQuery(new NewsQueryDTO());
    }
}