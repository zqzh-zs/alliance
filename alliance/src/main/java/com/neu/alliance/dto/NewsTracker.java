package com.neu.alliance.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class NewsTracker implements Serializable {
    private Integer newsId;
    private int sharedCount;
    private int viewCount;
    private int likeCount;
}