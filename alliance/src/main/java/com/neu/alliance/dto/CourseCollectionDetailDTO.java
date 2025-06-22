package com.neu.alliance.dto;

import com.neu.alliance.entity.Course;
import com.neu.alliance.entity.CourseCollection;
import lombok.Data;

import java.util.List;

@Data
public class CourseCollectionDetailDTO {
    private CourseCollection collection;
    private List<Course> passedCourses; // status = 1 的课程
}
