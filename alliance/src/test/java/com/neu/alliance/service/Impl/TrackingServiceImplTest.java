package com.neu.alliance.service.Impl;

import com.neu.alliance.dto.NewsQueryDTO;
import com.neu.alliance.dto.NewsTracker;
import com.neu.alliance.entity.NewsInfo;
import com.neu.alliance.mapper.NewsInfoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TrackingServiceImplTest {

    @InjectMocks
    private TrackingServiceImpl trackingService;

    @Mock
    private NewsInfoMapper newsInfoMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // 初始化 @Mock 和 @InjectMocks
    }

    @Test
    void processTrackers_shouldCallIncrementBehaviorCountsForEachTracker() {
        // Arrange
        NewsTracker t1 = new NewsTracker();
        t1.setNewsId(1);
        t1.setViewCount(5);
        t1.setLikeCount(2);
        t1.setSharedCount(1);

        NewsTracker t2 = new NewsTracker();
        t2.setNewsId(2);
        t2.setViewCount(3);
        t2.setLikeCount(0);
        t2.setSharedCount(0);

        List<NewsTracker> trackers = Arrays.asList(t1, t2);

        // Act
        trackingService.processTrackers(trackers);

        // Assert
        verify(newsInfoMapper, times(1)).incrementBehaviorCounts(1L, 5, 2, 1);
        verify(newsInfoMapper, times(1)).incrementBehaviorCounts(2L, 3, 0, 0);
        verifyNoMoreInteractions(newsInfoMapper);
    }

    @Test
    void getAllBehaviors_shouldReturnAllNewsInfo() {
        // Arrange
        List<NewsInfo> mockList = new ArrayList<>();
        NewsInfo news1 = new NewsInfo();
        news1.setId(1L);
        mockList.add(news1);

        when(newsInfoMapper.selectByQuery(any(NewsQueryDTO.class))).thenReturn(mockList);

        // Act
        List<NewsInfo> result = trackingService.getAllBehaviors();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(newsInfoMapper, times(1)).selectByQuery(any(NewsQueryDTO.class));
    }
}