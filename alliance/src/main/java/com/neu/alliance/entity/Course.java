package com.neu.alliance.entity;

import lombok.Data;


import java.time.LocalDate;
import java.util.Date;


public class Course {
    private Long id;
    private String course_name;
    private String cover_image;
    private String introduction;
    private Integer sort_order;
    private String video_url;
    private String author;
    private Integer status;
    private LocalDate create_time;
    private LocalDate update_time;
    private String reject_reason;
    private String author_identity;

    public Course(Long id, String course_name, String cover_image, String introduction, Integer sort_order, String video_url, String author, Integer status, LocalDate create_time, LocalDate update_time, String reject_reason, String author_identity) {
        this.id = id;
        this.course_name = course_name;
        this.cover_image = cover_image;
        this.introduction = introduction;
        this.sort_order = sort_order;
        this.video_url = video_url;
        this.author = author;
        this.status = status;
        this.create_time = create_time;
        this.update_time = update_time;
        this.reject_reason = reject_reason;
        this.author_identity = author_identity;
    }

    public Course() {
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

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public Integer getSort_order() {
        return sort_order;
    }

    public void setSort_order(Integer sort_order) {
        this.sort_order = sort_order;
    }

    public String getVideo_url() {
        return video_url;
    }

    public void setVideo_url(String video_url) {
        this.video_url = video_url;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDate getCreate_time() {
        return create_time;
    }

    public void setCreate_time(LocalDate create_time) {
        this.create_time = create_time;
    }

    public LocalDate getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(LocalDate update_time) {
        this.update_time = update_time;
    }

    public String getReject_reason() {
        return reject_reason;
    }

    public void setReject_reason(String reject_reason) {
        this.reject_reason = reject_reason;
    }

    public String getAuthor_identity() {
        return author_identity;
    }
    public void setAuthor_identity(String author_identity) {
        this.author_identity = author_identity;
    }
}