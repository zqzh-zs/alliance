package com.neu.alliance.mapper;

import com.neu.alliance.dto.NewsQueryDTO;
import com.neu.alliance.entity.NewsAttachment;
import com.neu.alliance.entity.NewsInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

@Mapper
public interface NewsInfoMapper {
    int insert(NewsInfo newsInfo);
    int update(NewsInfo newsInfo);
    int deleteById(Long id);
    NewsInfo selectById(Long id);
    List<NewsInfo> selectByQuery(NewsQueryDTO query);
    int countByQuery(NewsQueryDTO query);
    int auditNews(Map<String, Object> params);
    int updateViewCount(@Param("id") Long id, @Param("viewCount") Integer viewCount);
    void updateWithoutTime(NewsInfo newsInfo);

    int incrementBehaviorCounts(@Param("id") Long id,
                                @Param("viewCount") Integer viewCount,
                                @Param("likeCount") Integer likeCount,
                                @Param("shareCount") Integer shareCount);
}
