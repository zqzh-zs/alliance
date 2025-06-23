package com.neu.alliance.mapper;

import com.neu.alliance.entity.CourseCollection;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CourseCollectionMapper {
    int insertCollection(CourseCollection collection);
    // ✅ 新增：查询所有合集
    List<CourseCollection> selectAll();
    // CourseCollectionMapper.java
    List<String> findNamesByCourseId(Long courseId);

    int updateCollection(CourseCollection collection);

    int deleteCollection(Integer id);

    int deleteCollectionRelations(Integer id);
}
