package com.neu.alliance.controller;

import com.neu.alliance.dto.AssignCoursesRequest;
import com.neu.alliance.entity.Result;
import com.neu.alliance.service.CourseCollectionRelationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course-collection-relation")
public class CourseCollectionRelationController {

    @Autowired
    private CourseCollectionRelationService relationService;

    /**
     * 给合集分配课程（一次分配多个课程）
     */

    @PostMapping("/assign")
    public Result assignCourses(@RequestBody AssignCoursesRequest request) {
        Long collectionId = request.getCollectionId();
        List<Long> courseIds = request.getCourseIds();
        System.out.println("集合ID：" + collectionId);
        System.out.println("课程IDs：" + courseIds);
        relationService.assignCourses(collectionId, courseIds);
        return Result.success("课程已成功分配到合集");
    }

    /**
     * 获取合集下所有课程 ID
     */
    @GetMapping("/courseIds")
    public Result getCourseIds(@RequestParam Long collectionId) {
        List<Long> ids = relationService.getCourseIdsByCollectionId(collectionId);
        return Result.success(ids);
    }

    /**
     * 从合集中移除指定课程
     */
    @DeleteMapping("/remove")
    public Result removeCourse(@RequestParam Long collectionId, @RequestParam Long courseId) {
        relationService.removeCourse(collectionId, courseId);
        return Result.success("课程已从合集中移除");
    }
}
