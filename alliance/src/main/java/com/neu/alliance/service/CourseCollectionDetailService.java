package com.neu.alliance.service;

import com.neu.alliance.entity.Course;
import com.neu.alliance.entity.CourseCollection;

import java.util.List;

public interface CourseCollectionDetailService {
    CourseCollection getCollectionById(Long collectionId);

    List<Course> getApprovedCoursesByCollectionId(Long collectionId);
}
