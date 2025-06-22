package com.neu.alliance.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


public class CourseCollectionRelation implements Serializable {
    private Long id;

    private Long collection_id;  // 合集ID，对应数据库字段名

    private Long course_id;      // 课程ID，对应数据库字段名

    private Integer sort_order;  // 排序字段，对应数据库字段名

    public CourseCollectionRelation(Long id, Long collection_id, Long course_id, Integer sort_order) {
        this.id = id;
        this.collection_id = collection_id;
        this.course_id = course_id;
        this.sort_order = sort_order;
    }

    public CourseCollectionRelation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCollection_id() {
        return collection_id;
    }

    public void setCollection_id(Long collection_id) {
        this.collection_id = collection_id;
    }

    public Long getCourse_id() {
        return course_id;
    }

    public void setCourse_id(Long course_id) {
        this.course_id = course_id;
    }

    public Integer getSort_order() {
        return sort_order;
    }

    public void setSort_order(Integer sort_order) {
        this.sort_order = sort_order;
    }
}
