package com.neu.alliance.service.Impl;

import com.neu.alliance.entity.Course;
import com.neu.alliance.entity.CourseCollection;
import com.neu.alliance.mapper.CourseCollectionDetailMapper;
import com.neu.alliance.mapper.CourseMapper;
import com.neu.alliance.mapper.CourseCollectionRelationMapper;
import com.neu.alliance.service.CourseCollectionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseCollectionDetailServiceImpl implements CourseCollectionDetailService {

    @Autowired
    private CourseCollectionDetailMapper detailMapper;

    @Autowired
    private CourseCollectionRelationMapper relationMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    public CourseCollection getCollectionById(Long collectionId) {
        return detailMapper.getById(collectionId);
    }

    @Override
    public List<Course> getApprovedCoursesByCollectionId(Long collectionId) {
        List<Long> courseIds = relationMapper.selectCourseIdsByCollectionId(collectionId);
        List<Course> result = new ArrayList<>();
        for (Long id : courseIds) {
            Course course = courseMapper.getById(id);
            if (course != null && course.getStatus() == 1) {
                result.add(course);
            }
        }
        return result;
    }
}
