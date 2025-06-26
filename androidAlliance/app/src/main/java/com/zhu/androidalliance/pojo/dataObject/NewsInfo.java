package com.zhu.androidalliance.pojo.dataObject;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class NewsInfo implements Serializable {
    private Integer id;
    private String title;
    private String newsImage;
    private String content;
    private String summary;
    private String author;
    private Integer status; // 0=待审核，1=已通过，2=未通过
    private String rejectReason;
    private Date createTime;
    private Date updateTime;
    private Integer deleted; // 0-未删除，1-已删除
    private Integer createUserId;
    private Integer isTop; // 0-否，1-是
    private Integer viewCount;
    private List<NewsAttachment> attachments; // 附件列表
}
