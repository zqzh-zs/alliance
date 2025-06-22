package com.neu.alliance.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neu.alliance.entity.CollectionDetailVO;
import com.neu.alliance.entity.Course;
import com.neu.alliance.entity.CourseCollection;
import com.neu.alliance.mapper.CourseCertainCollectionMapper;
import com.neu.alliance.service.CourseCertainCollectionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseCertainCollectionServiceImpl implements CourseCertainCollectionService {

    @Autowired
    private CourseCertainCollectionMapper mapper;

//    @Override
//    public CollectionDetailVO getCollectionDetail(Long collectionId, int pageNum, int pageSize) {
//        // 查询合集信息
//        CourseCollection collection = mapper.selectCollectionById(collectionId);
//        if (collection == null) {
//            return null;
//        }
//
//        // 查询该合集下 status = 1 的课程，分页处理
//        PageHelper.startPage(pageNum, pageSize);
//        List<Course> courseList = mapper.selectPassedCoursesByCollectionId(collectionId);
//        PageInfo<Course> pageInfo = new PageInfo<>(courseList);
//
//        return new CollectionDetailVO(collection, pageInfo);
//    }

    @Override
    public CollectionDetailVO getCollectionDetail(Long collectionId, int pageNum, int pageSize) {
        CourseCollection collection = mapper.selectCollectionById(collectionId);
        if (collection == null) {
            return null;
        }

        // 🔁 不使用分页插件，直接返回所有 status = 1 的课程
        List<Course> courseList = mapper.selectPassedCoursesByCollectionId(collectionId);

        return new CollectionDetailVO(collection, courseList);
    }


    @Override
    public boolean updateCourseSortOrder(Long collectionId, Long courseId, int sortOrder) {
        int rows = mapper.updateCourseSortOrder(collectionId, courseId, sortOrder);
        return rows > 0;
    }
}
