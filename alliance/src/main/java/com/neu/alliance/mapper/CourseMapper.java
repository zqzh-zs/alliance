package com.neu.alliance.mapper;

import com.neu.alliance.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseMapper {
    int insertCourse(Course course);
    int addCourse(Course course);

    // 新增：分页 + 条件查询课程
    List<Course> selectAll(@Param("course_name") String course_name,
                           @Param("author_identity") String author_identity,
                           @Param("author") String author);

    // 新增：删除课程
    void deleteById(Long id);

    Course getById(Long id);
    int updateCourse(Course course);

    List<Course> findAll(@Param("courseName") String courseName,
                         @Param("authorIdentity") String authorIdentity,
                         @Param("author") String author);

    // 新增：分页查询（用于关联合集）
    List<Course> selectAllPaged(@Param("courseName") String courseName,
                                @Param("authorIdentity") String authorIdentity,
                                @Param("author") String author);
}
