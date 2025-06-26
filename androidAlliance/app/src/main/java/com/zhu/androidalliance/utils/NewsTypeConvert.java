package com.zhu.androidalliance.utils;

import com.zhu.androidalliance.enums.BehaviorType;
import com.zhu.androidalliance.pojo.dataObject.NewsBehavior;

public class NewsTypeConvert {
    public static void convert(BehaviorType type, NewsBehavior newsBehavior) {
        switch (type) {
            case LIKE:
                newsBehavior.setLikeCount(newsBehavior.getLikeCount() + 1);
                break;
            case VIEW:
                newsBehavior.setViewCount(newsBehavior.getViewCount() + 1);
                break;
            case SHARE:
                newsBehavior.setSharedCount(newsBehavior.getSharedCount() + 1);
                break;
            case BACK:
                // 记录返回行为，可用于统计浏览时长等
                break;
        }
    }
}