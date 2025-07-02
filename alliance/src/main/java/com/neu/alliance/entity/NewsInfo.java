package com.neu.alliance.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class NewsInfo {
    private Long id;
    private String title;
    private String newsImage;
    private String content;
    private String summary;
    private String author;
    private Integer status;
    private String rejectReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer deleted;
    private Integer createUserId;
    private Integer isTop;
    private Integer viewCount;
    private Integer likeCount;
    private Integer shareCount;
    private List<NewsAttachment> attachments;
}