package com.neu.alliance.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.neu.alliance.dto.CourseUpdateRequest;
import com.neu.alliance.entity.CourseCollectionRelation;
import com.neu.alliance.entity.Result;
import com.neu.alliance.service.CourseCollectionRelationService;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import com.neu.alliance.entity.Course;
import com.neu.alliance.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class CourseController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseCollectionRelationService courseCollectionRelationService;


    @Value("${upload.image-path}")
    private String imagePath;

    @Value("${upload.video-path}")
    private String videoPath;

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);


    @PostMapping("/addcourse")
    public Result addCourse(
            @RequestParam String course_name,
            @RequestParam String introduction,
            @RequestParam String sort_order,
            @RequestParam String author,

            @RequestParam MultipartFile coverImage,  // 图片文件
            @RequestParam MultipartFile videoFile,   // 视频文件

            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) List<Long> collectionIds // 合集 ID 列表
    ) throws IOException {

        // 1. 保存文件
        String coverImageURL = courseService.saveFile(coverImage, imagePath, "/static/image/");
        String videoPathURL = courseService.saveFile(videoFile, videoPath, "/static/video/");

        // 2. 构造 Course 实体
        Course course = new Course();
        course.setId(id != null ? id.longValue() : null);
        course.setCourse_name(course_name);
        course.setIntroduction(introduction);
        course.setSort_order(Integer.parseInt(sort_order));
        course.setAuthor(author);
        course.setCover_image(coverImageURL);
        course.setVideo_url(videoPathURL);

        // 3. 添加课程
        int result = courseService.addCourse(course);
        if (result > 0 && course.getId() != null) {
            Long courseId = course.getId();
            // 合集ID列表：
            System.out.println("合集ID列表："+collectionIds);
            // 4. 添加课程-合集关系
            if (collectionIds != null && !collectionIds.isEmpty()) {
                for (Long collectionId : collectionIds) {
                    CourseCollectionRelation relation = new CourseCollectionRelation();
                    relation.setCourse_id(courseId);
                    relation.setCollection_id(collectionId);
                    relation.setSort_order(0); // 默认排序值为 0，可按需修改
                    System.out.println("插入关系——课程id为："+courseId);
                    System.out.println("插入关系——合集id为："+collectionId);

                    courseCollectionRelationService.insertRelation(relation); // 插入关系
                }
            }

            return Result.success(courseId); // 成功返回新课程ID
        } else {
            return Result.error("添加课程失败");
        }
    }

    @PostMapping("/course/uploadImage")
    public String uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return courseService.saveFile(file, imagePath, "/upload/images/");
    }

    @PostMapping("/course/uploadVideo")
    public String uploadVideo(@RequestParam("file") MultipartFile file) throws IOException {
        return courseService.saveFile(file, videoPath, "/upload/videos/");
    }

    // 分页查询所有课程
    @GetMapping("/selectAll")
    public Result selectAll(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String author_identity,
            @RequestParam(required = false) String author
    ) {
        PageHelper.startPage(pageNum, pageSize);
        List<Course> list = courseService.findAll(courseName, author_identity, author);
        PageInfo<Course> pageInfo = new PageInfo<>(list);
        return Result.success(pageInfo);
    }

    // 删除课程
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        System.out.println("调用删除，id=" + id);
        courseService.deleteById(id);
        return Result.success();
    }

    // 更新课程
    @PutMapping(value = "/updateCourse", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result updateCourse(@ModelAttribute CourseUpdateRequest request) {
        logger.info("收到文件: {}",
                request.getCover_image() != null ? request.getCover_image().getOriginalFilename() : "null");
        try {
            logger.info("收到课程更新请求 - ID: {}, 课程名: {}", request.getId(), request.getCourse_name());

            // 获取原始课程
            Course existingCourse = courseService.getById(request.getId());
            if (existingCourse == null) {
                return Result.error("课程不存在");
            }

            // 更新基础字段
            if (request.getCourse_name() != null && !request.getCourse_name().isEmpty()) {
                existingCourse.setCourse_name(request.getCourse_name());
            }
            if (request.getIntroduction() != null) {
                existingCourse.setIntroduction(request.getIntroduction());
            }
            if (request.getAuthor() != null && !request.getAuthor().isEmpty()) {
                existingCourse.setAuthor(request.getAuthor());
            }

            // 处理封面图片
            if (request.getCover_image() != null && !request.getCover_image().isEmpty()) {
                logger.info("更新封面图片: {}", request.getCover_image().getOriginalFilename());
                String coverPath = courseService.saveFile(
                        request.getCover_image(),
                        imagePath,
                        "/static/image/"
                );
                existingCourse.setCover_image(coverPath);
            }

            // 处理视频文件
            if (request.getVideo_url() != null && !request.getVideo_url().isEmpty()) {
                logger.info("更新视频文件: {}", request.getVideo_url().getOriginalFilename());
                String videoPathURL = courseService.saveFile(
                        request.getVideo_url(),
                        videoPath,
                        "/static/video/"
                );
                existingCourse.setVideo_url(videoPathURL);
            }

            // 更新修改时间
            existingCourse.setUpdate_time(LocalDate.now());

            // 保存更新
            int result = courseService.updateCourse(existingCourse);
            if (result > 0) {
                return Result.success("课程更新成功");
            } else {
                return Result.error("课程更新失败");
            }
        } catch (Exception e) {
            logger.error("更新课程失败", e);
            return Result.error("课程更新失败: " + e.getMessage());
        }
    }

    // 课程审核通过
    @PutMapping("/course/pass/{id}")
    public Result passCourse(@PathVariable Long id) {
        Course course = courseService.getById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        course.setStatus(1); // 设置为已通过
        course.setUpdate_time(LocalDate.now());
        courseService.updateCourse(course);
        return Result.success("审核通过");
    }

    // 课程驳回
    @PutMapping("/course/reject")
    public Result rejectCourse(@RequestParam Long id, @RequestParam String reason) {
        Course course = courseService.getById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        course.setStatus(2); // 设置为未通过
        course.setReject_reason(reason);
        course.setUpdate_time(LocalDate.now());
        courseService.updateCourse(course);
//        System.out.println("驳回状态设置成功，status=" + course.getStatus() + ", reason=" + course.getReject_reason());

        return Result.success("课程已驳回");
    }




}