package com.neu.alliance.dto;

import lombok.Data;
import java.util.List;


public class AssignCoursesRequest {
    private Long collectionId;
    private List<Long> courseIds;

    public Long getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Long collectionId) {
        this.collectionId = collectionId;
    }

    public List<Long> getCourseIds() {
        return courseIds;
    }

    public void setCourseIds(List<Long> courseIds) {
        this.courseIds = courseIds;
    }
}
