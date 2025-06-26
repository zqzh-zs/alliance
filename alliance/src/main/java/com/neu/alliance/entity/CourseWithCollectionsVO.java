package com.neu.alliance.entity;

import com.neu.alliance.entity.Course;
import lombok.Data;

import java.util.List;

@Data
public class CourseWithCollectionsVO {
    private Course course;
    private List<String> collectionNames; // 合集名称列表

    public CourseWithCollectionsVO(Course course, List<String> collectionNames) {
        this.course = course;
        this.collectionNames = collectionNames;
    }
}
