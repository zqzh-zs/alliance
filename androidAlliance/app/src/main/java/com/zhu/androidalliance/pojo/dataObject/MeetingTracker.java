package com.zhu.androidalliance.pojo.dataObject;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MeetingTracker {
    private Integer meetingId = -1;       // 浏览的动态ID
    private int formSubmitCount;
    private int viewCount;
}
