package com.neu.alliance.service.Impl;

import com.neu.alliance.entity.CourseCollection;
import com.neu.alliance.mapper.CourseCollectionMapper;
import com.neu.alliance.service.CourseCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseCollectionServiceImpl implements CourseCollectionService {

    @Autowired
    private CourseCollectionMapper collectionMapper;

    @Override
    public int addCourseCollection(CourseCollection collection) {
        return collectionMapper.insertCollection(collection);
    }

    @Override
    public List<CourseCollection> getAllCollections() {
        return collectionMapper.selectAll();
    }

    @Override
    public int updateCourseCollection(CourseCollection collection) {
        return collectionMapper.updateCollection(collection);
    }

    @Override
    public int deleteCourseCollection(Integer id) {
        // 先删关联关系，再删合集
        collectionMapper.deleteCollectionRelations(id);
        return collectionMapper.deleteCollection(id);
    }
}
