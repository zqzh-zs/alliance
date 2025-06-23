package com.neu.alliance.service;

import com.neu.alliance.entity.CourseCollection;

import java.util.List;

public interface CourseCollectionService {
    int addCourseCollection(CourseCollection collection);

    List<CourseCollection> getAllCollections();

    int updateCourseCollection(CourseCollection collection);


    int deleteCourseCollection(Integer id);

}
