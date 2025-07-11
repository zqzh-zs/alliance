package com.neu.alliance.controller;

import com.neu.alliance.entity.*;
import com.neu.alliance.mapper.AgendaMapper;
import com.neu.alliance.mapper.GuestMapper;
import com.neu.alliance.mapper.MeetingMapper;
import com.neu.alliance.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/meeting")
public class MeetingController {
    private MeetingMapper meetingMapper;
    @Autowired
    private GuestMapper guestMapper;
    @Autowired
    private AgendaMapper agendaMapper;


    @Autowired
    public void setMeetingMapper(MeetingMapper meetingMapper) {
        this.meetingMapper = meetingMapper;
    }

    /**
     * 创建会议
     */
    @PostMapping("/create")
    public Response<String> createMeeting(@RequestBody Meeting meeting, @RequestParam("role") String role) {
        if (meeting == null) {
            return Response.error("参数不能为空");
        }

        // 设置会议状态
        if ("1".equals(role)) {
            meeting.setStatus(MeetingStatus.APPROVED);
        } else {
            meeting.setStatus(MeetingStatus.PENDING);
        }

        int result = meetingMapper.insertMeeting(meeting);

        if (result > 0) {
            return Response.success("会议创建成功");
        } else {
            return Response.error("会议创建失败");
        }
    }


    /**
     * 会议详情
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getMeetingDetail(@PathVariable int id) {
        Meeting meeting = meetingMapper.selectMeetingDetail(id);

        if (meeting == null) {
            return ResponseEntity.notFound().build();
        }

        // 确保图片URL不为空且格式正确
        if (meeting.getImageUrl() != null) {
            // 如果URL以/uploads开头，自动补全（针对旧数据）
            if (meeting.getImageUrl().startsWith("/uploads")) {
                meeting.setImageUrl("http://localhost:8080" + meeting.getImageUrl());
            }
            // 其他情况保持原样（假设已经是完整URL）
        }

        return ResponseEntity.ok(new Result(200, "获取成功", meeting));
    }
    /**
     * 查询审核通过的会议列表（带分页、筛选）
     * 关键字和创建人是前缀匹配，不用全模糊
     */
    @GetMapping("/approved")
    public Map<String, Object> getApprovedMeetings(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String organizer,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        int offset = (page - 1) * pageSize;

        List<Meeting> meetings = meetingMapper.getApprovedMeetings(type, keyword, organizer, startDate, endDate, offset, pageSize);
        int total = meetingMapper.countApprovedMeetings(type, keyword, organizer, startDate, endDate);

        Map<String, Object> data = new HashMap<>();
        data.put("list", meetings);
        data.put("total", total);

        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("data", data);

        return res;
    }
    @GetMapping("/search")
    public Map<String, Object> search(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String organizer,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        int offset = (page - 1) * pageSize;
        List<Meeting> meetings = meetingMapper.searchMeetings(type, keyword, organizer, startDate, endDate, offset, pageSize);
        for (Meeting meeting : meetings){
            List<Guest> guests = guestMapper.getGuestsByMeetingId(meeting.getId());
            meeting.setGuests(guests);
            List<AgendaItem> agendaItems = agendaMapper.getAgendaItemsByMeetingId(meeting.getId());
            meeting.setAgendaItems(agendaItems);
        }
        int total = meetingMapper.countMeetings(type, keyword, organizer, startDate, endDate);

        Map<String, Object> result = new HashMap<>();
        result.put("list", meetings);
        result.put("total", total);
        return result;
    }

    @GetMapping("/searchAll")
    public Map<String, Object> searchAll(
            @RequestParam(defaultValue = "SEMINAR")  MeetingType type
    ) {

        List<Meeting> meetings = meetingMapper.selectApprovedByType(type);
        for (Meeting meeting : meetings){
            List<Guest> guests = guestMapper.getGuestsByMeetingId(meeting.getId());
            meeting.setGuests(guests);
            List<AgendaItem> agendaItems = agendaMapper.getAgendaItemsByMeetingId(meeting.getId());
            meeting.setAgendaItems(agendaItems);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("list", meetings);
        result.put("total", meetings.size());
        return result;
    }

    @DeleteMapping("/search/{id}")
    public Map<String, Object> delete(@PathVariable int id) {
        meetingMapper.deleteById(id);
        return Map.of("code", 200, "msg", "删除成功");
    }
    @PostMapping("/approve/{id}")
    public Result approveMeeting(@PathVariable int id) {
        int rows = meetingMapper.updateMeetingStatus(id, MeetingStatus.APPROVED.getCode());
        if (rows > 0) {
            return new Result(200, "会议已通过", null);
        } else {
            return new Result(500, "会议不存在或状态更新失败", null);
        }
    }

    @PostMapping("/reject/{id}")
    public Result rejectMeeting(@PathVariable int id) {
        int rows = meetingMapper.updateMeetingStatus(id, MeetingStatus.REJECTED.getCode());
        if (rows > 0) {
            return new Result(200, "会议已驳回", null);
        } else {
            return new Result(500, "会议不存在或状态更新失败", null);
        }
    }
    @GetMapping("/searchuser")
    public Map<String, Object> searchMeeting(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam String organizer) {

        System.out.println("接收到参数: page=" + page + ", pageSize=" + pageSize + ", type=" + type + ", keyword=" + keyword
                + ", startDate=" + startDate + ", endDate=" + endDate + ", organizer=" + organizer);

        int offset = (page - 1) * pageSize;

        List<Meeting> meetings = meetingMapper.searchMeeting(organizer, type, keyword, startDate, endDate, offset, pageSize);
        int total = meetingMapper.countMeeting(organizer, type, keyword, startDate, endDate);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("list", meetings);
        result.put("total", total);

        return result;
    }
    @PutMapping("/update/{id}")
    public Response<?> updateMeeting(@PathVariable("id") int id, @RequestBody Meeting meeting) {
        System.out.println(meeting.getImageUrl());

        meeting.setId(id);  // 确保id正确
        int rows = meetingMapper.updateMeeting(meeting);
        if (rows > 0) {
            return Response.success("更新成功");
        } else {
            return Response.error("更新失败");
        }
    }

    static class Result {
        private int code;
        private String msg;
        private Object data;

        public Result(int code, String msg, Object data) {
            this.code = code;
            this.msg = msg;
            this.data = data;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    public Map<String, Object> searchAll(){
        List<Meeting> meetings = meetingMapper.selectAllApproved();
        for (Meeting meeting : meetings){
            List<Guest> guests = guestMapper.getGuestsByMeetingId(meeting.getId());
            meeting.setGuests(guests);
            List<AgendaItem> agendaItems = agendaMapper.getAgendaItemsByMeetingId(meeting.getId());
            meeting.setAgendaItems(agendaItems);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("list", meetings);
        result.put("total", meetings.size());
        return result;
    }

    @GetMapping("/mobile/search")
    public Map<String, Object> mobileSearch(
            @RequestParam int page,
            @RequestParam int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String organizer,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        int offset = (page - 1) * pageSize;
        List<Meeting> meetings = meetingMapper.searchMeetings(type, keyword, organizer, startDate, endDate, offset, pageSize);
        for (Meeting meeting : meetings){
            List<Guest> guests = guestMapper.getGuestsByMeetingId(meeting.getId());
            meeting.setGuests(guests);
            List<AgendaItem> agendaItems = agendaMapper.getAgendaItemsByMeetingId(meeting.getId());
            meeting.setAgendaItems(agendaItems);
        }
        int total = meetingMapper.countMeetings(type, keyword, organizer, startDate, endDate);

        Map<String, Object> result = new HashMap<>();
        result.put("list", meetings);
        result.put("total", total);
        return result;
    }
}
