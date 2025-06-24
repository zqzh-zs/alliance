package com.neu.alliance.service;


import com.neu.alliance.entity.CollectionDetailVO;

public interface CourseCertainCollectionService {

    CollectionDetailVO getCollectionDetail(Long collectionId, int pageNum, int pageSize);

    boolean updateCourseSortOrder(Long collectionId, Long courseId, int sortOrder);

}
