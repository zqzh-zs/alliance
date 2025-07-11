package com.neu.alliance.controller;

import com.neu.alliance.entity.*;
import com.neu.alliance.service.MeetingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mobile/meeting")
public class MobileMeetingController {
    private final MeetingService meetingService;

    public MobileMeetingController(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    @GetMapping("/listByType")
    public Response<MeetingListData> getMeetingsByType(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam MeetingType type
    ) {
        MeetingListData data = meetingService.getApprovedMeetingsByType(type, page, pageSize);
        return Response.success(data);
    }

    @PostMapping("/submitAttendance")
    public String submitAttendance(@RequestBody AttendanceForm attendanceForm){
        if(meetingService.submitAttendanceForm(attendanceForm)){
            return "提交成功";
        }else{
            return "提交失败";
        }
    }


    @PostMapping("/track")
    public String track(@RequestBody List<MeetingTracker> meetingTracker){
        if(meetingService.track(meetingTracker)){
            return "提交成功";
        }else{
            return "提交失败";
        }
    }
}