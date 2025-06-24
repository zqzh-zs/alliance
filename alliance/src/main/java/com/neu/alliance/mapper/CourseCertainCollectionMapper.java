package com.neu.alliance.mapper;

import com.neu.alliance.entity.CourseCollection;
import com.neu.alliance.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseCertainCollectionMapper {

    CourseCollection selectCollectionById(Long collectionId);

    List<Course> selectPassedCoursesByCollectionId(Long collectionId);

    int updateCourseSortOrder(@Param("collectionId") Long collectionId,
                              @Param("courseId") Long courseId,
                              @Param("sortOrder") int sortOrder);
}
