package com.neu.alliance.mapper;

import com.neu.alliance.entity.CourseCollectionRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseCollectionRelationMapper {

    void deleteByCollectionId(@Param("collectionId") Long collectionId);

//    void insertRelation(@Param("collectionId") Long collectionId,
//                        @Param("courseId") Long courseId,
//                        @Param("sortOrder") Integer sortOrder);

    int insertRelation(CourseCollectionRelation relation);


    List<Long> selectCourseIdsByCollectionId(@Param("collectionId") Long collectionId);

    void deleteByCollectionAndCourse(@Param("collectionId") Long collectionId,
                                     @Param("courseId") Long courseId);
}
