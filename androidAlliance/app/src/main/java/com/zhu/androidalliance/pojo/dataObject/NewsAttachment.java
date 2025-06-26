package com.zhu.androidalliance.pojo.dataObject;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class NewsAttachment implements Serializable {
    private Integer id;
    private Long newsId;
    private String fileName;
    private String fileUrl;
    private LocalDateTime uploadTime;
}