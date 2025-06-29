package com.zhu.androidalliance.pojo.dataObject;


import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NewsTracker implements Serializable {
    private Integer newsId;       // 浏览的动态ID
    private int sharedCount;
    private int viewCount;
    private int likeCount;
}
