package com.neu.alliance.mapper;

import com.neu.alliance.entity.NewsAttachment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface NewsAttachmentMapper {
    int insert(NewsAttachment attachment);
    int deleteByNewsId(@Param("newsId") Long newsId);
    List<NewsAttachment> selectByNewsId(@Param("newsId") Long newsId);
    void deleteById(Long id);
    NewsAttachment selectById(Long id);
}
