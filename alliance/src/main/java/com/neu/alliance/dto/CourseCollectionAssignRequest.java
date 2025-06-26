package com.neu.alliance.dto;

import lombok.Data;

import java.util.List;

@Data
public class CourseCollectionAssignRequest {
    private Long collectionId;
    private List<Long> courseIds;
}
