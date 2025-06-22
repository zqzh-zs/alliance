package com.neu.alliance.service;

import com.neu.alliance.entity.CourseCollectionRelation;

import java.util.List;

public interface CourseCollectionRelationService {
    void assignCourses(Long collectionId, List<Long> courseIds);
    List<Long> getCourseIdsByCollectionId(Long collectionId);
    void removeCourse(Long collectionId, Long courseId);
    int insertRelation(CourseCollectionRelation relation);
}

