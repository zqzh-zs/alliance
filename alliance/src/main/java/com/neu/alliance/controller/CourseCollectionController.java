package com.neu.alliance.controller;

import com.neu.alliance.entity.CourseCollection;
import com.neu.alliance.entity.Result;
import com.neu.alliance.service.CourseCollectionService;
import com.neu.alliance.service.CourseService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/course-collection")
@CrossOrigin
public class CourseCollectionController {

    @Resource
    private CourseCollectionService collectionService;

    @Resource
    private CourseService courseService; // 用于复用 saveFile 方法

    @Value("${upload.image-path}")
    private String imagePath;

    /**
     * 添加课程合集
     */
    @PostMapping("/add")
    public Result addCourseCollection(
            @RequestParam String collection_name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile cover_image
    ) {
        try {
            CourseCollection collection = new CourseCollection();
            collection.setCollection_name(collection_name);
            collection.setDescription(description);

            if (cover_image != null && !cover_image.isEmpty()) {
                String path = courseService.saveFile(cover_image, imagePath, "/static/image/");
                collection.setCover_image(path);
            }

            collection.setCreate_time(LocalDateTime.now());
            collection.setUpdate_time(LocalDateTime.now());

            int result = collectionService.addCourseCollection(collection);
            if (result > 0) {
                return Result.success("合集添加成功");
            } else {
                return Result.error("合集添加失败");
            }
        } catch (IOException e) {
            return Result.error("封面上传失败：" + e.getMessage());
        } catch (Exception e) {
            return Result.error("内部错误：" + e.getMessage());
        }
    }


    @GetMapping("/listAll")
    public Result listAllCollections() {
        try {
            return Result.success(collectionService.getAllCollections());
        } catch (Exception e) {
            return Result.error("获取合集列表失败: " + e.getMessage());
        }
    }


    @PutMapping
    public Result updateCourseCollection(@RequestBody CourseCollection collection) {
        try {
            collection.setUpdate_time(LocalDateTime.now());
            int result = collectionService.updateCourseCollection(collection);
            if (result > 0) {
                return Result.success("合集更新成功");
            } else {
                return Result.error("未找到该合集或更新失败");
            }
        } catch (Exception e) {
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result deleteCourseCollection(@PathVariable Integer id) {
        try {
            int result = collectionService.deleteCourseCollection(id);
            if (result > 0) {
                return Result.success("合集删除成功");
            } else {
                return Result.error("删除失败，可能是ID不存在");
            }
        } catch (Exception e) {
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    // 后续可以在这里加：分页查询合集、删除合集、更新合集、获取合集下的课程等功能
}
