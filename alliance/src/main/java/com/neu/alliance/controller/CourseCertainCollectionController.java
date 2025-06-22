package com.neu.alliance.controller;

import com.neu.alliance.entity.CollectionDetailVO;
import com.neu.alliance.entity.Result;
import com.neu.alliance.service.CourseCertainCollectionService;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/course-certain-collection")
@CrossOrigin
public class CourseCertainCollectionController {

    @Resource
    private CourseCertainCollectionService certainCollectionService;

    /**
     * 根据合集ID获取合集详情（介绍、封面）和课程视频列表（status=1）
     * 支持分页查询课程，pageNum和pageSize用于分页
     */
//    @GetMapping("/{collectionId}/detail")
//    public Result getCollectionDetail(
//            @PathVariable Long collectionId,
//            @RequestParam(defaultValue = "1") int pageNum,
//            @RequestParam(defaultValue = "10") int pageSize) {
//        try {
//            CollectionDetailVO detailVO = certainCollectionService.getCollectionDetail(collectionId, pageNum, pageSize);
//            if (detailVO == null) {
//                return Result.error("合集不存在");
//            }
//            return Result.success(detailVO);
//        } catch (Exception e) {
//            return Result.error("查询失败：" + e.getMessage());
//        }
//    }

    @GetMapping("/{collectionId}/detail")
    public Result getCollectionDetail(
            @PathVariable Long collectionId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            CollectionDetailVO detailVO = certainCollectionService.getCollectionDetail(collectionId, pageNum, pageSize);
            if (detailVO == null) {
                return Result.error("合集不存在");
            }
            return Result.success(detailVO);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }


    /**
     * 调整合集内某课程的排序
     * @param collectionId 合集ID
     * @param courseId 课程ID
     * @param sortOrder 新排序值
     */
    @PutMapping("/{collectionId}/course/{courseId}/sortOrder")
    public Result updateCourseSortOrder(
            @PathVariable Long collectionId,
            @PathVariable Long courseId,
            @RequestParam int sortOrder) {
        try {
            boolean success = certainCollectionService.updateCourseSortOrder(collectionId, courseId, sortOrder);
            if (success) {
                return Result.success("排序更新成功");
            } else {
                return Result.error("排序更新失败");
            }
        } catch (Exception e) {
            return Result.error("排序更新失败：" + e.getMessage());
        }
    }
}
