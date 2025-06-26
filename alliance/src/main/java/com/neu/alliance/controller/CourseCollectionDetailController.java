package com.neu.alliance.controller;

import com.neu.alliance.entity.Course;
import com.neu.alliance.entity.CourseCollection;
import com.neu.alliance.entity.Result;
import com.neu.alliance.service.CourseCollectionDetailService;
import com.neu.alliance.service.CourseService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course-collection-detail")
@CrossOrigin
public class CourseCollectionDetailController {

    @Resource
    private CourseCollectionDetailService detailService;

    @Resource
    private CourseService courseService;

    @GetMapping("/{collectionId}")
    public Result getCollectionDetail(@PathVariable Long collectionId) {
        CourseCollection collection = detailService.getCollectionById(collectionId);
        if (collection == null) {
            return Result.error("合集不存在");
        }

        List<Course> courses = detailService.getApprovedCoursesByCollectionId(collectionId);
        return Result.success(new CollectionDetailResponse(collection, courses));
    }

    // 内部类用于封装响应体
    public static class CollectionDetailResponse {
        private CourseCollection collection;
        private List<Course> courses;

        public CollectionDetailResponse(CourseCollection collection, List<Course> courses) {
            this.collection = collection;
            this.courses = courses;
        }

        public CourseCollection getCollection() {
            return collection;
        }

        public List<Course> getCourses() {
            return courses;
        }
    }
}
