package com.zhu.androidalliance.utils;

import com.zhu.androidalliance.enums.BehaviorType;
import com.zhu.androidalliance.pojo.dataObject.NewsTracker;

public class NewsTypeConvert {
    public static void convert(BehaviorType type, NewsTracker newsTracker) {
        switch (type) {
            case LIKE:
                newsTracker.setLikeCount(newsTracker.getLikeCount() + 1);
                break;
            case VIEW:
                newsTracker.setViewCount(newsTracker.getViewCount() + 1);
                break;
            case SHARE:
                newsTracker.setSharedCount(newsTracker.getSharedCount() + 1);
                break;
            case BACK:
                // 记录返回行为，可用于统计浏览时长等
                break;
        }
    }
}