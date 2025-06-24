package com.neu.alliance.service;


import com.neu.alliance.entity.Course;
import com.neu.alliance.entity.CourseWithCollectionsVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface CourseService {
    int addCourse(Course course);

    String saveFile(MultipartFile file, String basePath, String returnPathPrefix) throws IOException;

    List<Course> findAll(String courseName, String authorIdentity, String author);

    void deleteById(Long id);

    Course getById(Long id);

    int updateCourse(Course course);

    List<CourseWithCollectionsVO> selectAllWithCollections(int pageNum, int pageSize, String courseName, String authorIdentity, String author);


}
