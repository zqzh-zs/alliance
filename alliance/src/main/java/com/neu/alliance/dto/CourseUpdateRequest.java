package com.neu.alliance.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CourseUpdateRequest {
    private Long id;
    private String course_name;
    private String introduction;
    private String author;
    private MultipartFile cover_image;
    private MultipartFile video_url;

    public CourseUpdateRequest(Long id, String course_name, String introduction, String author, MultipartFile cover_image, MultipartFile video_url) {
        this.id = id;
        this.course_name = course_name;
        this.introduction = introduction;
        this.author = author;
        this.cover_image = cover_image;
        this.video_url = video_url;
    }

    public CourseUpdateRequest() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public MultipartFile getCover_image() {
        return cover_image;
    }

    public void setCover_image(MultipartFile cover_image) {
        this.cover_image = cover_image;
    }

    public MultipartFile getVideo_url() {
        return video_url;
    }

    public void setVideo_url(MultipartFile video_url) {
        this.video_url = video_url;
    }
}

