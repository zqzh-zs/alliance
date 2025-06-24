package com.neu.alliance.service.Impl;

import com.github.pagehelper.PageHelper;
import com.neu.alliance.entity.Course;
import com.neu.alliance.entity.CourseWithCollectionsVO;
import com.neu.alliance.mapper.CourseCollectionMapper;
import com.neu.alliance.mapper.CourseMapper;
import com.neu.alliance.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private CourseCollectionMapper collectionMapper;
    @Transactional  // 事务注解，确保回填正常
    @Override

    public int addCourse(Course course) {
        int rows = courseMapper.insertCourse(course);
        System.out.println("新增课程ID：" + course.getId());  // 一定要确认这里打印的不是 null
        return rows;
    }

    @Override
    public String saveFile(MultipartFile file, String basePath, String returnPathPrefix) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("文件名不合法");
        }

        String ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String fileName = uuid + ext;

        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File dest = new File(dir, fileName);
        file.transferTo(dest);

        // 注意：这里拼接的是对外暴露的静态路径
        return returnPathPrefix + fileName;
    }

    @Override
    public List<Course> findAll(String courseName, String authorIdentity, String author) {
        return courseMapper.selectAll(courseName, authorIdentity, author);
    }


    @Override
    public void deleteById(Long id) {
        courseMapper.deleteById(id);
    }

    @Override
    public Course getById(Long id) {
        return courseMapper.getById(id);
    }

    @Override
    public int updateCourse(Course course) {
        return courseMapper.updateCourse(course);
    }

    @Override
    public List<CourseWithCollectionsVO> selectAllWithCollections(int pageNum, int pageSize, String courseName, String authorIdentity, String author) {
        PageHelper.startPage(pageNum, pageSize);  // 启用分页
        List<Course> courses = courseMapper.selectAllPaged(courseName, authorIdentity, author);
        List<CourseWithCollectionsVO> result = new ArrayList<>();

        for (Course course : courses) {
            List<String> collectionNames = collectionMapper.findNamesByCourseId(course.getId());
            result.add(new CourseWithCollectionsVO(course, collectionNames));
        }

        return result;
    }

}