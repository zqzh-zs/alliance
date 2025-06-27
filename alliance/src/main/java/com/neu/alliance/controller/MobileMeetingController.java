package com.neu.alliance.controller;

import com.neu.alliance.entity.MeetingListData;
import com.neu.alliance.entity.MeetingType;
import com.neu.alliance.entity.Response;
import com.neu.alliance.service.MeetingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}