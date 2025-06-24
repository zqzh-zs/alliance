package com.neu.alliance.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NewsQueryDTO {
    private String title;
    private String summary;
    private String newsImage;
    private String author;
    private Integer status;
    private Integer createUserId;
    private Integer pageNum;
    private Integer pageSize;
    private Integer offset;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean onlyMine;
}