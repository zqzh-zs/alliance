package com.neu.alliance.mapper;

import com.neu.alliance.entity.CourseCollection;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseCollectionDetailMapper {
    CourseCollection getById(Long collectionId);
}
