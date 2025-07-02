package com.neu.alliance.controller;

import com.neu.alliance.dto.NewsQueryDTO;
import com.neu.alliance.dto.NewsTracker;
import com.neu.alliance.entity.NewsAttachment;
import com.neu.alliance.entity.NewsInfo;
import com.neu.alliance.entity.User;
import com.neu.alliance.service.NewsInfoService;
import com.neu.alliance.service.TrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsInfoService newsInfoService;

    @Autowired
    private TrackingService trackingService;

    @PostMapping
    public Map<String, Object> create(@RequestBody NewsInfo newsInfo, @RequestAttribute("user") User user) {
        System.out.println("接收到发布请求，当前登录用户：" + user);
        System.out.println("原始newsInfo数据：" + newsInfo);

        newsInfo.setCreateUserId(user.getId());
        if (newsInfo.getAuthor() == null || newsInfo.getAuthor().isBlank()) {
            // 优先设置 nickname，再用 username，最后用 "用户ID"
            String name = user.getNickname();
            if (name == null || name.isBlank()) {
                name = user.getUsername();
            }
            if (name == null || name.isBlank()) {
                name = "用户" + user.getId();
            }
            newsInfo.setAuthor(name);
        }

        System.out.println("补充作者后newsInfo：" + newsInfo);

        newsInfoService.create(newsInfo);

        Map<String, Object> res = new HashMap<>();
        res.put("message", "创建成功，等待审核");
        res.put("id", newsInfo.getId());
        return res;
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody NewsInfo newsInfo, @RequestAttribute("user") User user) {
        newsInfo.setId(id);
        newsInfoService.update(newsInfo, user);
        return "更新成功";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id, @RequestAttribute("user") User user) {
        newsInfoService.delete(id, user);
        return "删除成功";
    }

    @GetMapping("/{id:\\d+}")
    public NewsInfo getDetail(@PathVariable Long id,
                              @RequestHeader(value = "Client-Type", required = false) String clientType) {
        System.out.println("getDetail called with id = " + id + ", client = " + clientType);
        if (!"mobile".equalsIgnoreCase(clientType)) {
            System.out.println("增加浏览量：" + id);
            newsInfoService.incrementViewCount(id);
        }
        return newsInfoService.getById(id);
    }

    @GetMapping("/list")
    //安卓：
    //public Map<String, Object> list(NewsQueryDTO query, @RequestAttribute("user") @RequestBody(required = false) User user) {
    public Map<String, Object> list(NewsQueryDTO query, @RequestAttribute("user") User user) {
        if (Boolean.TRUE.equals(query.getOnlyMine())) {
            query.setAuthor(user.getUsername()  );
        }

        // 设置 endTime 为当天 23:59:59.999
        if (query.getEndTime() != null) {
            LocalDateTime endTime = query.getEndTime();
            query.setEndTime(endTime.withHour(23).withMinute(59).withSecond(59).withNano(999_000_000));
            System.out.println("StartTime: " + query.getStartTime());
            System.out.println("EndTime (corrected): " + query.getEndTime());
        }

        List<NewsInfo> list = newsInfoService.listByQuery(query);
        int total = newsInfoService.countByQuery(query);

        Map<String, Object> res = new HashMap<>();
        res.put("total", total);
        res.put("list", list);
        res.put("pageNum", query.getPageNum());
        res.put("pageSize", query.getPageSize());
        res.put("totalPages", (total + query.getPageSize() - 1) / query.getPageSize());

        return res;
    }

    @PostMapping("/audit")
    public String audit(@RequestBody Map<String, Object> body) {
        System.out.println("AUDIT 被 POST 调用了: " + body);
        Long id = Long.valueOf(body.get("id").toString());
        Integer status = Integer.valueOf(body.get("status").toString());
        String reason = (String) body.get("reason");

        newsInfoService.audit(id, status, reason);
        return "审核成功";
    }

    @PostMapping("/upload/{newsId}")
    public Map<String, Object> uploadAttachment(@PathVariable Long newsId,
                                                @RequestParam("file") MultipartFile file,
                                                @RequestParam(value = "type", required = false) String type) throws IOException {
        Map<String, Object> res = new HashMap<>();
        if (file.isEmpty()) {
            res.put("error", "文件为空");
            return res;
        }

        String uploadDir = "/Users/zqz/local/alliance/uploads/";//保存文件地址
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(uploadDir + fileName);
        file.transferTo(dest);

        String fileUrl = "/files/" + fileName;

        if ("cover".equals(type)) {
            NewsInfo newsInfo = new NewsInfo();
            newsInfo.setId(newsId);
            newsInfo.setNewsImage(fileUrl);
            newsInfoService.updateCover(newsInfo); // 新增专用方法避免权限校验
            res.put("fileUrl", fileUrl);
            res.put("message", "封面上传成功");
        } else {
            NewsAttachment attachment = new NewsAttachment();
            attachment.setNewsId(newsId);
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFileUrl(fileUrl);
            attachment.setUploadTime(java.time.LocalDateTime.now());

            newsInfoService.saveAttachments(newsId, List.of(attachment));
            res.put("fileUrl", fileUrl);
            res.put("message", "附件上传成功");
        }

        return res;
    }

    @GetMapping("/list/{newsId}")
    public List<NewsAttachment> listAttachments(@PathVariable Long newsId) {
        return newsInfoService.getAttachments(newsId);
    }

    @DeleteMapping("/attachment/{id}")
    public String deleteAttachment(@PathVariable Long id, @RequestAttribute("user") User user) {
        newsInfoService.deleteAttachment(id, user);
        return "附件删除成功";
    }

    @PostMapping("/top")
    public String setTop(@RequestParam Long id, @RequestParam Integer isTop, @RequestAttribute("user") User user) {
        if (user.getRole() != 1) {
            throw new RuntimeException("无权限设置置顶");
        }
        NewsInfo news = new NewsInfo();
        news.setId(id);
        news.setIsTop(isTop);
        newsInfoService.updateWithoutTime(news, user);   // 用 update 方法即可
        return "设置成功";
    }

    // 安卓调用：上传用户行为数据
    @PostMapping("/track")
    public void trackBehaviors(@RequestBody List<NewsTracker> trackers) {
        trackingService.processTrackers(trackers);
    }

    // Web 前端：获取所有新闻行为数据
    @GetMapping("/track")
    public List<NewsInfo> getAllTrackers() {
        return trackingService.getAllBehaviors();
    }

    @GetMapping("/getNews")
    public Map<String, Object> getNews(@RequestParam int page, @RequestParam int pageSize) {
        NewsQueryDTO query = new NewsQueryDTO();
        query.setPageNum(page);
        query.setPageSize(pageSize);
        List<NewsInfo> list = newsInfoService.listByQuery(query);
        int total = newsInfoService.countByQuery(query);

        Map<String, Object> res = new HashMap<>();
        res.put("total", total);
        res.put("list", list);
        return res;
    }

    @GetMapping
    public String newsRoot() {
        return "news root page";
    }
}