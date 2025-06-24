package com.neu.alliance.service.Impl;

import com.neu.alliance.entity.CourseCollectionRelation;
import com.neu.alliance.mapper.CourseCollectionRelationMapper;
import com.neu.alliance.service.CourseCollectionRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseCollectionRelationServiceImpl implements CourseCollectionRelationService {

    @Autowired
    private CourseCollectionRelationMapper relationMapper;

    @Override
    public void assignCourses(Long collectionId, List<Long> courseIds) {

        // 打印调试信息
        System.out.println("集合 ID: " + collectionId);
        System.out.println("课程 ID 列表: " + courseIds);

        // 防止 courseIds 为 null
        if (courseIds == null || courseIds.isEmpty()) {
            System.out.println("courseIds 为空，跳过插入");
            return;
        }
        // 先删除已有的
        relationMapper.deleteByCollectionId(collectionId);
        // 再批量插入
        for (int i = 0; i < courseIds.size(); i++) {
            CourseCollectionRelation relation = new CourseCollectionRelation();
            relation.setCollection_id(collectionId);
            relation.setCourse_id(courseIds.get(i));
            relation.setSort_order(i); // 你原本用 i 排序

            relationMapper.insertRelation(relation);
//            relationMapper.insertRelation(collectionId, courseIds.get(i), i);
        }
    }

    @Override
    public List<Long> getCourseIdsByCollectionId(Long collectionId) {
        return relationMapper.selectCourseIdsByCollectionId(collectionId);
    }

    @Override
    public void removeCourse(Long collectionId, Long courseId) {
        relationMapper.deleteByCollectionAndCourse(collectionId, courseId);
    }

    @Override
    public int insertRelation(CourseCollectionRelation relation) {
        return relationMapper.insertRelation(relation);
    }
}
