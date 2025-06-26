package com.neu.alliance.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NewsAttachment {
    private Integer id;
    private Long newsId;
    private String fileName;
    private String fileUrl;
    private LocalDateTime uploadTime;
}